package com.clipboard.rulemanager.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.clipboard.rulemanager.data.database.AppDatabase
import com.clipboard.rulemanager.data.model.ClipboardHistory
import com.clipboard.rulemanager.data.repository.ClipboardRepository
import com.clipboard.rulemanager.domain.RuleEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Accessibility Service that monitors clipboard changes and applies rules.
 * This service runs in the background and automatically processes copied text.
 */
class ClipboardAccessibilityService : AccessibilityService() {
    private lateinit var repository: ClipboardRepository
    private val ruleEngine = RuleEngine()
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var lastClipboardText: String? = null
    private var isProcessing = false

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        repository = ClipboardRepository(
            this,
            database.clipboardRuleDao(),
            database.clipboardHistoryDao()
        )
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_CLIPBOARD_CHANGED) {
            return
        }

        // Prevent recursive processing when we write back to clipboard
        if (isProcessing) {
            return
        }

        serviceScope.launch(Dispatchers.IO) {
            try {
                isProcessing = true

                val currentClipboardText = repository.getClipboardText() ?: return@launch

                // Check if text actually changed
                if (currentClipboardText == lastClipboardText) {
                    return@launch
                }

                lastClipboardText = currentClipboardText

                // Get enabled rules
                val enabledRules = repository.getEnabledRules().first()

                if (enabledRules.isEmpty()) {
                    return@launch
                }

                // Apply rules
                val result = ruleEngine.applyRules(currentClipboardText, enabledRules)

                if (result.modified) {
                    // Write cleaned text back to clipboard
                    repository.setClipboardText(result.modifiedText)

                    // Record in history
                    val history = ClipboardHistory(
                        originalText = result.originalText,
                        modifiedText = result.modifiedText,
                        ruleApplied = result.appliedRule?.name
                    )
                    repository.insertHistory(history)

                    // Update lastClipboardText to prevent reprocessing
                    lastClipboardText = result.modifiedText
                }
            } catch (e: Exception) {
                // Silently handle errors
            } finally {
                isProcessing = false
            }
        }
    }

    override fun onInterrupt() {
        // Handle service interruption
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
