package com.linli.dictionary.presentation

import com.linli.dictionary.data.mock.MockDictionaryData
import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.domain.repository.DictionaryRepository
import com.linli.dictionary.domain.usecase.GetRecentSearchesUseCase
import com.linli.dictionary.domain.usecase.LookupWordUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class DictionaryViewModelTest {

    private lateinit var viewModel: DictionaryViewModel
    private lateinit var lookupWordUseCase: MockLookupWordUseCase
    private lateinit var getRecentSearchesUseCase: MockGetRecentSearchesUseCase
    private lateinit var testScope: TestScope

    @BeforeTest
    fun setUp() {
        val testDispatcher = StandardTestDispatcher()
        testScope = TestScope(testDispatcher)
        lookupWordUseCase = MockLookupWordUseCase()
        getRecentSearchesUseCase = MockGetRecentSearchesUseCase()
        viewModel = DictionaryViewModel(
            lookupWordUseCase = lookupWordUseCase,
            getRecentSearchesUseCase = getRecentSearchesUseCase,
        )
    }

    @Test
    fun `initial state is correct`() = testScope.runTest {
        // Advance time to complete the initial loadRecentSearches call
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNull(state.word)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(MockDictionaryData.getMockRecentSearches(), state.recentSearches)
    }

    @Test
    fun `lookupWord with valid word updates state correctly`() = testScope.runTest {
        // Advance time to complete the initial setup
        advanceUntilIdle()

        val testWord = "test"
        val mockWord = MockDictionaryData.getMockWord(testWord)
        lookupWordUseCase.setNextResult(Result.success(mockWord))

        viewModel.lookupWord(testWord)

        // Advance time to complete the lookupWord operation
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(mockWord, state.word)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `lookupWord with error updates state correctly`() = testScope.runTest {
        // Advance time to complete the initial setup
        advanceUntilIdle()

        val testWord = "test"
        val errorMessage = "Network error"
        lookupWordUseCase.setNextResult(Result.failure(RuntimeException(errorMessage)))

        viewModel.lookupWord(testWord)

        // Advance time to complete the lookupWord operation
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNull(state.word)
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `lookupWord with blank word does nothing`() = testScope.runTest {
        // Advance time to complete the initial setup
        advanceUntilIdle()

        val initialState = viewModel.state.value
        viewModel.lookupWord(" ")
        assertEquals(initialState, viewModel.state.value)
    }

    @Test
    fun `clearSearch resets word and error`() = testScope.runTest {
        // Advance time to complete the initial setup
        advanceUntilIdle()

        // First set a word and error
        val testWord = "test"
        lookupWordUseCase.setNextResult(Result.failure(RuntimeException("Some error")))
        viewModel.lookupWord(testWord)

        // Advance time to complete the lookupWord operation
        advanceUntilIdle()

        // Then clear the search
        viewModel.clearSearch()

        // Check that word and error are reset
        val state = viewModel.state.value
        assertNull(state.word)
        assertNull(state.error)
    }

    /**
     * Mock implementation of LookupWordUseCase for testing.
     */
    class MockLookupWordUseCase : LookupWordUseCase {
        private var nextResult: Result<Word>? = null

        constructor() : super(MockRepository())

        fun setNextResult(result: Result<Word>) {
            nextResult = result
        }

        override suspend operator fun invoke(word: String): Result<Word> {
            return nextResult ?: Result.success(MockDictionaryData.getMockWord(word))
        }
    }

    /**
     * Mock implementation of GetRecentSearchesUseCase for testing.
     */
    class MockGetRecentSearchesUseCase : GetRecentSearchesUseCase {
        constructor() : super(MockRepository())

        override suspend operator fun invoke(): List<String> {
            return MockDictionaryData.getMockRecentSearches()
        }
    }

    /**
     * Mock repository for testing.
     */
    class MockRepository : DictionaryRepository {
        override suspend fun lookupWord(word: String): Result<Word> {
            return Result.success(MockDictionaryData.getMockWord(word))
        }

        override suspend fun getRecentSearches(): List<String> {
            return MockDictionaryData.getMockRecentSearches()
        }

        override suspend fun saveRecentSearch(word: String) {
            // Do nothing in the mock
        }
    }
}