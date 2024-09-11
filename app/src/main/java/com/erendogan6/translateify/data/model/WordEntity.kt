package com.erendogan6.translateify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.UUID

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val english: String,
    val translation: String,
    val isLearned: Boolean = false,
    val difficulty: String = "easy",
) : Serializable
