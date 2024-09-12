package com.erendogan6.translateify.data.mapper

import com.erendogan6.translateify.data.model.WordEntity
import com.erendogan6.translateify.domain.model.Word

fun WordEntity.toDomainModel(): Word =
    Word(
        id = id,
        english = english,
        translation = translation,
        isLearned = isLearned,
        difficulty = difficulty,
        categories = categories,
    )

fun Word.toEntity(): WordEntity =
    WordEntity(
        id = id,
        english = english,
        translation = translation,
        isLearned = isLearned,
        difficulty = difficulty,
        categories = categories,
    )
