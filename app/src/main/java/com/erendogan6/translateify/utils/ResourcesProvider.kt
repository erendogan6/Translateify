package com.erendogan6.translateify.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourcesProvider
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        fun getString(resId: Int): String = context.getString(resId)
    }
