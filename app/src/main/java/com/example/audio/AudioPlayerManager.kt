package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.model.SongInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sin

class AudioPlayerManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var mediaPlayer: MediaPlayer? = null
    private var positionTickerJob: Job? = null
    private var synthAudioJob: Job? = null
    private var synthTrack: AudioTrack? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(45000L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _volume = MutableStateFlow(0.85f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private var currentSong: SongInfo? = null
    private var isUsingSynth = false

    init {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        val currentSysVol = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 8
        val maxSysVol = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15
        _volume.value = (currentSysVol.toFloat() / maxSysVol.toFloat()).coerceIn(0.1f, 1f)
    }

    fun loadSong(song: SongInfo) {
        stop()
        currentSong = song
        _currentPositionMs.value = 0L
        _durationMs.value = if (song.durationMs > 0) song.durationMs else 45000L

        if (song.fileUri != null) {
            try {
                isUsingSynth = false
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, Uri.parse(song.fileUri))
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    prepareAsync()
                    setOnPreparedListener { mp ->
                        val dur = mp.duration.toLong()
                        if (dur > 0) _durationMs.value = dur
                        setVolume(_volume.value, _volume.value)
                    }
                    setOnCompletionListener {
                        _isPlaying.value = false
                        _currentPositionMs.value = 0L
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.w("AudioPlayerManager", "MediaPlayer error: $what, $extra")
                        isUsingSynth = true
                        true
                    }
                }
            } catch (e: Exception) {
                Log.e("AudioPlayerManager", "Failed to init MediaPlayer with URI", e)
                isUsingSynth = true
            }
        } else {
            isUsingSynth = true
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        if (isUsingSynth || mediaPlayer == null) {
            startSynthPlayback()
        } else {
            try {
                mediaPlayer?.let { mp ->
                    mp.setVolume(_volume.value, _volume.value)
                    mp.start()
                    _isPlaying.value = true
                    startPositionTicker()
                } ?: run {
                    startSynthPlayback()
                }
            } catch (e: Exception) {
                Log.e("AudioPlayerManager", "Play failed, falling back to synth", e)
                startSynthPlayback()
            }
        }
    }

    fun pause() {
        _isPlaying.value = false
        positionTickerJob?.cancel()
        positionTickerJob = null

        synthAudioJob?.cancel()
        synthAudioJob = null
        try {
            synthTrack?.pause()
        } catch (_: Exception) {}

        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: Exception) {
            Log.w("AudioPlayerManager", "Error pausing MediaPlayer", e)
        }
    }

    fun seekTo(positionMs: Long) {
        val clamped = positionMs.coerceIn(0L, _durationMs.value)
        _currentPositionMs.value = clamped

        if (!isUsingSynth && mediaPlayer != null) {
            try {
                mediaPlayer?.seekTo(clamped.toInt())
            } catch (e: Exception) {
                Log.w("AudioPlayerManager", "Error seeking MediaPlayer", e)
            }
        }
    }

    fun skipForward5s() {
        seekTo(_currentPositionMs.value + 5000L)
    }

    fun skipBackward5s() {
        seekTo(_currentPositionMs.value - 5000L)
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _volume.value = clamped
        try {
            mediaPlayer?.setVolume(clamped, clamped)
            synthTrack?.setVolume(clamped)
        } catch (e: Exception) {
            Log.w("AudioPlayerManager", "Error setting volume", e)
        }
    }

    private fun startPositionTicker() {
        positionTickerJob?.cancel()
        positionTickerJob = scope.launch {
            while (isActive && _isPlaying.value) {
                if (!isUsingSynth && mediaPlayer != null) {
                    try {
                        val current = mediaPlayer?.currentPosition?.toLong() ?: _currentPositionMs.value
                        _currentPositionMs.value = current
                    } catch (_: Exception) {}
                }
                delay(100)
            }
        }
    }

    private fun startSynthPlayback() {
        _isPlaying.value = true
        startPositionTicker()

        synthAudioJob?.cancel()
        synthAudioJob = scope.launch(Dispatchers.Default) {
            val sampleRate = 22050
            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            try {
                synthTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                synthTrack?.setVolume(_volume.value)
                synthTrack?.play()

                // Pleasant chord notes for melody demo (C4, E4, G4, A4, B4, C5)
                val chordFrequencies = doubleArrayOf(261.63, 329.63, 392.00, 440.00, 493.88, 523.25)
                val bufferSize = 2205 // ~100ms chunks
                val buffer = ShortArray(bufferSize)
                var phase = 0.0

                while (isActive && _isPlaying.value) {
                    val currentPos = _currentPositionMs.value
                    if (currentPos >= _durationMs.value) {
                        withContext(Dispatchers.Main) {
                            _isPlaying.value = false
                            _currentPositionMs.value = 0L
                        }
                        break
                    }

                    // Advance position by ~100ms
                    withContext(Dispatchers.Main) {
                        _currentPositionMs.value = (currentPos + 100L).coerceAtMost(_durationMs.value)
                    }

                    // Generate gentle acoustic ambient melodic tone
                    val noteIndex = ((currentPos / 1500) % chordFrequencies.size).toInt()
                    val freq = chordFrequencies[noteIndex]
                    val decay = 1.0 - ((currentPos % 1500).toDouble() / 1500.0) * 0.4

                    for (i in 0 until bufferSize) {
                        val sample = (sin(phase) * 12000 * decay * _volume.value).toInt().toShort()
                        buffer[i] = sample
                        phase += 2.0 * Math.PI * freq / sampleRate
                        if (phase > 2.0 * Math.PI) phase -= 2.0 * Math.PI
                    }

                    synthTrack?.write(buffer, 0, bufferSize)
                    delay(95)
                }
            } catch (e: Exception) {
                Log.w("AudioPlayerManager", "Synth audio error", e)
                // If audio output fails, timer ticker continues silently
                while (isActive && _isPlaying.value) {
                    delay(100)
                    withContext(Dispatchers.Main) {
                        val nxt = _currentPositionMs.value + 100L
                        if (nxt >= _durationMs.value) {
                            _isPlaying.value = false
                            _currentPositionMs.value = 0L
                        } else {
                            _currentPositionMs.value = nxt
                        }
                    }
                }
            }
        }
    }

    fun stop() {
        pause()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null

        try {
            synthTrack?.stop()
            synthTrack?.release()
        } catch (_: Exception) {}
        synthTrack = null
    }
}
