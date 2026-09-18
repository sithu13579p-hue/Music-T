package com.example.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongTranslationDao {
    @Query("SELECT * FROM song_translations ORDER BY createdAt DESC")
    fun getAllTranslations(): Flow<List<SongTranslationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslation(entity: SongTranslationEntity): Long

    @Query("DELETE FROM song_translations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM song_translations WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): SongTranslationEntity?
}
