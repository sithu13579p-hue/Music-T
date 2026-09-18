package com.example.model

enum class TranslationStyle(val displayName: String, val myanmarLabel: String, val promptInstruction: String) {
    NATURAL(
        displayName = "Natural Myanmar",
        myanmarLabel = "သဘာဝကျသော မြန်မာဘာသာပြန်",
        promptInstruction = "Translate into natural, flowing Myanmar (Burmese) phrasing that sounds like a native speaker and feels melodic."
    ),
    LITERAL(
        displayName = "Literal Translation",
        myanmarLabel = "တိုက်ရိုက် စာသားအတိုင်း ဘာသာပြန်",
        promptInstruction = "Translate literally word-by-word into Myanmar (Burmese) while maintaining grammatical clarity."
    ),
    POETIC(
        displayName = "Emotional / Poetic Myanmar",
        myanmarLabel = "ကဗျာဆန်ဆန် ခံစားချက်ပါသော မြန်မာဘာသာပြန်",
        promptInstruction = "Translate into deeply emotional, poetic, and artistic Myanmar (Burmese) song lyrics that evoke strong sentiments."
    ),
    EASY(
        displayName = "Easy-to-understand Myanmar",
        myanmarLabel = "နားလည်လွယ် ရှင်းလင်းသော မြန်မာစကားပြေ",
        promptInstruction = "Translate into simple, modern, colloquial, and crystal-clear everyday Myanmar (Burmese)."
    )
}

data class SongLyricLine(
    val id: Int,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val originalText: String,
    val myanmarText: String,
    val notes: String? = null
) {
    fun formatTimestamp(ms: Long): String {
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        return String.format("%02d:%02d", min, sec)
    }

    val startFormatted: String get() = formatTimestamp(startTimeMs)
    val endFormatted: String get() = formatTimestamp(endTimeMs)
}

data class SongInfo(
    val title: String,
    val fileName: String,
    val fileUri: String? = null,
    val durationMs: Long = 0,
    val format: String = "MP3",
    val sizeFormatted: String = "4.2 MB",
    val detectedLanguage: String = "Unknown",
    val isSample: Boolean = false
)

enum class AIProcessStep(val title: String, val subtitle: String, val stepNumber: Int) {
    ANALYZING("Analyzing Audio", "Separating background instruments from vocal frequency...", 1),
    TRANSCRIBING("Transcribing Vocals", "Detecting lyric phrases, chorus repetitions, and millisecond timestamps...", 2),
    TRANSLATING("Translating to Myanmar", "Translating into natural Myanmar Unicode preserving rhythm and emotion...", 3),
    SYNCHRONIZING("Synchronizing Subtitles", "Aligning bilingual lines with playback cues...", 4)
}

sealed interface ProcessingState {
    object Idle : ProcessingState
    data class InProgress(val currentStep: AIProcessStep, val progress: Float) : ProcessingState
    data class Success(val result: TranslationResult) : ProcessingState
    data class Failure(val errorMsg: String, val recoverable: Boolean = true) : ProcessingState
}

data class TranslationResult(
    val songTitle: String,
    val detectedLanguage: String,
    val confidence: Float,
    val styleUsed: TranslationStyle,
    val keepEmotion: Boolean,
    val lines: List<SongLyricLine>
)
