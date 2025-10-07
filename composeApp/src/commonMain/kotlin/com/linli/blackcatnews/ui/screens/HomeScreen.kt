package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.model.NewsCategory
import com.linli.blackcatnews.model.NewsItem
import com.linli.blackcatnews.ui.components.CategoryChip
import com.linli.blackcatnews.ui.components.NewsCard

/**
 * 首頁屏幕
 * 顯示分類 Chips 和新聞列表
 * Scaffold 和 TopBar 由 AppNavigation 統一管理
 */
@Composable
fun HomeScreen(
    onNewsItemClick: (NewsItem) -> Unit,
    modifier: Modifier = Modifier
) {
    // 當前選中的分類
    var selectedCategory by remember { mutableStateOf(NewsCategory.LATEST) }

    // 模擬新聞數據
    val newsList = remember { getSampleNewsList() }

    // 根據分類過濾新聞
    val filteredNews = remember(selectedCategory) {
        if (selectedCategory == NewsCategory.LATEST) {
            newsList
        } else {
            newsList.filter { it.category == selectedCategory }
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 分類 Chips
        CategoryChipsRow(
            categories = NewsCategory.entries,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            modifier = Modifier.fillMaxWidth()
        )

        // 新聞列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredNews, key = { it.id }) { newsItem ->
                NewsCard(
                    newsItem = newsItem,
                    onClick = { onNewsItemClick(newsItem) }
                )
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
            items(categories) { category ->
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
private fun getSampleNewsList(): List<NewsItem> {
    return listOf(
        NewsItem(
            id = "1",
            title = "AI Technology Reshapes Global Economy",
            summary = "Artificial intelligence is transforming industries worldwide, boosting efficiency and creating new opportunities...",
            imageUrl = "https://picsum.photos/400/300?random=1",
            source = "Tech News",
            publishTime = "2小時前",
            category = NewsCategory.TECH
        ),
        NewsItem(
            id = "2",
            title = "Global Markets Show Strong Recovery",
            summary = "Stock markets worldwide are experiencing significant gains as economic indicators improve...",
            imageUrl = "https://picsum.photos/400/300?random=2",
            source = "財經週刊",
            publishTime = "3小時前",
            category = NewsCategory.BUSINESS
        ),
        NewsItem(
            id = "3",
            title = "Climate Change Impact on Agriculture",
            summary = "New research reveals how changing weather patterns are affecting crop yields globally...",
            imageUrl = "https://picsum.photos/400/300?random=3",
            source = "環球時報",
            publishTime = "5小時前",
            category = NewsCategory.WORLD
        ),
        NewsItem(
            id = "4",
            title = "New Health Diet Trends Gain Popularity",
            summary = "Nutrition experts recommend innovative dietary approaches spreading on social media...",
            imageUrl = "https://picsum.photos/400/300?random=4",
            source = "健康生活",
            publishTime = "6小時前",
            category = NewsCategory.HEALTH
        ),
        NewsItem(
            id = "5",
            title = "Scientists Discover New Exoplanet",
            summary = "Astronomers find potentially habitable planet 100 light-years away from Earth...",
            imageUrl = "https://picsum.photos/400/300?random=5",
            source = "科學週刊",
            publishTime = "8小時前",
            category = NewsCategory.SCIENCE
        )
    )
}
