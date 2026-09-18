package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.model.SongLyricLine
import com.example.model.TranslationResult

object LyricExporter {

    fun generateSrt(result: TranslationResult): String {
        val sb = StringBuilder()
        result.lines.forEachIndexed { index, line ->
            val cueNumber = index + 1
            val startTime = formatSrtTimestamp(line.startTimeMs)
            val endTime = formatSrtTimestamp(line.endTimeMs)

            sb.append(cueNumber).append("\n")
            sb.append(startTime).append(" --> ").append(endTime).append("\n")
            sb.append(line.myanmarText).append("\n\n")
        }
        return sb.toString().trim()
    }

    fun generateMyanmarTxt(result: TranslationResult): String {
        val sb = StringBuilder()
        sb.append("=== ${result.songTitle} (Myanmar Translation) ===\n")
        sb.append("Detected Language: ${result.detectedLanguage}\n")
        sb.append("Style: ${result.styleUsed.displayName}\n")
        sb.append("Translated by Myanmar Song Translator AI\n\n")

        result.lines.forEach { line ->
            sb.append("[${line.startFormatted}] ${line.myanmarText}\n")
        }
        return sb.toString().trim()
    }

    fun generateBilingualTxt(result: TranslationResult): String {
        val sb = StringBuilder()
        sb.append("=== ${result.songTitle} (Bilingual Lyrics) ===\n")
        sb.append("Original Language: ${result.detectedLanguage}\n")
        sb.append("Style: ${result.styleUsed.displayName}\n")
        sb.append("--------------------------------------------------\n\n")

        result.lines.forEach { line ->
            sb.append("[${line.startFormatted} - ${line.endFormatted}]\n")
            sb.append("ORIGINAL: ${line.originalText}\n")
            sb.append("MYANMAR : ${line.myanmarText}\n\n")
        }
        return sb.toString().trim()
    }

    fun copyToClipboard(context: Context, text: String, label: String = "Myanmar Lyrics") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun shareText(context: Context, title: String, content: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Lyrics via")
        context.startActivity(shareIntent)
    }

    private fun formatSrtTimestamp(ms: Long): String {
        val totalSec = ms / 1000
        val millis = ms % 1000
        val hours = totalSec / 3600
        val minutes = (totalSec % 3600) / 60
        val seconds = totalSec % 60
        return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, millis)
    }
}
