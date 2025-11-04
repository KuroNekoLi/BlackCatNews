package com.linli.dictionary.data.local

import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

/**
 * iOS-specific implementation of DictionaryDataStore using NSUserDefaults.
 */
class IosDictionaryDataStore : DictionaryDataStore {

    companion object {
        private const val RECENT_SEARCHES_KEY = "recent_searches"
        private const val MAX_RECENT_SEARCHES = 10
    }

    private val userDefaults = NSUserDefaults.standardUserDefaults

    /**
     * Gets the recently searched words.
     *
     * @return A list of recently searched words.
     */
    override suspend fun getRecentSearches(): List<String> {
        val searchesJson = userDefaults.stringForKey(RECENT_SEARCHES_KEY) ?: ""
        return if (searchesJson.isNotEmpty()) {
            try {
                Json.decodeFromString<List<String>>(searchesJson)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    /**
     * Saves a word to the recent searches.
     * Maintains a limit of recent searches and prevents duplicates.
     *
     * @param word The word to save.
     */
    override suspend fun saveRecentSearch(word: String) {
        val formattedWord = word.trim().lowercase()

        // Return if the word is empty
        if (formattedWord.isEmpty()) {
            return
        }

        val currentSearches = getRecentSearches().toMutableList()

        // Remove if it already exists to avoid duplication
        currentSearches.remove(formattedWord)

        // Add at the beginning (most recent search)
        currentSearches.add(0, formattedWord)

        // Limit to MAX_RECENT_SEARCHES
        val limitedSearches = currentSearches.take(MAX_RECENT_SEARCHES)

        // Save to NSUserDefaults
        userDefaults.setObject(
            Json.encodeToString(limitedSearches),
            RECENT_SEARCHES_KEY
        )
    }
}