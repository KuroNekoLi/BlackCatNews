package com.linli.blackcatnews.presentation.state

import com.linli.blackcatnews.domain.model.NewsItem
import com.linli.blackcatnews.domain.model.NewsCategory

/**
 * 首頁畫面狀態
 */
data class HomeUiState(
    val isRefreshing: Boolean = false,
    val articles: List<NewsItem> = emptyList(),
    val errorMessage: String? = null,
    val selectedCategory: NewsCategory = NewsCategory.LATEST,
    val prefersChinese: Boolean = false
)

sealed interface HomeUiEvent {
    data object Refresh : HomeUiEvent
    data class SelectCategory(val category: NewsCategory) : HomeUiEvent
    data class ToggleLanguage(val useChinese: Boolean) : HomeUiEvent
    data class OpenArticle(val articleId: String) : HomeUiEvent
    data object LoadPreferences : HomeUiEvent
}

/**
 * 首頁一次性效果，如 Snackbar 顯示
 */
sealed interface HomeUiEffect {
    data class ShowError(val message: String) : HomeUiEffect
}
