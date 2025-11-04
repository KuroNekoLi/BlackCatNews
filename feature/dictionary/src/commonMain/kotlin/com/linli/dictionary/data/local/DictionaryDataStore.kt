package com.linli.dictionary.data.local

/**
 * 管理最近詞典搜尋記錄的介面。
 */
interface DictionaryDataStore {
    /**
     * 取得最近搜尋的單字。
     *
     * @return 最近搜尋單字的列表。
     */
    suspend fun getRecentSearches(): List<String>

    /**
     * 將單字儲存到最近搜尋記錄中。
     * 應維持最近搜尋記錄的數量限制（例如 10 筆）並避免重複。
     *
     * @param word 要儲存的單字。
     */
    suspend fun saveRecentSearch(word: String)
}