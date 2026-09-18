package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward5
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SongInfo
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet

@Composable
fun MusicPlayerBar(
    currentSong: SongInfo?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    volume: Float,
    onTogglePlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onSkipForward: () -> Unit,
    onSkipBackward: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var showVolumeSlider by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 4f, targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 16f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 8f, targetValue = 24f,
        animationSpec = infiniteRepeatable(tween(280, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    val wave4 by infiniteTransition.animateFloat(
        initialValue = 20f, targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(480, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF191D30), Color(0xFF111424))
                )
            )
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Track Info & Visualizer Waves
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentSong?.title ?: "No Song Selected",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = "${currentSong?.fileName ?: ""} • ${currentSong?.format ?: "MP3"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }

                // Dancing Audio Waves Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .height(24.dp)
                        .padding(end = 4.dp)
                ) {
                    val heights = if (isPlaying) listOf(wave1, wave2, wave3, wave4) else listOf(6f, 6f, 6f, 6f)
                    val colors = listOf(NeonViolet, NeonCyan, NeonPink, NeonCyan)
                    heights.forEachIndexed { idx, h ->
                        Box(
                            modifier = Modifier
                                .width(3.5.dp)
                                .height(h.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(colors[idx])
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Seekbar
            val sliderValue = if (durationMs > 0) {
                (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
            } else 0f

            Slider(
                value = sliderValue,
                onValueChange = { fraction ->
                    onSeek((fraction * durationMs).toLong())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .testTag("audio_seekbar"),
                colors = SliderDefaults.colors(
                    thumbColor = NeonCyan,
                    activeTrackColor = NeonViolet,
                    inactiveTrackColor = Color(0x33475569)
                )
            )

            // Timestamps: Elapsed & Remaining / Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMs(currentPositionMs),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = formatMs(durationMs),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Controls Row: Replay5, Play/Pause, Forward5, Volume
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Volume Toggle Button
                IconButton(
                    onClick = { showVolumeSlider = !showVolumeSlider },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = when {
                            volume == 0f -> Icons.Default.VolumeMute
                            volume < 0.5f -> Icons.Default.VolumeDown
                            else -> Icons.Default.VolumeUp
                        },
                        contentDescription = "Volume Control",
                        tint = if (showVolumeSlider) NeonCyan else Color(0xFF94A3B8),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Center Controls: Skip -5s, Play/Pause, Skip +5s
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    IconButton(
                        onClick = onSkipBackward,
                        modifier = Modifier
                            .testTag("skip_backward_button")
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x22334155))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay5,
                            contentDescription = "Skip Backward 5s",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Main Play/Pause Button
                    Box(
                        modifier = Modifier
                            .testTag("play_pause_button")
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(NeonViolet, NeonCyan))
                            )
                            .clickable { onTogglePlayPause() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    IconButton(
                        onClick = onSkipForward,
                        modifier = Modifier
                            .testTag("skip_forward_button")
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x22334155))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward5,
                            contentDescription = "Skip Forward 5s",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Placeholder for symmetry
                Spacer(modifier = Modifier.size(44.dp))
            }

            // Expandable Volume Slider
            AnimatedVisibility(visible = showVolumeSlider) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeDown,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Slider(
                        value = volume,
                        onValueChange = onVolumeChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("volume_slider"),
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = Color(0x33475569)
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    val min = totalSec / 60
    val sec = totalSec % 60
    return String.format("%02d:%02d", min, sec)
}
