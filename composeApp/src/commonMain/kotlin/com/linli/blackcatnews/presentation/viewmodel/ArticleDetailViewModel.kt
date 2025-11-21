package com.linli.blackcatnews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linli.blackcatnews.domain.repository.Result
import com.linli.blackcatnews.domain.usecase.GetArticleDetailUseCase
import com.linli.blackcatnews.presentation.state.ArticleDetailUiEffect
import com.linli.blackcatnews.presentation.state.ArticleDetailUiEvent
import com.linli.blackcatnews.presentation.state.ArticleDetailUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ArticleDetailViewModel(
    private val articleId: String,
    private val getArticleDetailUseCase: GetArticleDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticleDetailUiState(isLoading = true))
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<ArticleDetailUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<ArticleDetailUiEffect> = _uiEffect.receiveAsFlow()

    init {
        loadArticle()
    }

    fun onEvent(event: ArticleDetailUiEvent) {
        when (event) {
            ArticleDetailUiEvent.Refresh -> loadArticle(force = true)
        }
    }

    private fun loadArticle(force: Boolean = false) {
        viewModelScope.launch {
            getArticleDetailUseCase(articleId).collect { result ->
                when (result) {
                    Result.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is Result.Success -> _uiState.value = ArticleDetailUiState(
                        isLoading = false,
                        article = result.data
                    )

                    is Result.Error -> {
                        _uiState.value = ArticleDetailUiState(
                            isLoading = false,
                            errorMessage = result.message
                        )
                        _uiEffect.send(ArticleDetailUiEffect.ShowError(result.message))
                    }
                }
            }
        }
    }


}
