package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.TranslationStyle
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet

@Composable
fun TranslationOptionsSection(
    selectedStyle: TranslationStyle,
    keepEmotion: Boolean,
    onStyleSelected: (TranslationStyle) -> Unit,
    onKeepEmotionChanged: (Boolean) -> Unit,
    onTranslateClicked: () -> Unit,
    isProcessing: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF14192A))
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
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = NeonViolet,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Translation Options",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NeonViolet.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Burmese Unicode",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4 Style Options Grid / Cards
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TranslationStyle.values().forEach { style ->
                    val isSelected = style == selectedStyle
                    val bgBrush = if (isSelected) {
                        Brush.horizontalGradient(
                            listOf(NeonViolet.copy(alpha = 0.25f), NeonCyan.copy(alpha = 0.15f))
                        )
                    } else {
                        Brush.horizontalGradient(
                            listOf(Color(0xFF1B2138), Color(0xFF1B2138))
                        )
                    }

                    val borderColor = if (isSelected) NeonViolet else Color(0x22FFFFFF)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(bgBrush)
                            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                            .clickable { onStyleSelected(style) }
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = style.displayName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isSelected) Color.White else Color(0xFFE2E8F0),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = style.myanmarLabel,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isSelected) NeonCyan else Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Checkbox: Keep original meaning and emotion
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onKeepEmotionChanged(!keepEmotion) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = keepEmotion,
                    onCheckedChange = onKeepEmotionChanged,
                    colors = CheckboxDefaults.colors(
                        checkedColor = NeonViolet,
                        uncheckedColor = Color(0xFF64748B),
                        checkmarkColor = Color.White
                    ),
                    modifier = Modifier.testTag("checkbox_keep_emotion")
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = if (keepEmotion) NeonPink else Color(0xFF64748B),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.opt_keep_emotion),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (keepEmotion) Color.White else Color(0xFFCBD5E1),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action: Translate with AI button
            Button(
                onClick = onTranslateClicked,
                enabled = !isProcessing,
                modifier = Modifier
                    .testTag("translate_ai_button")
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonViolet,
                    disabledContainerColor = NeonViolet.copy(alpha = 0.4f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isProcessing) "AI Processing Vocals..." else stringResource(R.string.btn_translate),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}
