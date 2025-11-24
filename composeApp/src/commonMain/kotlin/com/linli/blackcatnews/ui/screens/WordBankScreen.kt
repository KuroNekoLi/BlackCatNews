package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.linli.dictionary.domain.model.ReviewCard
import com.linli.dictionary.domain.model.ReviewMetadata
import com.linli.dictionary.domain.model.ReviewState
import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.presentation.wordbank.WordBankViewModel
import com.linli.dictionary.presentation.wordbank.WordBankViewModel.WordBankState
import com.linli.dictionary.presentation.wordbank.WordReviewViewModel
import kotlinx.datetime.Instant
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

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
    reviewViewModel: WordReviewViewModel = koinViewModel(),
    onNavigateToReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val reviewUiState by reviewViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.wordCount) {
        reviewViewModel.refreshQueue()
    }

    LaunchedEffect(Unit) {
        viewModel.refreshReviewEvent.collect {
            reviewViewModel.refreshQueue()
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        reviewViewModel.refreshQueue()
    }

    WordBankContentScreen(
        uiState = uiState,
        reviewUiState = reviewUiState,
        onRemoveWord = viewModel::removeWord,
        onResetProgress = viewModel::resetWordProgress,
        onNavigateToReview = onNavigateToReview,
        onRefreshReview = reviewViewModel::refreshQueue,
        modifier = modifier
    )
}

/**
 * 單字庫內容畫面，可用於預覽，僅依賴狀態與回呼參數。
 *
 * @param uiState 畫面需要呈現的單字庫狀態
 * @param reviewUiState 複習區域狀態
 * @param onRemoveWord 使用者點擊移除時的回呼
 * @param onResetProgress 使用者重置學習進度時的回呼
 * @param onNavigateToReview 開始複習的回呼
 * @param onRefreshReview 重新載入複習列表的回呼
 * @param modifier 外部傳入的修飾器
 */
@Composable
fun WordBankContentScreen(
    uiState: WordBankState,
    reviewUiState: WordReviewViewModel.ReviewUiState,
    onRemoveWord: (String) -> Unit,
    onResetProgress: (String) -> Unit,
    onNavigateToReview: () -> Unit,
    onRefreshReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val errorMessage = uiState.error
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ReviewSummaryCard(
                reviewState = reviewUiState,
                onNavigateToReview = onNavigateToReview,
                onRefreshReview = onRefreshReview
            )
        }

        when {
            uiState.isLoading && uiState.savedWords.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            errorMessage != null && uiState.savedWords.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = errorMessage, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            uiState.savedWords.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "還沒有儲存的單字", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            else -> {
                items(uiState.savedWords) { word ->
                    WordBankCard(
                        word = word,
                        onRemoveWord = { onRemoveWord(word.word) },
                        onResetProgress = { onResetProgress(word.word) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewSummaryCard(
    reviewState: WordReviewViewModel.ReviewUiState,
    onNavigateToReview: () -> Unit,
    onRefreshReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "FSRS 複習", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "到期卡片：${reviewState.dueCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (reviewState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                Button(
                    onClick = onNavigateToReview,
                    enabled = reviewState.dueCount > 0,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (reviewState.dueCount > 0) "開始複習" else "目前沒有需要複習的單字")
                }
            }

            val currentError = reviewState.error
            if (currentError != null) {
                Text(
                    text = currentError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun WordBankCard(
    word: Word,
    onRemoveWord: (String) -> Unit,
    onResetProgress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val firstEntry = word.entries.firstOrNull()
    val firstDefinition = firstEntry?.definitions?.firstOrNull()
    var showMenu by remember { mutableStateOf(false) }

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

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "更多選項")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("重置學習進度") },
                            onClick = {
                                onResetProgress(word.word)
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("移除單字", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                onRemoveWord(word.word)
                                showMenu = false
                            }
                        )
                    }
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
        reviewUiState = previewReviewState,
        onRemoveWord = {},
        onResetProgress = {},
        onNavigateToReview = {},
        onRefreshReview = {}
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

@OptIn(ExperimentalTime::class)
private val previewReviewState = WordReviewViewModel.ReviewUiState(
    currentCard = ReviewCard(
        word = sampleWords.first(),
        metadata = ReviewMetadata(
            dueAt = Instant.DISTANT_PAST,
            lastReviewedAt = null,
            stability = 0.5,
            difficulty = 5.0,
            reps = 0,
            lapses = 0,
            state = ReviewState.NEW,
            scheduledDays = 0,
            elapsedDays = 0
        )
    ),
    waitingCards = emptyList(),
    reviewedCount = 0,
    totalDueCount = 1,
    isLoading = false,
    error = null,
    sessionCompleted = false
)
