package com.linli.dictionary.data.mock

import com.linli.dictionary.data.model.WordResponseDto
import com.linli.dictionary.data.remote.DictionaryApi

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