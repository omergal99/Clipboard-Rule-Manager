package com.clipboard.rulemanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.clipboard.rulemanager.data.model.ClipboardRule
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipboardRuleDao {
    @Query("SELECT * FROM clipboard_rules ORDER BY createdAt DESC")
    fun getAllRules(): Flow<List<ClipboardRule>>

    @Query("SELECT * FROM clipboard_rules WHERE enabled = 1 ORDER BY createdAt DESC")
    fun getEnabledRules(): Flow<List<ClipboardRule>>

    @Query("SELECT * FROM clipboard_rules WHERE id = :id")
    suspend fun getRuleById(id: Int): ClipboardRule?

    @Insert
    suspend fun insertRule(rule: ClipboardRule): Long

    @Update
    suspend fun updateRule(rule: ClipboardRule)

    @Delete
    suspend fun deleteRule(rule: ClipboardRule)

    @Query("DELETE FROM clipboard_rules WHERE id = :id")
    suspend fun deleteRuleById(id: Int)

    @Query("SELECT COUNT(*) FROM clipboard_rules")
    suspend fun getRuleCount(): Int
}
