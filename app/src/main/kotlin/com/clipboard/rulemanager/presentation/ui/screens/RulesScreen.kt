package com.clipboard.rulemanager.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clipboard.rulemanager.data.database.AppDatabase
import com.clipboard.rulemanager.data.model.ClipboardRule
import com.clipboard.rulemanager.data.repository.ClipboardRepository
import com.clipboard.rulemanager.presentation.viewmodel.RulesViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit

@Composable
fun RulesScreen(onOpenAccessibilitySettings: () -> Unit) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val repository = remember { ClipboardRepository(context, database.clipboardRuleDao(), database.clipboardHistoryDao()) }
    val viewModel: RulesViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return RulesViewModel(repository) as T
        }
    })

    val rules by viewModel.rules.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingRule by remember { mutableStateOf<ClipboardRule?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { onOpenAccessibilitySettings() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            Text("Enable Service in Accessibility Settings")
        }

        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        ) {
            Text("Add New Rule")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(rules) { rule ->
                RuleCard(
                    rule = rule,
                    onToggleEnabled = { viewModel.toggleRuleEnabled(rule) },
                    onEdit = { editingRule = it },
                    onDelete = { viewModel.deleteRule(it) }
                )
            }
        }
    }

    if (showAddDialog || editingRule != null) {
        AddEditRuleDialog(
            rule = editingRule,
            onSave = { rule ->
                if (editingRule != null) {
                    viewModel.updateRule(rule)
                } else {
                    viewModel.addRule(rule)
                }
                showAddDialog = false
                editingRule = null
            },
            onDismiss = {
                showAddDialog = false
                editingRule = null
            }
        )
    }
}

@Composable
fun RuleCard(
    rule: ClipboardRule,
    onToggleEnabled: () -> Unit,
    onEdit: (ClipboardRule) -> Unit,
    onDelete: (ClipboardRule) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rule.name,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Default
                    )
                    Text(
                        text = "Match: ${rule.matchContains}",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rule.enabled,
                        onCheckedChange = { onToggleEnabled() }
                    )
                    IconButton(onClick = { onEdit(rule) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { onDelete(rule) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
            Text(
                text = "Regex: ${rule.regex}",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
