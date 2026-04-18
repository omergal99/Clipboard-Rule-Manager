package com.clipboard.rulemanager.data.repository

import android.content.ClipboardManager
import android.content.Context
import com.clipboard.rulemanager.data.dao.ClipboardHistoryDao
import com.clipboard.rulemanager.data.dao.ClipboardRuleDao
import com.clipboard.rulemanager.data.model.ClipboardHistory
import com.clipboard.rulemanager.data.model.ClipboardRule
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing clipboard operations and rule persistence
 */
class ClipboardRepository(
    private val context: Context,
    private val ruleDao: ClipboardRuleDao,
    private val historyDao: ClipboardHistoryDao
) {
    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    // Rules management
    fun getAllRules(): Flow<List<ClipboardRule>> = ruleDao.getAllRules()

    fun getEnabledRules(): Flow<List<ClipboardRule>> = ruleDao.getEnabledRules()

    suspend fun insertRule(rule: ClipboardRule): Long = ruleDao.insertRule(rule)

    suspend fun updateRule(rule: ClipboardRule) = ruleDao.updateRule(rule)

    suspend fun deleteRule(rule: ClipboardRule) = ruleDao.deleteRule(rule)

    suspend fun getRuleById(id: Int) = ruleDao.getRuleById(id)

    suspend fun getRuleCount() = ruleDao.getRuleCount()

    // History management
    fun getRecentHistory(): Flow<List<ClipboardHistory>> = historyDao.getRecentHistory()

    suspend fun insertHistory(history: ClipboardHistory) = historyDao.insertHistory(history)

    suspend fun clearHistory() = historyDao.clearHistory()

    suspend fun deleteHistoryBefore(timestamp: Long) = historyDao.deleteHistoryBefore(timestamp)

    // Clipboard operations
    fun getClipboardText(): String? {
        return try {
            val clip = clipboardManager.primaryClip
            clip?.getItemAt(0)?.text?.toString()
        } catch (e: Exception) {
            null
        }
    }

    fun setClipboardText(text: String) {
        try {
            val clip = android.content.ClipData.newPlainText("cleaned_text", text)
            clipboardManager.setPrimaryClip(clip)
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    // Batch import/export
    suspend fun deleteAllRules() {
        val count = ruleDao.getRuleCount()
        val rules = getAllRules().collect { ruleList ->
            ruleList.forEach { deleteRule(it) }
        }
    }
}
