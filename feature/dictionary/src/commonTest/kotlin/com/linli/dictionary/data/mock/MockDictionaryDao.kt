package com.linli.dictionary.data.mock

import com.linli.dictionary.data.local.dao.DictionaryDao
import com.linli.dictionary.data.local.entity.WordEntity

/**
 * 模擬 DictionaryDao 的測試實現
 *
 * 用於單元測試中模擬數據庫操作，不需要真實的數據庫連接
 */
class MockDictionaryDao : DictionaryDao {
    // 模擬內部存儲
    private val wordEntities = mutableMapOf<String, WordEntity>()
    private val searchHistory = mutableListOf<String>()

    // 記錄方法調用次數，用於測試驗證
    var getWordByNameCallCount = 0
    var insertWordCallCount = 0
    var getRecentSearchesCallCount = 0
    var deleteWordCallCount = 0
    var deleteAllWordsCallCount = 0

    override suspend fun getWordByName(word: String): WordEntity? {
        getWordByNameCallCount++
        return wordEntities[word]
    }

    override suspend fun insertWord(wordEntity: WordEntity) {
        insertWordCallCount++
        wordEntities[wordEntity.word] = wordEntity

        // 更新搜尋歷史記錄
        searchHistory.remove(wordEntity.word) // 移除舊記錄（如果存在）
        searchHistory.add(0, wordEntity.word) // 添加到最前面

        // 保持搜尋歷史記錄數量在限制範圍內
        if (searchHistory.size > 10) {
            searchHistory.removeAt(searchHistory.lastIndex)
        }
    }

    override suspend fun getRecentSearches(limit: Int): List<WordEntity> {
        getRecentSearchesCallCount++
        return searchHistory.take(limit)
            .mapNotNull { wordEntities[it] }
    }

    override suspend fun deleteWord(word: String) {
        deleteWordCallCount++
        wordEntities.remove(word)
        searchHistory.remove(word)
    }

    override suspend fun deleteAllWords() {
        deleteAllWordsCallCount++
        wordEntities.clear()
        searchHistory.clear()
    }

    // 測試輔助方法：清除所有計數器
    fun clearCounters() {
        getWordByNameCallCount = 0
        insertWordCallCount = 0
        getRecentSearchesCallCount = 0
        deleteWordCallCount = 0
        deleteAllWordsCallCount = 0
    }

    // 測試輔助方法：預設添加單字
    fun preloadWord(wordEntity: WordEntity) {
        wordEntities[wordEntity.word] = wordEntity
        searchHistory.remove(wordEntity.word)
        searchHistory.add(0, wordEntity.word)
    }
}