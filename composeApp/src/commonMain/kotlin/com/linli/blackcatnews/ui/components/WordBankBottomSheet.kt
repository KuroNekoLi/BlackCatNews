package com.linli.blackcatnews.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.presentation.wordbank.WordBankViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordBankBottomSheet(
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    viewModel: WordBankViewModel,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val uiState by viewModel.uiState.collectAsState()
    var pendingWord by rememberSaveable { mutableStateOf("") }
    var wordToDelete by remember { mutableStateOf<Word?>(null) }

    LaunchedEffect(isOpen) {
        if (isOpen) {
            viewModel.loadSavedWords()
            viewModel.loadWordCount()
        }
    }

    if (isOpen) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "我的單字庫",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "共 ${uiState.wordCount} 個單字",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    value = pendingWord,
                    onValueChange = { pendingWord = it },
                    label = { Text("輸入要加入的單字") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (pendingWord.isNotBlank()) {
                                    viewModel.addWord(pendingWord.trim())
                                    pendingWord = ""
                                }
                            },
                            enabled = pendingWord.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "新增單字"
                            )
                        }
                    }
                )

                if (uiState.error != null) {
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = uiState.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (uiState.isLoading) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }

                if (uiState.savedWords.isEmpty() && !uiState.isLoading) {
                    Text(
                        modifier = Modifier.padding(top = 24.dp),
                        text = "目前沒有已儲存的單字，試著新增一個吧！",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(top = 12.dp),
                        contentPadding = PaddingValues(bottom = 48.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.savedWords, key = { it.word }) { word ->
                            WordBankListItem(
                                word = word,
                                onRemove = { wordToDelete = word }
                            )
                        }
                    }
                }
            }
        }
    }

    if (wordToDelete != null) {
        val target = wordToDelete
        AlertDialog(
            onDismissRequest = { wordToDelete = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        target?.word?.let { viewModel.removeWord(it) }
                        wordToDelete = null
                    }
                ) { Text("移除") }
            },
            dismissButton = {
                TextButton(onClick = { wordToDelete = null }) { Text("取消") }
            },
            title = { Text("從單字庫移除") },
            text = {
                Text("確定要移除 \"${target?.word}\" 嗎？")
            }
        )
    }
}

@Composable
private fun WordBankListItem(
    word: Word,
    onRemove: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    modifier = Modifier.clip(CircleShape),
                    onClick = onRemove
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "移除單字"
                    )
                }
            }

            val pronunciations = word.pronunciations
            if (pronunciations.uk.isNotBlank() || pronunciations.us.isNotBlank()) {
                Text(
                    text = buildPronunciationText(pronunciations),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val firstDefinition = word.entries.firstOrNull()?.definitions?.firstOrNull()
            if (firstDefinition != null) {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = firstDefinition.enDefinition,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (firstDefinition.zhDefinition.isNotBlank()) {
                    Text(
                        text = firstDefinition.zhDefinition,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun buildPronunciationText(pronunciations: Word.Pronunciations): String {
    val parts = mutableListOf<String>()
    if (pronunciations.uk.isNotBlank()) {
        parts.add("UK [${pronunciations.uk}]")
    }
    if (pronunciations.us.isNotBlank()) {
        parts.add("US [${pronunciations.us}]")
    }
    return parts.joinToString("  ·  ")
}
