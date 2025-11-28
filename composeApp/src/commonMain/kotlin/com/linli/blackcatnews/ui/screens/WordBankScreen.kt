package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.linli.blackcatnews.tts.rememberTextToSpeechManager
import com.linli.blackcatnews.tts.rememberTtsPlaybackController
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

    val ttsManager = rememberTextToSpeechManager()
    val ttsController = rememberTtsPlaybackController(ttsManager)

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
        onPlayAudio = { text, id ->
            ttsController.play(text, id, "en-US")
        },
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
 * @param onPlayAudio 播放音訊的回呼
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
    onPlayAudio: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    // Mock Stats (Replace with ViewModel data)
    val stats = LearningStats(
        totalWords = uiState.wordCount,
        newWords = uiState.wordCount / 2, // Mock
        learningWords = uiState.wordCount / 3, // Mock
        masteredWords = uiState.wordCount - (uiState.wordCount / 2) - (uiState.wordCount / 3), // Mock
        dueReviewCount = reviewUiState.dueCount
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp), // Handle bottom nav padding externally or here
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Header Dashboard
        item {
            WordBankHeader(stats)
        }

        // 2. Training Modes
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "訓練模式",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        TrainingModeCard(
                            title = "閃卡特訓",
                            subtitle = "${stats.dueReviewCount} 待複習",
                            icon = Icons.Filled.School,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            onClick = onNavigateToReview,
                            enabled = stats.dueReviewCount > 0
                        )
                    }
                    item {
                        TrainingModeCard(
                            title = "聽力挑戰",
                            subtitle = "即將推出",
                            icon = Icons.Filled.Headphones,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = {},
                            enabled = false
                        )
                    }
                    item {
                        TrainingModeCard(
                            title = "拼寫練習",
                            subtitle = "即將推出",
                            icon = Icons.Filled.Edit,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            onClick = {},
                            enabled = false
                        )
                    }
                }
            }
        }

        // 3. Word List Section
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "所有單字 (${stats.totalWords})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { /* TODO: Sort/Filter */ }) {
                        Text("排序")
                    }
                }
                androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        when {
            uiState.isLoading && uiState.savedWords.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            uiState.savedWords.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "還沒有儲存的單字",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "在閱讀時點擊單字即可加入",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            else -> {
                items(uiState.savedWords) { word ->
                    WordBankCard(
                        word = word,
                        onRemoveWord = { onRemoveWord(word.word) },
                        onResetProgress = { onResetProgress(word.word) },
                        onPlayAudio = onPlayAudio,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WordBankHeader(stats: LearningStats) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "學習概況",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    count = stats.totalWords,
                    label = "總單字量",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    count = stats.masteredWords,
                    label = "已熟練",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    count = stats.newWords,
                    label = "新單字",
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    count: Int,
    label: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TrainingModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .height(140.dp)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = if (enabled) 0.3f else 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.4f
                        )
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.4f
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.4f
                    )
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
    onPlayAudio: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val firstEntry = word.entries.firstOrNull()
    val firstDefinition = firstEntry?.definitions?.firstOrNull()
    var showMenu by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = word.word,
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = { onPlayAudio(word.word, word.word) }) {
                        Icon(
                            imageVector = Icons.Filled.VolumeUp,
                            contentDescription = "播放音訊"
                        )
                    }
                }

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
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            androidx.compose.animation.AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    word.entries.forEach { entry ->
                        Text(
                            text = entry.partOfSpeech,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        entry.definitions.forEach { definition ->
                            Text(
                                text = "• ${definition.enDefinition}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                text = definition.zhDefinition,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            definition.examples.forEach { example ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 2.dp)
                                ) {
                                    Text(
                                        text = "  Example: $example",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.VolumeUp,
                                        contentDescription = "朗讀例句",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .size(16.dp)
                                            .clickable {
                                                onPlayAudio(
                                                    example,
                                                    "example_${example.hashCode()}"
                                                )
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Arrow icon to indicate expandability
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "收起" else "展開",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class LearningStats(
    val totalWords: Int,
    val newWords: Int,
    val learningWords: Int,
    val masteredWords: Int,
    val dueReviewCount: Int
)

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
        onRefreshReview = {},
        onPlayAudio = { _, _ -> }
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
