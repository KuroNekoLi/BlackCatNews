package com.linli.dictionary.presentation.wordbank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linli.dictionary.domain.model.ReviewCard
import com.linli.dictionary.domain.model.ReviewRating
import com.linli.dictionary.domain.usecase.GetDueReviewCardsUseCase
import com.linli.dictionary.domain.usecase.ReviewWordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 單字複習流程的 ViewModel，負責取得到期卡片並處理評分。
 *
 * @property getDueReviewCardsUseCase 取得到期卡片的用例
 * @property reviewWordUseCase 對卡片進行評分排程的用例
 */
class WordReviewViewModel(
    private val getDueReviewCardsUseCase: GetDueReviewCardsUseCase,
    private val reviewWordUseCase: ReviewWordUseCase
) : ViewModel() {

    /**
     * 複習畫面的狀態
     *
     * @property currentCard 正在顯示的卡片
     * @property waitingCards 尚未複習的排程隊列
     * @property reviewedCount 本次 session 已完成的卡片數
     * @property totalDueCount 本次 session 的總到期數量
     * @property isLoading 是否正在載入資料或提交評分
     * @property error 目前的錯誤訊息
     * @property sessionCompleted 是否已完成本輪複習
     */
    data class ReviewUiState(
        val currentCard: ReviewCard? = null,
        val waitingCards: List<ReviewCard> = emptyList(),
        val reviewedCount: Int = 0,
        val totalDueCount: Int = 0,
        val isLoading: Boolean = false,
        val error: String? = null,
        val sessionCompleted: Boolean = false
    ) {
        /**
         * 剩餘待複習的卡片數，包含當前顯示的一張。
         */
        val dueCount: Int get() = (currentCard?.let { 1 } ?: 0) + waitingCards.size
    }

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    init {
        refreshQueue()
    }

    /**
     * 重新載入到期複習卡片。
     */
    fun refreshQueue() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { getDueReviewCardsUseCase() }
                .onSuccess { cards ->
                    val current = cards.firstOrNull()
                    val waiting = if (cards.isNotEmpty()) cards.drop(1) else emptyList()
                    _uiState.update {
                        it.copy(
                            currentCard = current,
                            waitingCards = waiting,
                            reviewedCount = 0,
                            totalDueCount = cards.size,
                            isLoading = false,
                            sessionCompleted = cards.isEmpty()
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "無法載入複習列表"
                        )
                    }
                }
        }
    }

    /**
     * 開始複習流程，若尚未有卡片則重載一次列表。
     */
    fun startSession() {
        viewModelScope.launch {
            if (_uiState.value.currentCard == null && _uiState.value.waitingCards.isEmpty()) {
                refreshQueue()
            } else {
                _uiState.update { it.copy(sessionCompleted = it.dueCount == 0) }
            }
        }
    }

    /**
     * 對目前卡片進行評分。
     *
     * @param rating 使用者選擇的評分
     */
    fun gradeCurrentCard(rating: ReviewRating) {
        val current = _uiState.value.currentCard ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { reviewWordUseCase(current.word.word, rating) }
                .onSuccess {
                    advanceQueue()
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "提交複習結果失敗"
                        )
                    }
                }
        }
    }

    private fun advanceQueue() {
        val state = _uiState.value
        val nextCard = state.waitingCards.firstOrNull()
        val remaining =
            if (state.waitingCards.isNotEmpty()) state.waitingCards.drop(1) else emptyList()
        _uiState.update {
            it.copy(
                currentCard = nextCard,
                waitingCards = remaining,
                reviewedCount = it.reviewedCount + 1,
                isLoading = false,
                sessionCompleted = nextCard == null
            )
        }
    }
}
