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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.domain.model.NewsCategory
import com.linli.blackcatnews.domain.model.NewsItem
import com.linli.blackcatnews.presentation.state.HomeUiEvent
import com.linli.blackcatnews.presentation.viewmodel.HomeViewModel
import com.linli.blackcatnews.ui.components.CategoryChip
import com.linli.blackcatnews.ui.components.NewsCard
import com.linli.blackcatnews.ui.components.WordBankBottomSheet
import com.linli.dictionary.presentation.wordbank.WordBankViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * 首頁屏幕
 * 顯示分類 Chips 和新聞列表
 * Scaffold 和 TopBar 由 AppNavigation 統一管理
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNewsItemClick: (NewsItem) -> Unit,
    wordBankViewModel: WordBankViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory = uiState.selectedCategory
    val articles = uiState.articles
    val prefersChinese = uiState.prefersChinese
    val shouldShowUnsupportedCategoryMessage =
        !uiState.isRefreshing && articles.isEmpty() && selectedCategory !in supportedCategories

    if (uiState.isRefreshing && uiState.articles.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (!uiState.isRefreshing && uiState.errorMessage != null && uiState.articles.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = uiState.errorMessage ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    var isWordBankSheetOpen by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
        // 分類 Chips
        CategoryChipsRow(
            categories = NewsCategory.entries,
            selectedCategory = selectedCategory,
            onCategorySelected = {
                viewModel.onEvent(HomeUiEvent.SelectCategory(it))
            },
            modifier = Modifier.fillMaxWidth()
        )

        // 語言切換
        LanguageToggleRow(
            useChinese = prefersChinese,
            onToggle = { viewModel.onEvent(HomeUiEvent.ToggleLanguage(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // 新聞列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(articles, key = { it.id }) { newsItem ->
                NewsCard(
                    newsItem = newsItem,
                    onClick = { onNewsItemClick(newsItem) },
                    prefersChinese = prefersChinese
                )
            }
        }

        if (shouldShowUnsupportedCategoryMessage) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "此分類尚未提供內容",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

        ExtendedFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = {
                isWordBankSheetOpen = true
                wordBankViewModel.loadSavedWords()
                wordBankViewModel.loadWordCount()
            },
            icon = { Icon(imageVector = Icons.Filled.Book, contentDescription = null) },
            text = { Text("單字庫") }
        )
    }

    WordBankBottomSheet(
        isOpen = isWordBankSheetOpen,
        viewModel = wordBankViewModel,
        onDismissRequest = { isWordBankSheetOpen = false }
    )
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

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
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
private fun LanguageToggleRow(
    useChinese: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "內容顯示語言",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (useChinese) "目前顯示：中文" else "Currently showing: English",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LanguageChip(
                    text = "English",
                    selected = !useChinese,
                    onClick = { onToggle(false) }
                )
                LanguageChip(
                    text = "中文",
                    selected = useChinese,
                    onClick = { onToggle(true) }
                )
            }
        }
    }
}

@Composable
private fun LanguageChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
            labelColor = if (selected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    )
}
