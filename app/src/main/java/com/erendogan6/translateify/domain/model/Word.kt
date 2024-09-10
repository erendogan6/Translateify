package com.erendogan6.translateify.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Word(
    val id: Int,
    val english: String,
    val translation: String,
    val isLearned: Boolean,
    val difficulty: String,
) : Parcelable
