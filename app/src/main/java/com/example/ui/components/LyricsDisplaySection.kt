package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.SongLyricLine
import com.example.model.TranslationResult
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.KaraokeActive
import com.example.ui.theme.MyanmarGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.viewmodel.LyricViewMode

@Composable
fun LyricsDisplaySection(
    translationResult: TranslationResult?,
    activeLineIndex: Int,
    viewMode: LyricViewMode,
    onViewModeChange: (LyricViewMode) -> Unit,
    onLyricLineClick: (SongLyricLine) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Automatically scroll to active line
    LaunchedEffect(activeLineIndex) {
        if (activeLineIndex in 0 until (translationResult?.lines?.size ?: 0)) {
            val targetIndex = (activeLineIndex - 1).coerceAtLeast(0)
            listState.animateScrollToItem(targetIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF111422))
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Bar: View Mode Switch (Side-by-Side, Myanmar, Original)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Synchronized Lyrics",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Segmented Tabs
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val tabs = listOf(
                        Triple(LyricViewMode.BILINGUAL, stringResource(R.string.view_bilingual), "side_by_side"),
                        Triple(LyricViewMode.MYANMAR_ONLY, stringResource(R.string.view_myanmar_only), "myanmar_only"),
                        Triple(LyricViewMode.ORIGINAL_ONLY, stringResource(R.string.view_original_only), "original_only")
                    )

                    tabs.forEach { (mode, label, tag) ->
                        val isSelected = viewMode == mode
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) NeonViolet.copy(alpha = 0.3f) else Color(0xFF1B2032),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonViolet else Color(0x33475569)
                            ),
                            modifier = Modifier
                                .testTag("tab_$tag")
                                .clickable { onViewModeChange(mode) }
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Column Headers in Side-by-Side mode
            if (viewMode == LyricViewMode.BILINGUAL) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF181D30))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.col_original),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.col_myanmar),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MyanmarGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        modifier = Modifier.weight(1.2f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Lyric Lines LazyColumn
            val lines = translationResult?.lines.orEmpty()
            if (lines.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Upload an audio file or select a sample to view lyrics.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF64748B)
                        )
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(lines) { index, line ->
                        val isActive = index == activeLineIndex
                        LyricLineCard(
                            line = line,
                            isActive = isActive,
                            viewMode = viewMode,
                            onClick = { onLyricLineClick(line) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LyricLineCard(
    line: SongLyricLine,
    isActive: Boolean,
    viewMode: LyricViewMode,
    onClick: () -> Unit
) {
    val cardBg by animateColorAsState(
        targetValue = if (isActive) Color(0x3838BDF8) else Color(0xFF161A2C),
        animationSpec = tween(300),
        label = "bg"
    )

    val borderBrush = if (isActive) {
        Brush.horizontalGradient(listOf(NeonCyan, NeonViolet))
    } else {
        Brush.horizontalGradient(listOf(Color(0x22475569), Color(0x22475569)))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(cardBg)
            .border(if (isActive) 1.5.dp else 1.dp, borderBrush, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 9.dp)
            .testTag("lyric_line_${line.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header of line: Timestamp and active pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${line.startFormatted} - ${line.endFormatted}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isActive) NeonCyan else Color(0xFF64748B),
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                )

                if (isActive) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonCyan.copy(alpha = 0.25f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NeonCyan)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Playing",
                            color = NeonCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            when (viewMode) {
                LyricViewMode.BILINGUAL -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Original
                        Text(
                            text = line.originalText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isActive) Color.White else Color(0xFFCBD5E1),
                                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Myanmar
                        Text(
                            text = line.myanmarText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isActive) MyanmarGold else Color(0xFFF1F5F9),
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                lineHeight = 21.sp
                            ),
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                }
                LyricViewMode.MYANMAR_ONLY -> {
                    Text(
                        text = line.myanmarText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (isActive) MyanmarGold else Color(0xFFF1F5F9),
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    )
                }
                LyricViewMode.ORIGINAL_ONLY -> {
                    Text(
                        text = line.originalText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (isActive) Color.White else Color(0xFFCBD5E1),
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }
    }
}
