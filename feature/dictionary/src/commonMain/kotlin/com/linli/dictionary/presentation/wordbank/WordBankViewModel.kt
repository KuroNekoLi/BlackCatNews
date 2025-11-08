package com.linli.dictionary.presentation.wordbank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.domain.usecase.AddWordToWordBankUseCase
import com.linli.dictionary.domain.usecase.GetSavedWordsUseCase
import com.linli.dictionary.domain.usecase.GetWordBankCountUseCase
import com.linli.dictionary.domain.usecase.IsWordInWordBankUseCase
import com.linli.dictionary.domain.usecase.RemoveWordFromWordBankUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 單字庫畫面的 ViewModel
 *
 * 該 ViewModel 負責管理單字庫相關的狀態和使用者交互。
 * 包括列出所有已儲存的單字、添加新單字、刪除現有單字等功能。
 */
class WordBankViewModel(
    private val getSavedWordsUseCase: GetSavedWordsUseCase,
    private val addWordToWordBankUseCase: AddWordToWordBankUseCase,
    private val removeWordFromWordBankUseCase: RemoveWordFromWordBankUseCase,
    private val isWordInWordBankUseCase: IsWordInWordBankUseCase,
    private val getWordBankCountUseCase: GetWordBankCountUseCase
) : ViewModel() {

    /**
     * 單字庫狀態
     */
    data class WordBankState(
        val savedWords: List<Word> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val wordCount: Int = 0
    )

    private val _uiState = MutableStateFlow(WordBankState())
    val uiState: StateFlow<WordBankState> = _uiState.asStateFlow()

    /**
     * 初始化 ViewModel 時加載已儲存的單字
     */
    init {
        loadSavedWords()
        loadWordCount()
    }

    /**
     * 加載儲存在單字庫中的所有單字
     */
    fun loadSavedWords() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val words = getSavedWordsUseCase()
                _uiState.update { it.copy(savedWords = words, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "無法加載已儲存的單字",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * 加載單字庫中的單字數量
     */
    fun loadWordCount() {
        viewModelScope.launch {
            try {
                val count = getWordBankCountUseCase()
                _uiState.update { it.copy(wordCount = count) }
            } catch (e: Exception) {
                // 忽略加載數量的錯誤
            }
        }
    }

    /**
     * 添加單字到單字庫
     *
     * @param word 要添加的單字
     */
    fun addWord(word: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            addWordToWordBankUseCase(word)
                .onSuccess {
                    loadSavedWords()
                    loadWordCount()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            error = e.message ?: "無法添加單字",
                            isLoading = false
                        )
                    }
                }
        }
    }

    /**
     * 從單字庫中刪除單字
     *
     * @param word 要刪除的單字
     */
    fun removeWord(word: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            removeWordFromWordBankUseCase(word)
                .onSuccess {
                    loadSavedWords()
                    loadWordCount()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            error = e.message ?: "無法刪除單字",
                            isLoading = false
                        )
                    }
                }
        }
    }

    /**
     * 檢查單字是否已經存在於單字庫中
     *
     * @param word 要檢查的單字
     * @return 單字是否存在於單字庫中
     */
    suspend fun isWordInWordBank(word: String): Boolean {
        return isWordInWordBankUseCase(word)
    }
}