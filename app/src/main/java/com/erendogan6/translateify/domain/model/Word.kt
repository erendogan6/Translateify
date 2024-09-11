package com.erendogan6.translateify.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Word(
    val id: String = UUID.randomUUID().toString(),
    val english: String,
    val translation: String,
    val isLearned: Boolean,
    val difficulty: String,
) : Parcelable
