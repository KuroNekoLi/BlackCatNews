package com.linli.dictionary.domain.repository

import com.linli.dictionary.domain.model.Word

/**
 * 詞典操作的儲存庫介面。
 */
interface DictionaryRepository {
    /**
     * 在詞典中查詢單字。
     *
     * @param word 要查詢的單字。
     * @return 單字及其定義和發音，如果未找到則返回失敗結果。
     */
    suspend fun lookupWord(word: String): Result<Word>

    /**
     * 取得最近搜尋的單字。
     *
     * @return 最近搜尋單字的列表。
     */
    suspend fun getRecentSearches(): List<String>

    /**
     * 將單字儲存到最近搜尋記錄中。
     *
     * @param word 要儲存的單字。
     */
    suspend fun saveRecentSearch(word: String)
}