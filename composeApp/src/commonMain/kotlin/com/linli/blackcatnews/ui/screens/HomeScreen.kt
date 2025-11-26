package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.domain.model.NewsCategory
import com.linli.blackcatnews.domain.model.NewsItem
import com.linli.blackcatnews.isDebug
import com.linli.blackcatnews.presentation.state.HomeUiEvent
import com.linli.blackcatnews.presentation.viewmodel.HomeViewModel
import com.linli.blackcatnews.ui.components.CategoryChip
import com.linli.blackcatnews.ui.components.DebugBadge
import com.linli.blackcatnews.ui.components.NewsCard

/**
 * 首頁屏幕
 * 顯示分類 Chips 和新聞列表
 * Scaffold 和 TopBar 由 AppNavigation 統一管理
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNewsItemClick: (NewsItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory = uiState.selectedCategory
    val articles = uiState.articles
    val prefersChinese = uiState.prefersChinese
    val dailyStreak = uiState.dailyStreak
    val isStreakActive = uiState.isStreakActive
    val shouldShowUnsupportedCategoryMessage =
        !uiState.isRefreshing && articles.isEmpty() && selectedCategory !in supportedCategories

    val pullToRefreshState = rememberPullToRefreshState()

    val showEmptyState = !uiState.isRefreshing && uiState.errorMessage == null &&
        articles.isEmpty() && !shouldShowUnsupportedCategoryMessage
    val showErrorState = !uiState.isRefreshing && uiState.errorMessage != null && articles.isEmpty()

    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        isRefreshing = uiState.isRefreshing,
        onRefresh = { viewModel.onEvent(HomeUiEvent.Refresh) },
        state = pullToRefreshState
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. 分類 Chips (Fixed at top)
            CategoryChipsRow(
                categories = NewsCategory.entries,
                selectedCategory = selectedCategory,
                onCategorySelected = {
                    viewModel.onEvent(HomeUiEvent.SelectCategory(it))
                },
                modifier = Modifier.fillMaxWidth()
            )

            // 2. Scrollable Content
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Dashboard Section (Streak + Language)
                    item {
                        DashboardCard(
                            streak = dailyStreak,
                            isActive = isStreakActive,
                            useChinese = prefersChinese,
                            onLanguageToggle = { viewModel.onEvent(HomeUiEvent.ToggleLanguage(it)) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // News Items
                    items(articles, key = { it.id }) { newsItem ->
                        NewsCard(
                            newsItem = newsItem,
                            onClick = { onNewsItemClick(newsItem) },
                            prefersChinese = prefersChinese,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                if (shouldShowUnsupportedCategoryMessage) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "此分類尚未提供內容",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (showErrorState) {
                    ErrorState(
                        message = uiState.errorMessage.orEmpty(),
                        onRetry = { viewModel.onEvent(HomeUiEvent.Refresh) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (showEmptyState) {
                    EmptyArticlesState(
                        onRetry = { viewModel.onEvent(HomeUiEvent.Refresh) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                if (uiState.isRefreshing && articles.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                if (isDebug) {
                    DebugBadge(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * 整合型儀表板：包含連勝資訊與語言設定
 */
@Composable
private fun DashboardCard(
    streak: Int,
    isActive: Boolean,
    useChinese: Boolean,
    onLanguageToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Row 1: Streak Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isActive) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                            ) {}
                        }
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = if (isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isActive) "連勝中！" else "開始你的連勝",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Text(
                            text = "已連續學習 $streak 天",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isActive) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Great!", style = MaterialTheme.typography.labelSmall) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = null,
                        modifier = Modifier.height(24.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.height(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Row 2: Language Preferences
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Translate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "新聞顯示語言",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !useChinese,
                        onClick = { onLanguageToggle(false) },
                        label = { Text("English") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                    FilterChip(
                        selected = useChinese,
                        onClick = { onLanguageToggle(true) },
                        label = { Text("中文") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }
        }
    }
}

/**
 * 分類 Chips 橫向滾動列表
 */
@Composable
private fun CategoryChipsRow(
    categories: List<NewsCategory>,
    selectedCategory: NewsCategory,
    onCategorySelected: (NewsCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    // 只顯示已實作的分類
    val visibleCategories = categories.filter { it in supportedCategories }

    // Remove heavy Surface, use simple background
    LazyRow(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(visibleCategories) { category ->
            CategoryChip(
                category = category,
                isSelected = category == selectedCategory,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

/**
 * 生成模擬新聞數據
 */
private fun getSampleNewsList(): List<NewsItem> = emptyList()

private val supportedCategories = setOf(
    NewsCategory.LATEST,
    NewsCategory.WORLD,
    NewsCategory.TECH
)

@Composable
private fun EmptyArticlesState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "目前沒有文章",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRetry) {
            Text(text = "重新整理")
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRetry) {
            Text(text = "重試")
        }
    }
}
