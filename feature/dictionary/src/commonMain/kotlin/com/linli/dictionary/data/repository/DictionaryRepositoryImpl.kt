package com.linli.dictionary.data.repository

import com.linli.dictionary.data.local.dao.DictionaryDao
import com.linli.dictionary.data.local.entity.EntryEntity
import com.linli.dictionary.data.local.entity.WordEntity
import com.linli.dictionary.data.mapper.toDomain
import com.linli.dictionary.data.remote.DictionaryApi
import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.domain.repository.DictionaryRepository
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Implementation of DictionaryRepository that combines remote API and local storage.
 * Follows the Single Source of Truth (SSOT) principle by prioritizing local data.
 */
@OptIn(ExperimentalTime::class)
class DictionaryRepositoryImpl(
    private val api: DictionaryApi,
    private val dao: DictionaryDao
) : DictionaryRepository {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Looks up a word in the dictionary.
     * First checks if the word is available locally, then fallbacks to the API if needed.
     *
     * @param word The word to look up.
     * @return The word with its definitions and pronunciations wrapped in Result.
     */
    override suspend fun lookupWord(word: String): Result<Word> {
        return try {
            // 先嘗試從本地數據獲取
            val localWordEntity = dao.getWordByName(word)

            if (localWordEntity != null) {
                // 本地有數據，直接返回
                // 需要將 WordEntity 轉換為 Word 領域模型
                val domainWord = convertWordEntityToDomain(localWordEntity)
                Result.success(domainWord)
            } else {
                // 本地沒有，從API獲取
                val remoteWord = api.lookupWord(word).toDomain()
                println("dto: ${api.lookupWord(word)}")
                println("remoteWord: $remoteWord")

                // 將領域模型轉換為數據實體並保存到本地
                val wordEntity = WordEntity(
                    word = remoteWord.word,
                    ukPronunciation = remoteWord.pronunciations.uk,
                    usPronunciation = remoteWord.pronunciations.us,
                    entriesJson = convertEntriesToJson(remoteWord)
                )
                dao.insertWord(wordEntity)

                Result.success(remoteWord)
            }
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
        return dao.getRecentSearches().map { it.word }
    }

    /**
     * Saves a word to the recent searches.
     * This method might be called when user clicks on a word in the recent searches list.
     *
     * @param word The word to save.
     */
    override suspend fun saveRecentSearch(word: String) {
        // 檢查是否已經存在
        val existingWord = dao.getWordByName(word)

        // 如果已經存在，更新時間戳並移到最前面
        if (existingWord != null) {
            val updatedEntity =
                existingWord.copy(timestamp = Clock.System.now().toEpochMilliseconds())
            dao.insertWord(updatedEntity)
        }
        // 如果不存在，需要先查詢 API 並保存
    }

    /**
     * 將 WordEntity 轉換為領域模型 Word
     */
    private fun convertWordEntityToDomain(entity: WordEntity): Word {
        // 將 WordEntity 轉換為 Word 領域模型
        return Word(
            word = entity.word,
            pronunciations = Word.Pronunciations(
                uk = entity.ukPronunciation,
                us = entity.usPronunciation
            ),
            entries = json.decodeFromString<List<EntryEntity>>(entity.entriesJson)
                .map { entryEntity ->
                    Word.Entry(
                        partOfSpeech = entryEntity.partOfSpeech,
                        definitions = entryEntity.definitions.map { defEntity ->
                            Word.Definition(
                                enDefinition = defEntity.enDefinition,
                                zhDefinition = defEntity.zhDefinition,
                                examples = defEntity.examples
                            )
                        }
                    )
                }
        )
    }

    /**
     * 將領域模型 Word 的 entries 轉換為 JSON 字串
     */
    private fun convertEntriesToJson(word: Word): String {
        // 將領域模型 Word 的 entries 轉換為 EntryEntity 列表
        val entries = word.entries.map { entry ->
            EntryEntity(
                partOfSpeech = entry.partOfSpeech,
                definitions = entry.definitions.map { def ->
                    com.linli.dictionary.data.local.entity.DefinitionEntity(
                        enDefinition = def.enDefinition,
                        zhDefinition = def.zhDefinition,
                        examples = def.examples
                    )
                }
            )
        }
        return json.encodeToString(entries)
    }
}