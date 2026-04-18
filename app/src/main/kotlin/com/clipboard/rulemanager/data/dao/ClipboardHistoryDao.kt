package com.clipboard.rulemanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.clipboard.rulemanager.data.model.ClipboardHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipboardHistoryDao {
    @Query("SELECT * FROM clipboard_history ORDER BY timestamp DESC LIMIT 100")
    fun getRecentHistory(): Flow<List<ClipboardHistory>>

    @Query("SELECT * FROM clipboard_history WHERE timestamp > :since ORDER BY timestamp DESC")
    fun getHistorySince(since: Long): Flow<List<ClipboardHistory>>

    @Insert
    suspend fun insertHistory(history: ClipboardHistory): Long

    @Delete
    suspend fun deleteHistory(history: ClipboardHistory)

    @Query("DELETE FROM clipboard_history WHERE timestamp < :before")
    suspend fun deleteHistoryBefore(before: Long)

    @Query("DELETE FROM clipboard_history")
    suspend fun clearHistory()
}
