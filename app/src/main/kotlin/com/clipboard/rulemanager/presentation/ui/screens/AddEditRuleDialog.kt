package com.clipboard.rulemanager.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clipboard.rulemanager.data.model.ClipboardRule

@Composable
fun AddEditRuleDialog(
    rule: ClipboardRule?,
    onSave: (ClipboardRule) -> Unit,
    onDismiss: () -> Unit
) {
    val name = remember { mutableStateOf(rule?.name ?: "") }
    val matchContains = remember { mutableStateOf(rule?.matchContains ?: "") }
    val regex = remember { mutableStateOf(rule?.regex ?: "") }
    val replacement = remember { mutableStateOf(rule?.replacement ?: "") }
    val enabled = remember { mutableStateOf(rule?.enabled ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (rule == null) "Add New Rule" else "Edit Rule") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                TextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Rule Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                TextField(
                    value = matchContains.value,
                    onValueChange = { matchContains.value = it },
                    label = { Text("Match Contains (e.g., instagram.com)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                TextField(
                    value = regex.value,
                    onValueChange = { regex.value = it },
                    label = { Text("Regex Pattern") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    isError = regex.value.isNotEmpty() && !isValidRegex(regex.value)
                )

                if (regex.value.isNotEmpty() && !isValidRegex(regex.value)) {
                    Text(
                        text = "Invalid regex pattern",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                TextField(
                    value = replacement.value,
                    onValueChange = { replacement.value = it },
                    label = { Text("Replacement (leave empty to remove)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = enabled.value,
                        onCheckedChange = { enabled.value = it }
                    )
                    Text("Enabled")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.value.isNotEmpty() && matchContains.value.isNotEmpty() && regex.value.isNotEmpty()) {
                        onSave(
                            ClipboardRule(
                                id = rule?.id ?: 0,
                                name = name.value,
                                matchContains = matchContains.value,
                                regex = regex.value,
                                replacement = replacement.value,
                                enabled = enabled.value,
                                createdAt = rule?.createdAt ?: System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    }
                },
                enabled = name.value.isNotEmpty() && matchContains.value.isNotEmpty() && regex.value.isNotEmpty() && isValidRegex(regex.value)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun isValidRegex(pattern: String): Boolean {
    return try {
        Regex(pattern)
        true
    } catch (e: Exception) {
        false
    }
}
