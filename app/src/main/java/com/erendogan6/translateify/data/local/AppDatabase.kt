package com.erendogan6.translateify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.erendogan6.translateify.data.model.WordEntity

@Database(entities = [WordEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}
