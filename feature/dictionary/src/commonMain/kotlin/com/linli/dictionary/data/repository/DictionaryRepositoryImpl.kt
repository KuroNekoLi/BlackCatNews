package com.linli.dictionary.data.repository

import com.linli.dictionary.data.local.DictionaryDataStore
import com.linli.dictionary.data.mapper.toDomain
import com.linli.dictionary.data.remote.DictionaryApi
import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.domain.repository.DictionaryRepository

/**
 * Implementation of DictionaryRepository that combines remote API and local storage.
 */
class DictionaryRepositoryImpl(
    private val api: DictionaryApi,
    private val dataStore: DictionaryDataStore
) : DictionaryRepository {

    /**
     * Looks up a word in the dictionary.
     *
     * @param word The word to look up.
     * @return The word with its definitions and pronunciations wrapped in Result.
     */
    override suspend fun lookupWord(word: String): Result<Word> {
        return try {
            val response = api.lookupWord(word)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets the recently searched words.
     *
     * @return A list of recently searched words.
     */
    override suspend fun getRecentSearches(): List<String> {
        return dataStore.getRecentSearches()
    }

    /**
     * Saves a word to the recent searches.
     *
     * @param word The word to save.
     */
    override suspend fun saveRecentSearch(word: String) {
        dataStore.saveRecentSearch(word)
    }
}