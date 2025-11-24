package com.linli.dictionary.domain.repository

import com.linli.dictionary.domain.model.ReviewCard
import com.linli.dictionary.domain.model.Word
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Instant as KotlinxInstant

/**
 * 單字庫操作的儲存庫介面。
 * 提供管理使用者儲存單字的功能，例如添加、移除和查詢單字。
 */
@OptIn(ExperimentalTime::class)
interface WordBankRepository {
    /**
     * 獲取使用者單字庫中所有的單字。
     *
     * @return 儲存在單字庫中的單字列表。
     */
    suspend fun getSavedWords(): List<Word>

    /**
     * 將單字添加到單字庫。
     *
     * @param word 要添加的單字。
     * @return 操作結果，成功時為 Unit，失敗時包含例外。
     */
    suspend fun addWordToWordBank(word: String): Result<Unit>

    /**
     * 從單字庫移除單字。
     *
     * @param word 要移除的單字。
     * @return 操作結果，成功時為 Unit，失敗時包含例外。
     */
    suspend fun removeWordFromWordBank(word: String): Result<Unit>

    /**
     * 檢查單字是否已在單字庫中。
     *
     * @param word 要檢查的單字。
     * @return 如果單字在單字庫中則返回 true，否則返回 false。
     */
    suspend fun isWordInWordBank(word: String): Boolean

    /**
     * 獲取單字庫中的單字數量。
     *
     * @return 單字庫中的單字總數。
     */
    suspend fun getWordBankCount(): Int

    /**
     * 取得到期需要複習的單字卡片。
     *
     * @param now 當前時間，用於篩選到期的複習卡
     * @return 待複習的卡片列表
     */
    suspend fun getDueReviewCards(now: KotlinxInstant): List<ReviewCard>

    /**
     * 取得指定單字的複習卡片資料。
     *
     * @param word 單字內容
     * @return 複習卡片，若未加入單字庫則回傳 null
     */
    suspend fun getReviewCard(word: String): ReviewCard?

    /**
     * 儲存更新後的複習卡片狀態。
     *
     * @param card 包含最新排程資料的卡片
     */
    suspend fun saveReviewCard(card: ReviewCard)

    /**
     * 重置單字的學習進度。
     *
     * @param word 單字內容
     */
    suspend fun resetWordProgress(word: String)
}
