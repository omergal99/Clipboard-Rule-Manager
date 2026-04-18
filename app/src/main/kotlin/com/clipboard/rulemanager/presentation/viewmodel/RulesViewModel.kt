package com.clipboard.rulemanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clipboard.rulemanager.data.model.ClipboardRule
import com.clipboard.rulemanager.data.repository.ClipboardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RulesViewModel(private val repository: ClipboardRepository) : ViewModel() {
    val rules: StateFlow<List<ClipboardRule>> = repository.getAllRules()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addRule(rule: ClipboardRule) {
        viewModelScope.launch {
            repository.insertRule(rule)
        }
    }

    fun updateRule(rule: ClipboardRule) {
        viewModelScope.launch {
            repository.updateRule(rule)
        }
    }

    fun deleteRule(rule: ClipboardRule) {
        viewModelScope.launch {
            repository.deleteRule(rule)
        }
    }

    fun toggleRuleEnabled(rule: ClipboardRule) {
        viewModelScope.launch {
            repository.updateRule(rule.copy(enabled = !rule.enabled))
        }
    }
}
