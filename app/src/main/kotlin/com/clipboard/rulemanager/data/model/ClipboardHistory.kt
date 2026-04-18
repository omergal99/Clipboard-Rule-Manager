package com.clipboard.rulemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clipboard_history")
data class ClipboardHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val originalText: String,
    val modifiedText: String,
    val ruleApplied: String?, // Name of the rule applied, if any
    val timestamp: Long = System.currentTimeMillis()
)
