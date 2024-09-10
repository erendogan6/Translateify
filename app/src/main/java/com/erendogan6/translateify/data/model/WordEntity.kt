package com.erendogan6.translateify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val english: String,
    val translation: String,
    val isLearned: Boolean = false,
    val difficulty: String = "easy",
) : Serializable
