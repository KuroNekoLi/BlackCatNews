package com.linli.dictionary.domain.repository

import com.linli.dictionary.domain.model.Word

/**
 * 單字庫操作的儲存庫介面。
 * 提供管理使用者儲存單字的功能，例如添加、移除和查詢單字。
 */
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
}