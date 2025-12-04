package com.linli.dictionary.domain.usecase

import com.linli.dictionary.domain.model.ReviewCard
import com.linli.dictionary.domain.repository.WordBankRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Instant as KotlinxInstant

/**
 * 取得到期需要複習的單字卡片。
 *
 * @property wordBankRepository 單字庫儲存庫
 */
@OptIn(ExperimentalTime::class)
class GetDueReviewCardsUseCase(
    private val wordBankRepository: WordBankRepository
) {
    /**
     * 取得到期的複習卡片，依到期時間排序。
     *
     * @param now 當前時間，預設為系統時間
     * @return 到期需要複習的卡片列表
     */
    suspend operator fun invoke(
        now: KotlinxInstant = KotlinxInstant.fromEpochMilliseconds(
            Clock.System.now().toEpochMilliseconds()
        )
    ): List<ReviewCard> {
        return wordBankRepository.getDueReviewCards(now).sortedBy { it.metadata.dueAt }
    }
}
