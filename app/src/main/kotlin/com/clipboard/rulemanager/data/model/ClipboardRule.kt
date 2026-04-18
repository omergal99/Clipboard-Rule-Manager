package com.clipboard.rulemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "clipboard_rules")
data class ClipboardRule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val matchContains: String, // Domain or host to match
    val regex: String, // Regex pattern to match
    val replacement: String = "", // Replacement text (usually empty for removal)
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
