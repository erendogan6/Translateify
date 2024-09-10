package com.erendogan6.translateify.utils

import android.content.Context
import com.erendogan6.translateify.domain.model.Word
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object Utils {
    fun loadJSONFromAsset(
        context: Context,
        fileName: String,
    ): String? =
        try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }

    fun parseWordsFromJson(jsonString: String): List<Word> {
        val listType = object : TypeToken<List<Word>>() {}.type
        return Gson().fromJson(jsonString, listType)
    }
}
