package com.example.viewmodel

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerManager
import com.example.data.SampleSongData
import com.example.db.AppDatabase
import com.example.db.SongTranslationEntity
import com.example.model.AIProcessStep
import com.example.model.ProcessingState
import com.example.model.SongInfo
import com.example.model.SongLyricLine
import com.example.model.TranslationResult
import com.example.model.TranslationStyle
import com.example.network.GeminiSongService
import com.example.util.LyricExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

enum class LyricViewMode {
    BILINGUAL,
    MYANMAR_ONLY,
    ORIGINAL_ONLY
}

class SongTranslatorViewModel(application: Application) : AndroidViewModel(application) {

    private val geminiService = GeminiSongService(application)
    val playerManager = AudioPlayerManager(application)
    private val db = AppDatabase.getInstance(application)
    private val dao = db.songTranslationDao()

    private val _selectedSong = MutableStateFlow<SongInfo?>(SampleSongData.sampleSong1)
    val selectedSong: StateFlow<SongInfo?> = _selectedSong.asStateFlow()

    private val _selectedStyle = MutableStateFlow(TranslationStyle.NATURAL)
    val selectedStyle: StateFlow<TranslationStyle> = _selectedStyle.asStateFlow()

    private val _keepEmotion = MutableStateFlow(true)
    val keepEmotion: StateFlow<Boolean> = _keepEmotion.asStateFlow()

    private val _processingState = MutableStateFlow<ProcessingState>(ProcessingState.Idle)
    val processingState: StateFlow<ProcessingState> = _processingState.asStateFlow()

    private val _currentTranslation = MutableStateFlow<TranslationResult?>(null)
    val currentTranslation: StateFlow<TranslationResult?> = _currentTranslation.asStateFlow()

    private val _lyricViewMode = MutableStateFlow(LyricViewMode.BILINGUAL)
    val lyricViewMode: StateFlow<LyricViewMode> = _lyricViewMode.asStateFlow()

