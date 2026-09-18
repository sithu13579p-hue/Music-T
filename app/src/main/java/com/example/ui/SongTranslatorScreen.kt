package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ProcessingState
import com.example.ui.components.AIProcessingProgress
import com.example.ui.components.AppHeader
import com.example.ui.components.AudioUploadSection
import com.example.ui.components.ExportActionsSection
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.components.LyricsDisplaySection
import com.example.ui.components.MusicPlayerBar
import com.example.ui.components.TranslationOptionsSection
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.viewmodel.SongTranslatorViewModel

@Composable
fun SongTranslatorScreen(
    viewModel: SongTranslatorViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSong by viewModel.selectedSong.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val keepEmotion by viewModel.keepEmotion.collectAsState()
    val processingState by viewModel.processingState.collectAsState()
    val currentTranslation by viewModel.currentTranslation.collectAsState()
    val lyricViewMode by viewModel.lyricViewMode.collectAsState()
    val activeLyricIndex by viewModel.activeLyricIndex.collectAsState()

    val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
    val currentPositionMs by viewModel.playerManager.currentPositionMs.collectAsState()
    val durationMs by viewModel.playerManager.durationMs.collectAsState()
    val volume by viewModel.playerManager.volume.collectAsState()

    val savedTranslations by viewModel.savedTranslations.collectAsState()
    var showHistorySheet by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // Header with App Title, Logo, and Privacy/Copyright Badge
            AppHeader(
                onOpenHistory = { showHistorySheet = true }
            )

            // Audio Upload & Sample Selection Section
            AudioUploadSection(
                currentSong = selectedSong,
                detectedLanguage = currentTranslation?.detectedLanguage,
                onAudioFilePicked = { uri -> viewModel.onAudioUriSelected(uri) },
                onSampleChosen = { sample -> viewModel.onSelectSample(sample) }
            )

            // Translation Options: Styles and Keep Emotion
            TranslationOptionsSection(
                selectedStyle = selectedStyle,
                keepEmotion = keepEmotion,
                onStyleSelected = { style -> viewModel.setStyle(style) },
                onKeepEmotionChanged = { keep -> viewModel.setKeepEmotion(keep) },
                onTranslateClicked = { viewModel.startTranslation() },
                isProcessing = processingState is ProcessingState.InProgress
            )

            // Animated AI Progress Indicator
            AnimatedVisibility(visible = processingState is ProcessingState.InProgress) {
                if (processingState is ProcessingState.InProgress) {
                    val state = processingState as ProcessingState.InProgress
                    AIProcessingProgress(
                        currentStep = state.currentStep,
                        progressFraction = state.progress
                    )
                }
            }

            // Friendly Error State Card
            AnimatedVisibility(visible = processingState is ProcessingState.Failure) {
                if (processingState is ProcessingState.Failure) {
                    val error = processingState as ProcessingState.Failure
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF26141D))
                            .border(1.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Error",
                                tint = NeonPink,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Translation Notice",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = error.errorMsg,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFFECDD3),
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.startTranslation() },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("retry_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.retry_action),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Audio Player Bar
            MusicPlayerBar(
                currentSong = selectedSong,
                isPlaying = isPlaying,
                currentPositionMs = currentPositionMs,
                durationMs = durationMs,
                volume = volume,
                onTogglePlayPause = { viewModel.playerManager.togglePlayPause() },
                onSeek = { pos -> viewModel.playerManager.seekTo(pos) },
                onSkipForward = { viewModel.playerManager.skipForward5s() },
                onSkipBackward = { viewModel.playerManager.skipBackward5s() },
                onVolumeChange = { vol -> viewModel.playerManager.setVolume(vol) }
            )

            // Synchronized Lyrics Display
            LyricsDisplaySection(
                translationResult = currentTranslation,
                activeLineIndex = activeLyricIndex,
                viewMode = lyricViewMode,
                onViewModeChange = { mode -> viewModel.setLyricViewMode(mode) },
                onLyricLineClick = { line -> viewModel.onLyricLineClicked(line) }
            )

            // Export Actions: Copy, SRT, TXT, Bilingual
            ExportActionsSection(
                result = currentTranslation
            )

            Spacer(modifier = Modifier.height(28.dp))
        }

        // Saved History Sheet
        if (showHistorySheet) {
            HistoryBottomSheet(
                savedTranslations = savedTranslations,
                onSelectSong = { entity -> viewModel.loadSavedSong(entity) },
                onDismiss = { showHistorySheet = false }
            )
        }
    }
}
