package com.linli.dictionary.domain.usecase

import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.domain.repository.DictionaryRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * GetRecentSearchesUseCase 的單元測試
 */
class GetRecentSearchesUseCaseTest {
    // 被測試的 use case
    private lateinit var getRecentSearchesUseCase: GetRecentSearchesUseCase

    // Mock 數據倉庫
    private lateinit var mockRepository: MockDictionaryRepository

    @BeforeTest
    fun setup() {
        mockRepository = MockDictionaryRepository()
        getRecentSearchesUseCase = GetRecentSearchesUseCase(mockRepository)
    }

    @Test
    fun getRecentSearches_returnsOrderedList() = runTest {
        // 準備 Mock 數據倉庫返回的最近搜索記錄
        val expectedSearches = listOf("apple", "banana", "orange")
        mockRepository.recentSearchesToReturn = expectedSearches

        // 調用被測試的 use case
        val result = getRecentSearchesUseCase()

        // 斷言結果
        assertEquals(expectedSearches, result)
        assertEquals(1, mockRepository.getRecentSearchesCallCount)
    }

    @Test
    fun getRecentSearches_emptyList_returnsEmptyList() = runTest {
        // 準備 Mock 數據倉庫返回的最近搜索記錄為空
        mockRepository.recentSearchesToReturn = emptyList()

        // 調用被測試的 use case
        val result = getRecentSearchesUseCase()

        // 斷言結果
        assertEquals(0, result.size)
        assertEquals(1, mockRepository.getRecentSearchesCallCount)
    }

    /**
     * Mock 數據倉庫
     */
    private class MockDictionaryRepository : DictionaryRepository {
        // 被測試的方法調用次數
        var recentSearchesToReturn: List<String> = emptyList()
        var lookupWordCallCount = 0
        var getRecentSearchesCallCount = 0
        var saveRecentSearchCallCount = 0

        override suspend fun lookupWord(word: String): Result<Word> {
            lookupWordCallCount++
            return Result.failure(Exception("Not implemented for this test"))
        }

        override suspend fun getRecentSearches(): List<String> {
            getRecentSearchesCallCount++
            return recentSearchesToReturn
        }

        override suspend fun saveRecentSearch(word: String) {
            saveRecentSearchCallCount++
        }
    }
}