package com.linli.dictionary.domain.usecase

import com.linli.dictionary.data.mock.MockDictionaryData
import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.domain.repository.DictionaryRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LookupWordUseCaseTest {

    private lateinit var useCase: LookupWordUseCase
    private lateinit var repository: MockDictionaryRepository

    @BeforeTest
    fun setUp() {
        repository = MockDictionaryRepository()
        useCase = LookupWordUseCase(repository)
    }

    @Test
    fun `invoke returns success from repository`() = runTest {
        val testWord = "test"
        val mockWord = MockDictionaryData.getMockWord(testWord)
        repository.mockWordResult = Result.success(mockWord)

        val result = useCase(testWord)

        assertTrue(result.isSuccess)
        assertEquals(mockWord, result.getOrNull())
    }

    @Test
    fun `invoke returns failure from repository`() = runTest {
        val testWord = "test"
        val exception = RuntimeException("Network error")
        repository.mockWordResult = Result.failure(exception)

        val result = useCase(testWord)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke with empty word returns failure`() = runTest {
        val result = useCase("")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `invoke with blank word returns failure`() = runTest {
        val result = useCase("  ")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `invoke trims and lowercases the word`() = runTest {
        val originalWord = "  TeSt  "
        val expectedWord = "test"
        val mockWord = MockDictionaryData.getMockWord(expectedWord)
        repository.mockWordResult = Result.success(mockWord)

        useCase(originalWord)

        assertEquals(expectedWord, repository.lastSavedWord)
        assertEquals(expectedWord, repository.lastLookedUpWord)
    }

    /**
     * Mock implementation of DictionaryRepository for testing.
     */
    class MockDictionaryRepository : DictionaryRepository {
        var mockWordResult: Result<Word> = Result.success(MockDictionaryData.getMockWord())
        var mockRecentSearches = MockDictionaryData.getMockRecentSearches()
        var lastLookedUpWord: String? = null
        var lastSavedWord: String? = null

        override suspend fun lookupWord(word: String): Result<Word> {
            lastLookedUpWord = word
            return mockWordResult
        }

        override suspend fun getRecentSearches(): List<String> {
            return mockRecentSearches
        }

        override suspend fun saveRecentSearch(word: String) {
            lastSavedWord = word
        }
    }
}