package com.linli.blackcatnews.presentation.state

import com.linli.blackcatnews.domain.model.ArticleDetail

data class ArticleDetailUiState(
    val isLoading: Boolean = true,
    val article: ArticleDetail? = null,
    val errorMessage: String? = null
)

sealed interface ArticleDetailUiEvent {
    data object Refresh : ArticleDetailUiEvent
}

sealed interface ArticleDetailUiEffect {
    data class ShowError(val message: String) : ArticleDetailUiEffect
}

sealed interface ArticleDetailUiAction {
    data object ToggleFavorite : ArticleDetailUiAction
}
