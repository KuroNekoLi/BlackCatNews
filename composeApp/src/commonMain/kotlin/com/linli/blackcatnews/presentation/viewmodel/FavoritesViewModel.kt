package com.linli.blackcatnews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linli.blackcatnews.domain.model.NewsItem
import com.linli.blackcatnews.domain.repository.Result
import com.linli.blackcatnews.domain.usecase.ManageFavoritesUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * 收藏頁面 ViewModel
 * 管理收藏文章的顯示和操作
 */
class FavoritesViewModel(
    private val manageFavoritesUseCase: ManageFavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<FavoritesUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<FavoritesUiEffect> = _uiEffect.receiveAsFlow()

    init {
        loadFavorites()
    }

    fun onEvent(event: FavoritesUiEvent) {
        when (event) {
            FavoritesUiEvent.Refresh -> loadFavorites()
            is FavoritesUiEvent.RemoveFavorite -> removeFavorite(event.articleId)
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            manageFavoritesUseCase.getFavoriteArticles().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        _uiState.value = FavoritesUiState(
                            isLoading = false,
                            favorites = result.data,
                            errorMessage = null
                        )
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                        _uiEffect.send(FavoritesUiEffect.ShowError(result.message))
                    }
                }
            }
        }
    }

    private fun removeFavorite(articleId: String) {
        viewModelScope.launch {
            when (val result = manageFavoritesUseCase.removeFromFavorites(articleId)) {
                is Result.Success -> {
                    _uiEffect.send(FavoritesUiEffect.ShowMessage("已從收藏中移除"))
                }

                is Result.Error -> {
                    _uiEffect.send(FavoritesUiEffect.ShowError(result.message))
                }

                Result.Loading -> Unit
            }
        }
    }
}

/**
 * UI 狀態
 */
data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favorites: List<NewsItem> = emptyList(),
    val errorMessage: String? = null
)

/**
 * UI 事件
 */
sealed interface FavoritesUiEvent {
    data object Refresh : FavoritesUiEvent
    data class RemoveFavorite(val articleId: String) : FavoritesUiEvent
}

/**
 * UI 效果
 */
sealed interface FavoritesUiEffect {
    data class ShowError(val message: String) : FavoritesUiEffect
    data class ShowMessage(val message: String) : FavoritesUiEffect
}
