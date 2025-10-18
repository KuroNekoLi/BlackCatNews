package com.linli.blackcatnews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linli.blackcatnews.domain.model.NewsItem
import com.linli.blackcatnews.domain.repository.Result
import com.linli.blackcatnews.domain.usecase.GetAllArticlesUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * 搜尋畫面 ViewModel
 * 處理文章搜尋邏輯
 */
@OptIn(FlowPreview::class)
class SearchViewModel(
    private val getAllArticlesUseCase: GetAllArticlesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        // Auto-search when user stops typing for 500ms
        viewModelScope.launch {
            queryFlow
                .debounce(500)
                .filter { it.length >= 2 } // Only search if query has at least 2 characters
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isNotEmpty()) {
                        performSearch(query)
                    }
                }
        }
    }

    fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.QueryChanged -> {
                _uiState.value = _uiState.value.copy(query = event.query)
                queryFlow.value = event.query

                // Clear results if query is empty
                if (event.query.isEmpty()) {
                    _uiState.value = _uiState.value.copy(results = emptyList())
                }
            }

            SearchUiEvent.Search -> {
                val query = _uiState.value.query
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
            }

            SearchUiEvent.ClearQuery -> {
                _uiState.value = SearchUiState()
                queryFlow.value = ""
            }
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Search in all articles using GetAllArticlesUseCase
            getAllArticlesUseCase(count = 20, forceRefresh = false).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        val filteredResults = result.data.filter { article ->
                            searchMatches(article, query)
                        }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            results = filteredResults,
                            errorMessage = null
                        )
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    private fun searchMatches(article: NewsItem, query: String): Boolean {
        val queryLower = query.lowercase()
        return article.title.lowercase().contains(queryLower) ||
                (article.titleZh?.lowercase()?.contains(queryLower) == true) ||
                article.summary.lowercase().contains(queryLower) ||
                (article.summaryZh?.lowercase()?.contains(queryLower) == true)
    }
}

/**
 * 搜尋 UI 狀態
 */
data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<NewsItem> = emptyList(),
    val errorMessage: String? = null
)

/**
 * 搜尋 UI 事件
 */
sealed interface SearchUiEvent {
    data class QueryChanged(val query: String) : SearchUiEvent
    data object Search : SearchUiEvent
    data object ClearQuery : SearchUiEvent
}
