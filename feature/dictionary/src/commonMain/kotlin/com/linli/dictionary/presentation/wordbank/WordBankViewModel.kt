package com.linli.dictionary.presentation.wordbank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.domain.usecase.AddWordToWordBankUseCase
import com.linli.dictionary.domain.usecase.GetSavedWordsUseCase
import com.linli.dictionary.domain.usecase.GetWordBankCountUseCase
import com.linli.dictionary.domain.usecase.IsWordInWordBankUseCase
import com.linli.dictionary.domain.usecase.RemoveWordFromWordBankUseCase
import com.linli.dictionary.domain.usecase.ResetWordProgressUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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
    private val getWordBankCountUseCase: GetWordBankCountUseCase,
    private val resetWordProgressUseCase: ResetWordProgressUseCase
) : ViewModel() {

    /**
     * 單字庫狀態
     */
    data class WordBankState(
        val savedWords: List<Word> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val wordCount: Int = 0,
        val sortOption: SortOption = SortOption.ALPHABETICAL_ASCENDING,
        val partOfSpeechFilter: String? = null,
        val availablePartOfSpeech: List<String> = emptyList()
    )

    /**
     * 單字庫列表的排序方式。
     */
    enum class SortOption(val displayName: String) {
        ALPHABETICAL_ASCENDING("字母 A → Z"),
        ALPHABETICAL_DESCENDING("字母 Z → A")
    }

    private var allSavedWords: List<Word> = emptyList()
    private val _uiState = MutableStateFlow(WordBankState())
    val uiState: StateFlow<WordBankState> = _uiState.asStateFlow()

    private val _refreshReviewEvent = Channel<Unit>()
    val refreshReviewEvent = _refreshReviewEvent.receiveAsFlow()

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
                allSavedWords = getSavedWordsUseCase()
                _uiState.update { state ->
                    state.copy(
                        savedWords = applyListOptions(
                            words = allSavedWords,
                            sortOption = state.sortOption,
                            partOfSpeechFilter = state.partOfSpeechFilter
                        ),
                        availablePartOfSpeech = allSavedWords
                            .flatMap { word -> word.entries.map { it.partOfSpeech } }
                            .distinct()
                            .sorted(),
                        isLoading = false
                    )
                }
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
     * 更新單字列表的排序方式。
     *
     * @param sortOption 使用者選擇的排序方式
     */
    fun updateSortOption(sortOption: SortOption) {
        _uiState.update { state ->
            state.copy(
                sortOption = sortOption,
                savedWords = applyListOptions(
                    words = allSavedWords,
                    sortOption = sortOption,
                    partOfSpeechFilter = state.partOfSpeechFilter
                )
            )
        }
    }

    /**
     * 依詞性篩選單字列表；傳入 null 時顯示所有單字。
     *
     * @param partOfSpeech 要顯示的詞性，null 表示不篩選
     */
    fun updatePartOfSpeechFilter(partOfSpeech: String?) {
        _uiState.update { state ->
            state.copy(
                partOfSpeechFilter = partOfSpeech,
                savedWords = applyListOptions(
                    words = allSavedWords,
                    sortOption = state.sortOption,
                    partOfSpeechFilter = partOfSpeech
                )
            )
        }
    }

    private fun applyListOptions(
        words: List<Word>,
        sortOption: SortOption,
        partOfSpeechFilter: String?
    ): List<Word> {
        val filteredWords = partOfSpeechFilter?.let { filter ->
            words.filter { word -> word.entries.any { it.partOfSpeech == filter } }
        } ?: words

        return when (sortOption) {
            SortOption.ALPHABETICAL_ASCENDING -> filteredWords.sortedBy { it.word.lowercase() }
            SortOption.ALPHABETICAL_DESCENDING -> filteredWords.sortedByDescending { it.word.lowercase() }
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
     * 重置單字的學習進度
     *
     * @param word 要重置的單字
     */
    fun resetWordProgress(word: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            resetWordProgressUseCase(word)
                .onSuccess {
                    // 重新載入以確保任何狀態顯示更新（雖然列表可能不顯示進度，但保持同步）
                    loadSavedWords()
                    // 同時重新載入單字庫數量，這會觸發 UI 的 LaunchedEffect 去刷新複習隊列
                    loadWordCount()
                    // 通知外部需要刷新複習列表
                    _refreshReviewEvent.send(Unit)
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            error = e.message ?: "無法重置單字進度",
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