    val savedTranslations = dao.getAllTranslations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Derive the currently playing lyric line index
    val activeLyricIndex: StateFlow<Int> = combine(
        playerManager.currentPositionMs,
        _currentTranslation
    ) { posMs, translation ->
        if (translation == null || translation.lines.isEmpty()) {
            -1
        } else {
            val idx = translation.lines.indexOfFirst { line ->
                posMs in line.startTimeMs..line.endTimeMs
            }
            if (idx >= 0) {
                idx
            } else {
                // Find the latest line whose startTime is <= current position
                val latest = translation.lines.indexOfLast { it.startTimeMs <= posMs }
                if (latest >= 0) latest else 0
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), -1)

    init {
        // Pre-load default sample
        loadInitialSample(SampleSongData.sampleSong1)
    }

    private fun loadInitialSample(sample: SongInfo) {
        _selectedSong.value = sample
        playerManager.loadSong(sample)
        val initialResult = SampleSongData.getSampleResult(sample, _selectedStyle.value, _keepEmotion.value)
        _currentTranslation.value = initialResult
    }

    fun onSelectSample(sample: SongInfo) {
        playerManager.stop()
        _selectedSong.value = sample
        playerManager.loadSong(sample)
        // Automatically start translation for this sample
        startTranslation()
    }

    fun onAudioUriSelected(uri: Uri) {
        playerManager.stop()
        val context = getApplication<Application>()
        var fileName = "user_audio.mp3"
        var fileSize = 0L

        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: fileName
                    if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                }
            }
        } catch (e: Exception) {
            Log.e("SongTranslatorVM", "Failed to query audio file metadata", e)
        }

        val extension = fileName.substringAfterLast('.', "mp3").uppercase()
        val sizeMb = String.format("%.1f MB", (fileSize.toFloat() / (1024 * 1024)).coerceAtLeast(0.1f))

        val song = SongInfo(
            title = fileName.substringBeforeLast('.'),
            fileName = fileName,
            fileUri = uri.toString(),
            durationMs = 60000L,
            format = extension,
            sizeFormatted = sizeMb,
            detectedLanguage = "Detecting...",
            isSample = false
        )

        _selectedSong.value = song
        playerManager.loadSong(song)
        startTranslation()
    }

    fun setStyle(style: TranslationStyle) {
        _selectedStyle.value = style
        if (_currentTranslation.value != null) {
            startTranslation()
        }
    }

    fun setKeepEmotion(keep: Boolean) {
        _keepEmotion.value = keep
        if (_currentTranslation.value != null) {
            startTranslation()
        }
    }

    fun setLyricViewMode(mode: LyricViewMode) {
        _lyricViewMode.value = mode
    }

    fun startTranslation() {
        val song = _selectedSong.value ?: return

        viewModelScope.launch {
            _processingState.value = ProcessingState.InProgress(AIProcessStep.ANALYZING, 0.15f)

            val result = geminiService.translateSong(
                songInfo = song,
                style = _selectedStyle.value,
                keepEmotion = _keepEmotion.value
            ) { stepIndex ->
                val step = when (stepIndex) {
                    0 -> AIProcessStep.ANALYZING
                    1 -> AIProcessStep.TRANSCRIBING
                    2 -> AIProcessStep.TRANSLATING
                    else -> AIProcessStep.SYNCHRONIZING
                }
                val progress = when (stepIndex) {
                    0 -> 0.25f
                    1 -> 0.50f
                    2 -> 0.75f
                    else -> 0.95f
                }
                _processingState.value = ProcessingState.InProgress(step, progress)
            }

            result.fold(
                onSuccess = { translationResult ->
                    _currentTranslation.value = translationResult
                    _processingState.value = ProcessingState.Success(translationResult)
                    // Auto-save to private local Room database
                    saveToDatabase(song, translationResult)
                },
                onFailure = { error ->
                    _processingState.value = ProcessingState.Failure(
                        errorMsg = error.localizedMessage ?: "Failed to process audio with AI. Please check network or retry.",
                        recoverable = true
                    )
                }
            )
        }
    }

    private fun saveToDatabase(song: SongInfo, translation: TranslationResult) {
        viewModelScope.launch {
            try {
                val linesArray = JSONArray()
                translation.lines.forEach { line ->
                    val obj = JSONObject().apply {
                        put("id", line.id)
                        put("startTimeMs", line.startTimeMs)
                        put("endTimeMs", line.endTimeMs)
                        put("originalText", line.originalText)
                        put("myanmarText", line.myanmarText)
                    }
                    linesArray.put(obj)
                }

                val entity = SongTranslationEntity(
                    title = translation.songTitle,
                    fileName = song.fileName,
                    fileUri = song.fileUri,
                    detectedLanguage = translation.detectedLanguage,
                    translationStyle = translation.styleUsed.displayName,
                    keepEmotion = translation.keepEmotion,
                    lyricsJson = linesArray.toString()
                )
                dao.insertTranslation(entity)
            } catch (e: Exception) {
                Log.w("SongTranslatorVM", "Failed to cache translation locally", e)
            }
        }
    }

    fun loadSavedSong(entity: SongTranslationEntity) {
        try {
            val linesArray = JSONArray(entity.lyricsJson)
            val lines = mutableListOf<SongLyricLine>()
            for (i in 0 until linesArray.length()) {
                val obj = linesArray.getJSONObject(i)
                lines.add(
                    SongLyricLine(
                        id = obj.optInt("id", i + 1),
                        startTimeMs = obj.optLong("startTimeMs", 0L),
                        endTimeMs = obj.optLong("endTimeMs", 4000L),
                        originalText = obj.optString("originalText", ""),
                        myanmarText = obj.optString("myanmarText", "")
                    )
                )
            }

            val style = TranslationStyle.values().find { it.displayName == entity.translationStyle }
                ?: TranslationStyle.NATURAL

            val result = TranslationResult(
                songTitle = entity.title,
                detectedLanguage = entity.detectedLanguage,
                confidence = 0.95f,
                styleUsed = style,
                keepEmotion = entity.keepEmotion,
                lines = lines
            )

            val song = SongInfo(
                title = entity.title,
                fileName = entity.fileName,
                fileUri = entity.fileUri,
                durationMs = lines.lastOrNull()?.endTimeMs ?: 45000L,
                detectedLanguage = entity.detectedLanguage,
                isSample = entity.fileUri == null
            )

            _selectedSong.value = song
            _currentTranslation.value = result
            _processingState.value = ProcessingState.Success(result)
            playerManager.loadSong(song)
        } catch (e: Exception) {
            Log.e("SongTranslatorVM", "Failed to parse saved translation", e)
        }
    }

    fun onLyricLineClicked(line: SongLyricLine) {
        playerManager.seekTo(line.startTimeMs)
        if (!playerManager.isPlaying.value) {
            playerManager.play()
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.stop()
    }
}
