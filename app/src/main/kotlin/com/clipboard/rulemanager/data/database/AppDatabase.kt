package com.clipboard.rulemanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.clipboard.rulemanager.data.dao.ClipboardHistoryDao
import com.clipboard.rulemanager.data.dao.ClipboardRuleDao
import com.clipboard.rulemanager.data.model.ClipboardHistory
import com.clipboard.rulemanager.data.model.ClipboardRule

@Database(
    entities = [ClipboardRule::class, ClipboardHistory::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clipboardRuleDao(): ClipboardRuleDao
    abstract fun clipboardHistoryDao(): ClipboardHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "clipboard_rules_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
