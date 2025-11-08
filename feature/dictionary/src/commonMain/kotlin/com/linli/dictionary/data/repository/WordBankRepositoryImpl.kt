package com.linli.dictionary.data.repository

import com.linli.dictionary.data.local.dao.DictionaryDao
import com.linli.dictionary.data.local.entity.EntryEntity
import com.linli.dictionary.data.mapper.toDomain
import com.linli.dictionary.data.remote.DictionaryApi
import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.domain.repository.WordBankRepository
import kotlinx.serialization.json.Json

/**
 * WordBankRepository 的實現類，負責管理用戶的單字庫。
 * 通過在 WordEntity 上添加 isInWordBank 標記來區別單字是否存在於單字庫中。
 */
class WordBankRepositoryImpl(
    private val api: DictionaryApi,
    private val dao: DictionaryDao
) : WordBankRepository {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * 獲取使用者單字庫中所有的單字。
     *
     * @return 儲存在單字庫中的單字列表。
     */
    override suspend fun getSavedWords(): List<Word> {
        return dao.getWordsInWordBank().map { entity ->
            convertWordEntityToDomain(entity)
        }
    }

    /**
     * 將單字添加到單字庫。
     *
     * @param word 要添加的單字。
     * @return 操作結果，成功時為 Unit，失敗時包含例外。
     */
    override suspend fun addWordToWordBank(word: String): Result<Unit> {
        return try {
            // 先檢查單字是否存在於本地數據庫
            val localWordEntity = dao.getWordByName(word)

            if (localWordEntity != null) {
                // 如果單字已在數據庫中，將添加到單字庫
                val affectedRows = dao.addToWordBank(word)
                if (affectedRows > 0) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("添加單字到單字庫失敗"))
                }
            } else {
                // 如果單字不在數據庫中，則先從 API 獲取單字定義
                val remoteWord = api.lookupWord(word).toDomain()

                // 將單字保存到數據庫，並標記為單字庫中的單字
                val wordEntity = com.linli.dictionary.data.local.entity.WordEntity(
                    word = remoteWord.word,
                    ukPronunciation = remoteWord.pronunciations.uk,
                    usPronunciation = remoteWord.pronunciations.us,
                    entriesJson = convertEntriesToJson(remoteWord),
                    isInWordBank = true
                )
                dao.insertWord(wordEntity)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 從單字庫移除單字。
     *
     * @param word 要移除的單字。
     * @return 操作結果，成功時為 Unit，失敗時包含例外。
     */
    override suspend fun removeWordFromWordBank(word: String): Result<Unit> {
        return try {
            val affectedRows = dao.removeFromWordBank(word)
            if (affectedRows > 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("從單字庫移除單字失敗"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 檢查單字是否已在單字庫中。
     *
     * @param word 要檢查的單字。
     * @return 如果單字在單字庫中則返回 true，否則返回 false。
     */
    override suspend fun isWordInWordBank(word: String): Boolean {
        val wordEntity = dao.getWordByName(word)
        return wordEntity?.isInWordBank ?: false
    }

    /**
     * 獲取單字庫中的單字數量。
     *
     * @return 單字庫中的單字總數。
     */
    override suspend fun getWordBankCount(): Int {
        return dao.getWordBankCount()
    }

    /**
     * 將 WordEntity 轉換為領域模型 Word
     */
    private fun convertWordEntityToDomain(entity: com.linli.dictionary.data.local.entity.WordEntity): Word {
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