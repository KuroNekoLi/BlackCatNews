package com.linli.dictionary.presentation

import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.domain.usecase.GetRecentSearchesUseCase
import com.linli.dictionary.domain.usecase.LookupWordUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 詞典功能的視圖模型。
 */
class DictionaryViewModel(
    private val lookupWordUseCase: LookupWordUseCase,
    private val getRecentSearchesUseCase: GetRecentSearchesUseCase,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    // UI state for the dictionary screen
    private val _state = MutableStateFlow(DictionaryState())
    val state: StateFlow<DictionaryState> = _state

    init {
        loadRecentSearches()
    }

    /**
     * 在詞典中查詢單字。
     */
    fun lookupWord(word: String) {
        if (word.isBlank()) return

        _state.update { it.copy(isLoading = true, error = null) }

        coroutineScope.launch {
            lookupWordUseCase(word).fold(
                onSuccess = { result ->
                    _state.update {
                        it.copy(
                            word = result,
                            isLoading = false,
                            error = null
                        )
                    }
                    loadRecentSearches()
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Unknown error"
                        )
                    }
                }
            )
        }
    }

    /**
     * 從儲存庫載入最近的搜尋記錄。
     */
    private fun loadRecentSearches() {
        coroutineScope.launch {
            val recentSearches = getRecentSearchesUseCase()
            _state.update { it.copy(recentSearches = recentSearches) }
        }
    }

    /**
     * 清除當前單字並重置為初始狀態。
     */
    fun clearSearch() {
        _state.update {
            it.copy(
                word = null,
                error = null
            )
        }
    }
}

/**
 * 詞典功能的狀態類。
 */
data class DictionaryState(
    val word: Word? = null,
    val recentSearches: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)