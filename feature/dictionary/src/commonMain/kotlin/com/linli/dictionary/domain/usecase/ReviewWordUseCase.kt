package com.linli.dictionary.domain.usecase

import com.linli.dictionary.domain.model.ReviewCard
import com.linli.dictionary.domain.model.ReviewRating
import com.linli.dictionary.domain.repository.WordBankRepository
import com.linli.dictionary.domain.service.FsrsScheduler
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Instant as KotlinxInstant

/**
 * 更新單字複習記錄並回傳最新排程狀態。
 *
 * @property wordBankRepository 單字庫儲存庫
 * @property scheduler FSRS 排程器
 */
@OptIn(ExperimentalTime::class)
class ReviewWordUseCase(
    private val wordBankRepository: WordBankRepository,
    private val scheduler: FsrsScheduler
) {
    /**
     * 對單字進行複習評分並更新排程。
     *
     * @param word 要複習的單字
     * @param rating 使用者選擇的評分
     * @param now 當前時間，預設為系統時間
     * @return 更新後的複習卡片
     */
    suspend operator fun invoke(
        word: String,
        rating: ReviewRating,
        now: KotlinxInstant = KotlinxInstant.fromEpochMilliseconds(
            Clock.System.now().toEpochMilliseconds()
        )
    ): ReviewCard {
        val card = wordBankRepository.getReviewCard(word)
            ?: throw IllegalArgumentException("找不到要複習的單字：$word")

        val updatedCard = card.copy(
            metadata = scheduler.schedule(card.metadata, rating, now)
        )
        wordBankRepository.saveReviewCard(updatedCard)
        return updatedCard
    }
}
