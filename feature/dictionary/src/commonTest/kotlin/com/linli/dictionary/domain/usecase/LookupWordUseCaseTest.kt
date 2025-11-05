package com.linli.dictionary.domain.usecase

import com.linli.dictionary.data.mock.MockDictionaryData
import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.domain.repository.DictionaryRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * LookupWordUseCase 單元測試類別
 */
class LookupWordUseCaseTest {
    // 被測試的 use case
    private lateinit var lookupWordUseCase: LookupWordUseCase

    // Mock 存儲庫
    private lateinit var mockRepository: MockDictionaryRepository

    @BeforeTest
    fun setup() {
        mockRepository = MockDictionaryRepository()
        lookupWordUseCase = LookupWordUseCase(mockRepository)
    }

    @Test
    fun lookupWord_successfulLookup_returnsWordDetails() = runTest {
        // 準備：設定存儲庫返回成功結果
        val expectedWord = MockDictionaryData.getMockWord()
        mockRepository.wordToReturn = expectedWord

        // 執行測試
        val result = lookupWordUseCase("apple")

        // 驗證結果
        assertTrue(result.isSuccess)
        assertEquals(expectedWord, result.getOrNull())
        assertEquals(1, mockRepository.lookupWordCallCount)
    }

    @Test
    fun lookupWord_failedLookup_returnsError() = runTest {
        // 準備：設定存儲庫返回失敗結果
        mockRepository.errorToReturn = Exception("Dictionary error")

        // 執行測試
        val result = lookupWordUseCase("unknown")

        // 驗證結果
        assertTrue(result.isFailure)
        assertEquals("Dictionary error", result.exceptionOrNull()?.message)
        assertEquals(1, mockRepository.lookupWordCallCount)
    }

    /**
     * 模擬字典存儲庫
     */
    private class MockDictionaryRepository : DictionaryRepository {
        // 測試輔助設置
        var wordToReturn: Word? = null
        var errorToReturn: Exception? = null
        var lookupWordCallCount = 0
        var getRecentSearchesCallCount = 0
        var saveRecentSearchCallCount = 0

        override suspend fun lookupWord(word: String): Result<Word> {
            lookupWordCallCount++
            return errorToReturn?.let {
                Result.failure(it)
            } ?: wordToReturn?.let {
                Result.success(it)
            } ?: Result.failure(Exception("No mock response configured"))
        }

        override suspend fun getRecentSearches(): List<String> {
            getRecentSearchesCallCount++
            return emptyList()
        }

        override suspend fun saveRecentSearch(word: String) {
            saveRecentSearchCallCount++
        }
    }
}