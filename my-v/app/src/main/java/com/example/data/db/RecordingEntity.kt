package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordings")
data class RecordingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val filePath: String,
    val durationMs: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val scriptName: String = "Free Recording",
    val sentiment: String? = null, // Store AI sentiment report
    val voiceScore: Int? = null,   // Voice Quality Score (out of 100)
    val pitchHz: Float? = null,    // Estimated vocal pitch in Hz
    val clarityPercent: Int? = null, // Clarity & Diction %
    val emotionalTone: String? = null, // Estimated emotional tone (e.g. "Warm", "Confident", "Tense")
    val transcript: String? = null // AI transcript of the voice file if available
)
