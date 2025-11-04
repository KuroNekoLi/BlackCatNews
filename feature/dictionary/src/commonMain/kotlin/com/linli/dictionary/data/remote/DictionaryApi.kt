package com.linli.dictionary.data.remote

import com.linli.dictionary.data.model.WordResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * API interface for dictionary word lookups.
 */
interface DictionaryApi {
    /**
     * Looks up a word in the dictionary.
     *
     * @param word The word to look up.
     * @return The word with its definitions and pronunciations.
     */
    suspend fun lookupWord(word: String): WordResponseDto
}

/**
 * Default implementation of DictionaryApi.
 */
class DefaultDictionaryApi(private val httpClient: HttpClient) : DictionaryApi {

    /**
     * Base URL for the dictionary API.
     */
    private val baseUrl = "https://dictionary.kuronekoli.uk/api/v1/word"

    /**
     * Looks up a word in the dictionary.
     *
     * @param word The word to look up.
     * @return The word with its definitions and pronunciations.
     */
    override suspend fun lookupWord(word: String): WordResponseDto {
        return httpClient.get("$baseUrl/$word").body()
    }
}