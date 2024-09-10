package com.erendogan6.translateify.di

import android.app.Application
import android.content.Context
import com.erendogan6.translateify.data.local.AppDatabase
import com.erendogan6.translateify.data.mapper.toEntity
import com.erendogan6.translateify.utils.Utils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class TranslateifyApp : Application() {
    @Inject
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            loadInitialData(this@TranslateifyApp)
        }
    }

    private suspend fun loadInitialData(context: Context) {
        val wordDao = database.wordDao()

        val count = wordDao.getWordsCount()
        if (count == 0) {
            val jsonString = Utils.loadJSONFromAsset(context, "words.json")
            jsonString?.let {
                val wordList = Utils.parseWordsFromJson(it)
                wordDao.insertWords(wordList.map { it.toEntity() })
            }
        }
    }
}
