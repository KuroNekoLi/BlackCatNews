package com.linli.dictionary.data.repository

import com.linli.dictionary.data.local.DictionaryDataStore
import com.linli.dictionary.data.mock.MockDictionaryData
import com.linli.dictionary.data.model.WordResponseDto
import com.linli.dictionary.data.remote.DictionaryApi
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DictionaryRepositoryImplTest {

    private lateinit var repository: DictionaryRepositoryImpl
    private lateinit var api: MockDictionaryApi
    private lateinit var dataStore: MockDictionaryDataStore

    @BeforeTest
    fun setUp() {
        api = MockDictionaryApi()
        dataStore = MockDictionaryDataStore()
        repository = DictionaryRepositoryImpl(api, dataStore)
    }

    @Test
    fun `lookupWord returns successful result when API succeeds`() = runTest {
        val testWord = "test"
        val mockResponseDto = MockDictionaryData.getMockWordResponseDto(testWord)
        api.mockResponseDto = mockResponseDto

        val result = repository.lookupWord(testWord)

        assertTrue(result.isSuccess)
        assertEquals(testWord, result.getOrNull()?.word)
    }

    @Test
    fun `lookupWord returns failure result when API fails`() = runTest {
        val testWord = "test"
        val testException = RuntimeException("API error")
        api.exception = testException

        val result = repository.lookupWord(testWord)

        assertTrue(result.isFailure)
        assertEquals(testException, result.exceptionOrNull())
    }

    @Test
    fun `getRecentSearches returns list from dataStore`() = runTest {
        val mockSearches = MockDictionaryData.getMockRecentSearches()
        dataStore.mockRecentSearches = mockSearches

        val result = repository.getRecentSearches()

        assertEquals(mockSearches, result)
    }

    @Test
    fun `saveRecentSearch delegates to dataStore`() = runTest {
        val testWord = "test"

        repository.saveRecentSearch(testWord)

        assertEquals(testWord, dataStore.lastSavedWord)
    }

    /**
     * Mock implementation of DictionaryApi for testing.
     */
    class MockDictionaryApi : DictionaryApi {
        var mockResponseDto = MockDictionaryData.getMockWordResponseDto()
        var exception: Exception? = null

        override suspend fun lookupWord(word: String): WordResponseDto {
            exception?.let { throw it }
            return mockResponseDto
        }
    }

    /**
     * Mock implementation of DictionaryDataStore for testing.
     */
    class MockDictionaryDataStore : DictionaryDataStore {
        var mockRecentSearches = MockDictionaryData.getMockRecentSearches()
        var lastSavedWord: String? = null

        override suspend fun getRecentSearches(): List<String> {
            return mockRecentSearches
        }

        override suspend fun saveRecentSearch(word: String) {
            lastSavedWord = word
        }
    }
}