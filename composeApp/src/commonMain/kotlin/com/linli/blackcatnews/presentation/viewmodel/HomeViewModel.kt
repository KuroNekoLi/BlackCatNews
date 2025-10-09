package com.linli.blackcatnews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linli.blackcatnews.data.preferences.UserPreferencesRepository
import com.linli.blackcatnews.domain.model.ArticleSection
import com.linli.blackcatnews.domain.model.NewsCategory
import com.linli.blackcatnews.domain.repository.Result
import com.linli.blackcatnews.domain.usecase.GetArticlesBySectionUseCase
import com.linli.blackcatnews.domain.usecase.RefreshArticlesUseCase
import com.linli.blackcatnews.presentation.state.HomeUiEffect
import com.linli.blackcatnews.presentation.state.HomeUiEvent
import com.linli.blackcatnews.presentation.state.HomeUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getArticlesBySectionUseCase: GetArticlesBySectionUseCase,
    private val refreshArticlesUseCase: RefreshArticlesUseCase,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isRefreshing = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<HomeUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<HomeUiEffect> = _uiEffect.receiveAsFlow()

    private var articlesJob: Job? = null

    init {
        observePreferences()
        observeArticles(
            category = NewsCategory.LATEST,
            forceRefresh = false,
            shouldClearExistingArticles = true
        )
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.Refresh -> refresh()
            is HomeUiEvent.SelectCategory -> handleCategorySelected(event.category)
            is HomeUiEvent.ToggleLanguage -> updateLanguagePreference(event.useChinese)
            HomeUiEvent.LoadPreferences -> observePreferences()
            is HomeUiEvent.OpenArticle -> Unit
        }
    }

    private fun observeArticles(
        category: NewsCategory,
        forceRefresh: Boolean,
        shouldClearExistingArticles: Boolean
    ) {
        articlesJob?.cancel()
        val targetSection = mapCategoryToSection(category)
        if (targetSection == null) {
            val currentState = _uiState.value
            _uiState.value = currentState.copy(
                selectedCategory = category,
                isRefreshing = false,
                articles = emptyList(),
                errorMessage = null
            )
            return
        }

        val currentState = _uiState.value
        val updatedArticles = if (shouldClearExistingArticles) {
            emptyList()
        } else {
            currentState.articles
        }

        _uiState.value = currentState.copy(
            selectedCategory = category,
            isRefreshing = true,
            articles = updatedArticles,
            errorMessage = null
        )

        articlesJob = viewModelScope.launch {
            getArticlesBySectionUseCase(
                section = targetSection,
                count = DEFAULT_ARTICLE_COUNT,
                forceRefresh = forceRefresh
            ).collectLatest { result ->
                when (result) {
                    is Result.Loading -> Unit
                    is Result.Success -> _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        articles = result.data,
                        errorMessage = null
                    )
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isRefreshing = false,
                            errorMessage = result.message
                        )
                        _uiEffect.send(HomeUiEffect.ShowError(result.message))
                    }
                }
            }
        }
    }

    private fun refresh() {
        val currentCategory = _uiState.value.selectedCategory
        viewModelScope.launch {
            val targetSection = mapCategoryToSection(currentCategory)
            if (targetSection == null) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    articles = emptyList(),
                    errorMessage = null
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isRefreshing = true, errorMessage = null)

            when (
                val result = refreshArticlesUseCase(
                    section = targetSection,
                    count = DEFAULT_ARTICLE_COUNT
                )
            ) {
                is Result.Success -> observeArticles(
                    category = currentCategory,
                    forceRefresh = true,
                    shouldClearExistingArticles = false
                )
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isRefreshing = false)
                    _uiEffect.send(HomeUiEffect.ShowError(result.message))
                }
                Result.Loading -> Unit
            }
        }
    }

    private fun handleCategorySelected(category: NewsCategory) {
        val isSelectingNewCategory = category != _uiState.value.selectedCategory
        observeArticles(
            category = category,
            forceRefresh = false,
            shouldClearExistingArticles = isSelectingNewCategory
        )
    }

    private fun updateLanguagePreference(useChinese: Boolean) {
        _uiState.value = _uiState.value.copy(prefersChinese = useChinese)
        viewModelScope.launch {
            preferencesRepository.setPrefersChinese(useChinese)
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesRepository.prefersChinese().collect { prefersChinese ->
                _uiState.value = _uiState.value.copy(prefersChinese = prefersChinese)
            }
        }
    }

    private fun mapCategoryToSection(category: NewsCategory): ArticleSection? {
        return when (category) {
            NewsCategory.LATEST -> ArticleSection.News
            NewsCategory.WORLD -> ArticleSection.World
            NewsCategory.TECH -> ArticleSection.Technology
            else -> null
        }
    }

    companion object {
        private const val DEFAULT_ARTICLE_COUNT: Int = 5
    }
}
