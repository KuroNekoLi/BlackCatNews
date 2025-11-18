package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.presentation.wordbank.WordBankViewModel
import com.linli.dictionary.presentation.wordbank.WordBankViewModel.WordBankState
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 單字庫主畫面，負責從 [WordBankViewModel] 取得狀態並渲染內容。
 * 早期的 WordBankBottomSheet 已移除，統一改由此頁面呈現功能。
 *
 * @param viewModel 單字庫的狀態管理 ViewModel
 * @param modifier 外部傳入的修飾器
 */
@Composable
fun WordBankScreen(
    viewModel: WordBankViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    WordBankContentScreen(
        uiState = uiState,
        onRemoveWord = viewModel::removeWord,
        modifier = modifier
    )
}

/**
 * 單字庫內容畫面，可用於預覽，僅依賴狀態與回呼參數。
 *
 * @param uiState 畫面需要呈現的單字庫狀態
 * @param onRemoveWord 使用者點擊移除時的回呼
 * @param modifier 外部傳入的修飾器
 */
@Composable
fun WordBankContentScreen(
    uiState: WordBankState,
    onRemoveWord: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.error != null && uiState.savedWords.isEmpty() && !uiState.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = uiState.error, style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    when {
        uiState.isLoading && uiState.savedWords.isEmpty() -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.savedWords.isEmpty() -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "還沒有儲存的單字", style = MaterialTheme.typography.bodyLarge)
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.savedWords) { word ->
                    WordBankCard(
                        word = word,
                        onRemoveWord = { onRemoveWord(word.word) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WordBankCard(
    word: Word,
    onRemoveWord: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val firstEntry = word.entries.firstOrNull()
    val firstDefinition = firstEntry?.definitions?.firstOrNull()
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { onRemoveWord(word.word) }) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "移除單字")
                }
            }
            if (firstEntry != null && firstDefinition != null) {
                Text(
                    text = firstEntry.partOfSpeech,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = firstDefinition.zhDefinition,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * 單字庫內容的預覽，示範數筆假資料與移除按鈕的外觀。
 */
@Preview
@Composable
fun WordBankContentScreenPreview() {
    WordBankContentScreen(
        uiState = WordBankState(
            savedWords = sampleWords,
            isLoading = false,
            error = null,
            wordCount = sampleWords.size
        ),
        onRemoveWord = {}
    )
}

private val sampleWords = listOf(
    Word(
        word = "cat",
        pronunciations = Word.Pronunciations(uk = "kæt", us = "kæt"),
        entries = listOf(
            Word.Entry(
                partOfSpeech = "n.",
                definitions = listOf(
                    Word.Definition(
                        enDefinition = "a small domesticated carnivorous mammal",
                        zhDefinition = "家貓，常見的寵物之一",
                        examples = emptyList()
                    )
                )
            )
        )
    ),
    Word(
        word = "run",
        pronunciations = Word.Pronunciations(uk = "rʌn", us = "rʌn"),
        entries = listOf(
            Word.Entry(
                partOfSpeech = "v.",
                definitions = listOf(
                    Word.Definition(
                        enDefinition = "move at a speed faster than a walk",
                        zhDefinition = "奔跑、快速移動",
                        examples = emptyList()
                    )
                )
            )
        )
    )
)
