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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.domain.model.NewsItem
import com.linli.blackcatnews.presentation.viewmodel.FavoritesUiEffect
import com.linli.blackcatnews.presentation.viewmodel.FavoritesUiEvent
import com.linli.blackcatnews.presentation.viewmodel.FavoritesViewModel

/**
 * 收藏屏幕
 * 顯示用戶收藏的新聞
 * Scaffold 和 TopBar 由 AppNavigation 統一管理
 */
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onNewsItemClick: (NewsItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Tab State
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("已收藏", "閱讀紀錄", "錯題本")

    // Handle UI effects
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is FavoritesUiEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is FavoritesUiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tabs
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.BookmarkBorder
                                    1 -> Icons.Default.History
                                    2 -> Icons.Default.ErrorOutline
                                    else -> Icons.Default.BookmarkBorder
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            // Content Area
            when (selectedTabIndex) {
                0 -> SavedTabContent(uiState, onNewsItemClick, viewModel)
                1 -> HistoryTabContent()
                2 -> MistakesTabContent()
            }
        }

        // Snackbar host at the bottom
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(
    icon: String = "📚",
    title: String = "尚無內容",
    subtitle: String = "這裡空空如也"
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FavoritesList(
    favorites: List<NewsItem>,
    onItemClick: (NewsItem) -> Unit,
    onRemove: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = favorites,
            key = { it.id }
        ) { newsItem ->
            SwipeToDismissItem(
                newsItem = newsItem,
                onItemClick = onItemClick,
                onRemove = onRemove
            )
        }
    }
}

@Composable
private fun SwipeToDismissItem(
    newsItem: NewsItem,
    onItemClick: (NewsItem) -> Unit,
    onRemove: (String) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onRemove(newsItem.id)
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "刪除",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) {
        FavoriteNewsCard(
            newsItem = newsItem,
            onClick = { onItemClick(newsItem) }
        )
    }
}

@Composable
private fun FavoriteNewsCard(
    newsItem: NewsItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = newsItem.titleZh ?: newsItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = newsItem.summaryZh ?: newsItem.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = newsItem.source,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = newsItem.publishTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Mock Data Models
data class HistoryItem(val id: String, val title: String, val time: String, val progress: Float)
data class MistakeItem(val id: String, val question: String, val answer: String, val count: Int)

@Composable
private fun SavedTabContent(
    uiState: com.linli.blackcatnews.presentation.viewmodel.FavoritesUiState,
    onNewsItemClick: (NewsItem) -> Unit,
    viewModel: FavoritesViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading && uiState.favorites.isEmpty() -> {
                LoadingState()
            }

            uiState.favorites.isEmpty() && !uiState.isLoading -> {
                EmptyState(
                    icon = "📚",
                    title = "尚無收藏的文章",
                    subtitle = "瀏覽新聞時點擊愛心圖示即可收藏"
                )
            }

            else -> {
                FavoritesList(
                    favorites = uiState.favorites,
                    onItemClick = onNewsItemClick,
                    onRemove = { articleId ->
                        viewModel.onEvent(FavoritesUiEvent.RemoveFavorite(articleId))
                    }
                )
            }
        }
    }
}

@Composable
private fun HistoryTabContent() {
    // Mock Data
    val history = listOf(
        HistoryItem("1", "AI Giants Report Q3 Earnings", "2 小時前", 0.8f),
        HistoryItem("2", "Global Warming Effects", "昨天", 0.3f),
        HistoryItem("3", "New Space Race Begins", "3 天前", 1.0f)
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(history) { item ->
            Card(elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(item.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            item.time,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            if (item.progress >= 1f) "已讀完" else "進度 ${(item.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (item.progress >= 1f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MistakesTabContent() {
    // Mock Data
    val mistakes = listOf(
        MistakeItem("1", "What does 'ephemeral' mean?", "Lasting for a very short time", 3),
        MistakeItem("2", "Choose the correct preposition: Interested ___ music", "in", 2)
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                        alpha = 0.1f
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("錯題本 (TODO)", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "這裡將彙整你在測驗中答錯的題目，幫助你針對弱點複習。",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        items(mistakes) { item ->
            Card(elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(item.question, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "正確答案: ${item.answer}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "錯誤次數: ${item.count}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
