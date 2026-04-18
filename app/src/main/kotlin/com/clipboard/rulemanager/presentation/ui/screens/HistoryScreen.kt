package com.clipboard.rulemanager.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clipboard.rulemanager.data.database.AppDatabase
import com.clipboard.rulemanager.data.model.ClipboardHistory
import com.clipboard.rulemanager.data.repository.ClipboardRepository
import com.clipboard.rulemanager.presentation.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val repository = remember { ClipboardRepository(context, database.clipboardRuleDao(), database.clipboardHistoryDao()) }
    val viewModel: HistoryViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(repository) as T
        }
    })

    val history by viewModel.history.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { viewModel.clearHistory() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            Text("Clear History")
        }

        if (history.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No clipboard modifications recorded")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(history) { entry ->
                    HistoryCard(entry)
                }
            }
        }
    }
}

@Composable
fun HistoryCard(history: ClipboardHistory) {
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
            Text(
                text = "Time: ${formatTime(history.timestamp)}",
                fontSize = 12.sp
            )
            if (history.ruleApplied != null) {
                Text(
                    text = "Rule: ${history.ruleApplied}",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Text(
                text = "Original:",
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = history.originalText,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = "Modified:",
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = history.modifiedText,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val formatter = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
