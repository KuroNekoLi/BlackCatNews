package com.linli.dictionary.domain.usecase

import com.linli.dictionary.data.mock.MockDictionaryData
import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.domain.repository.DictionaryRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetRecentSearchesUseCaseTest {

    private lateinit var useCase: GetRecentSearchesUseCase
    private lateinit var repository: MockDictionaryRepository

    @BeforeTest
    fun setUp() {
        repository = MockDictionaryRepository()
        useCase = GetRecentSearchesUseCase(repository)
    }

    @Test
    fun `invoke returns recent searches from repository`() = runTest {
        val mockRecentSearches = MockDictionaryData.getMockRecentSearches()
        repository.mockRecentSearches = mockRecentSearches

        val result = useCase()

        assertEquals(mockRecentSearches, result)
    }

    /**
     * Mock implementation of DictionaryRepository for testing.
     */
    class MockDictionaryRepository : DictionaryRepository {
        var mockWordResult: Result<Word> = Result.success(MockDictionaryData.getMockWord())
        var mockRecentSearches = MockDictionaryData.getMockRecentSearches()

        override suspend fun lookupWord(word: String): Result<Word> {
            return mockWordResult
        }

        override suspend fun getRecentSearches(): List<String> {
            return mockRecentSearches
        }

        override suspend fun saveRecentSearch(word: String) {
            // Do nothing in test
        }
    }
}