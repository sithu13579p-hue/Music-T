package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_translations")
data class SongTranslationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val fileName: String,
    val fileUri: String?,
    val detectedLanguage: String,
    val translationStyle: String,
    val keepEmotion: Boolean,
    val lyricsJson: String,
    val createdAt: Long = System.currentTimeMillis()
)
