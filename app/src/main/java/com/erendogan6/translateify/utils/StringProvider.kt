package com.erendogan6.translateify.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StringProvider
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : ResourcesProvider {
        override fun getString(resId: Int): String = context.getString(resId)

        override fun getString(
            resId: Int,
            vararg formatArgs: Any,
        ): String = context.getString(resId, *formatArgs)
    }
