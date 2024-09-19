package com.erendogan6.translateify.domain.model

data class Category(
    val name: String,
    val iconResId: Int,
    var isSelected: Boolean = false,
)
