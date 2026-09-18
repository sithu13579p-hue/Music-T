package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.TranslationResult
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.MyanmarGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.util.LyricExporter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExportActionsSection(
    result: TranslationResult?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF131728))
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        tint = NeonViolet,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Export Subtitles & Lyrics",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = NeonCyan.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "SRT / TXT",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Export Buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // 1. Copy Myanmar Translation
                ExportActionTile(
                    title = stringResource(R.string.export_copy_myanmar),
                    subtitle = "Copies clean Myanmar Unicode lyrics directly to clipboard",
                    icon = Icons.Default.ContentCopy,
                    accentColor = MyanmarGold,
                    tag = "copy_myanmar_button",
                    onClick = {
                        if (result != null) {
                            val text = LyricExporter.generateMyanmarTxt(result)
                            LyricExporter.copyToClipboard(context, text, "${result.songTitle} Myanmar Lyrics")
                        }
                    }
                )

                // 2. Download SRT Subtitles
                ExportActionTile(
                    title = stringResource(R.string.export_download_srt),
                    subtitle = "Standard SubRip subtitle file with synced millisecond timestamps",
                    icon = Icons.Default.Subtitles,
                    accentColor = NeonCyan,
                    tag = "download_srt_button",
                    onClick = {
                        if (result != null) {
                            val srtContent = LyricExporter.generateSrt(result)
                            LyricExporter.shareText(
                                context,
                                "${result.songTitle}_Myanmar_Subtitles.srt",
                                srtContent
                            )
                        }
                    }
                )

                // 3. Download TXT (Myanmar Only)
                ExportActionTile(
                    title = stringResource(R.string.export_download_txt),
                    subtitle = "Clean text document with song metadata and Myanmar translation",
                    icon = Icons.Default.Description,
                    accentColor = NeonViolet,
                    tag = "download_txt_button",
                    onClick = {
                        if (result != null) {
                            val txt = LyricExporter.generateMyanmarTxt(result)
                            LyricExporter.shareText(
                                context,
                                "${result.songTitle}_Myanmar.txt",
                                txt
                            )
                        }
                    }
                )

                // 4. Download original + Myanmar translation
                ExportActionTile(
                    title = stringResource(R.string.export_download_both),
                    subtitle = "Bilingual side-by-side lyrics document with timestamps",
                    icon = Icons.Default.Translate,
                    accentColor = NeonPink,
                    tag = "download_both_button",
                    onClick = {
                        if (result != null) {
                            val bilingualTxt = LyricExporter.generateBilingualTxt(result)
                            LyricExporter.shareText(
                                context,
                                "${result.songTitle}_Bilingual_Lyrics.txt",
                                bilingualTxt
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ExportActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF191F34))
            .border(1.dp, Color(0x33475569), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            )
        }
    }
}
