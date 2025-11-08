package com.linli.dictionary.data.repository

import com.linli.dictionary.data.local.dao.DictionaryDao
import com.linli.dictionary.data.local.entity.DefinitionEntity
import com.linli.dictionary.data.local.entity.EntryEntity
import com.linli.dictionary.data.local.entity.WordEntity
import com.linli.dictionary.data.model.DefinitionDto
import com.linli.dictionary.data.model.EntryDto
import com.linli.dictionary.data.model.PronunciationsDto
import com.linli.dictionary.data.model.WordResponseDto
import com.linli.dictionary.data.remote.DictionaryApi
import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.domain.repository.WordBankRepository
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Fake 資源來源實現
 */
class FakeDictionaryDao : DictionaryDao {
    private val wordsMap = mutableMapOf<String, WordEntity>()

    override suspend fun getWordByName(word: String): WordEntity? = wordsMap[word]

    override suspend fun insertWord(wordEntity: WordEntity) {
        wordsMap[wordEntity.word] = wordEntity
    }

    override suspend fun getRecentSearches(limit: Int): List<WordEntity> {
        return wordsMap.values.toList().sortedByDescending { it.timestamp }.take(limit)
    }

    override suspend fun deleteWord(word: String) {
        wordsMap.remove(word)
    }

    override suspend fun deleteAllWords() {
        wordsMap.clear()
    }

    override suspend fun getWordsInWordBank(): List<WordEntity> {
        return wordsMap.values.filter { it.isInWordBank }
    }

    override suspend fun addToWordBank(word: String): Int {
        val entity = wordsMap[word]
        return if (entity != null) {
            wordsMap[word] = entity.copy(isInWordBank = true)
            1
        } else {
            0
        }
    }

    override suspend fun removeFromWordBank(word: String): Int {
        val entity = wordsMap[word]
        return if (entity != null && entity.isInWordBank) {
            wordsMap[word] = entity.copy(isInWordBank = false)
            1
        } else {
            0
        }
    }

    override suspend fun getWordBankCount(): Int {
        return wordsMap.values.count { it.isInWordBank }
    }
}

/**
 * Fake API 實現
 */
class FakeDictionaryApi : DictionaryApi {
    private val wordsData = mutableMapOf<String, Word>()

    fun addWordData(word: String, wordData: Word) {
        wordsData[word] = wordData
    }

    override suspend fun lookupWord(word: String): WordResponseDto {
        val wordData = wordsData[word] ?: throw Exception("Word not found: $word")

        return WordResponseDto(
            word = wordData.word,
            pronunciations = PronunciationsDto(
                uk = wordData.pronunciations.uk,
                us = wordData.pronunciations.us
            ),
            entries = wordData.entries.map { entry ->
                EntryDto(
                    partOfSpeech = entry.partOfSpeech,
                    definitions = entry.definitions.map { def ->
                        DefinitionDto(
                            enDefinition = def.enDefinition,
                            zhDefinition = def.zhDefinition,
                            examples = def.examples
                        )
                    }
                )
            }
        )
    }
}

/**
 * 單字庫儲存庫的單元測試
 *
 * 使用 Fake 資源來源以實現真正的跨平台測試
 */
class WordBankRepositoryImplTest {

    // 測試相關的物件
    private lateinit var fakeDictionaryDao: FakeDictionaryDao
    private lateinit var fakeDictionaryApi: FakeDictionaryApi
    private lateinit var repository: WordBankRepository

    // 測試用的 JSON 對象
    private val json = Json { ignoreUnknownKeys = true }

    // 測試數據
    private val testEntriesJson = json.encodeToString(
        listOf(
            EntryEntity(
                partOfSpeech = "n.",
                definitions = listOf(
                    DefinitionEntity(
                        enDefinition = "An animal of the feline family",
                        zhDefinition = "貓科動物",
                        examples = listOf("A black cat crossed the road.")
                    )
                )
            )
        )
    )

    private val testWordEntity = WordEntity(
        word = "cat",
        ukPronunciation = "/kæt/",
        usPronunciation = "/kæt/",
        entriesJson = testEntriesJson,
        isInWordBank = true
    )

    private val testWord = Word(
        word = "cat",
        pronunciations = Word.Pronunciations(uk = "/kæt/", us = "/kæt/"),
        entries = listOf(
            Word.Entry(
                partOfSpeech = "n.",
                definitions = listOf(
                    Word.Definition(
                        enDefinition = "An animal of the feline family",
                        zhDefinition = "貓科動物",
                        examples = listOf("A black cat crossed the road.")
                    )
                )
            )
        )
    )

    @BeforeTest
    fun setup() {
        // 直接實例化依賴
        fakeDictionaryDao = FakeDictionaryDao()
        fakeDictionaryApi = FakeDictionaryApi()
        repository = WordBankRepositoryImpl(fakeDictionaryApi, fakeDictionaryDao)

        // 初始化測試數據
        fakeDictionaryApi.addWordData("cat", testWord)
    }

    @Test
    fun getSavedWords_returnsWordBankList() = runTest {
        // 新增一個單字到單字庫
        fakeDictionaryDao.insertWord(testWordEntity)

        // 執行測試
        val result = repository.getSavedWords()

        // 驗證結果
        assertEquals(1, result.size)
        assertEquals("cat", result[0].word)
    }

    @Test
    fun addWordToWordBank_whenWordExists_setsFlag() = runTest {
        // 新增一個單字到數據庫，但不在單字庫中
        fakeDictionaryDao.insertWord(testWordEntity.copy(isInWordBank = false))

        // 執行測試
        val result = repository.addWordToWordBank("cat")

        // 驗證結果
        assertTrue(result.isSuccess)
        val savedWords = repository.getSavedWords()
        assertEquals(1, savedWords.size)
        assertEquals("cat", savedWords[0].word)
    }

    @Test
    fun addWordToWordBank_whenWordDoesNotExist_fetchesAndSaves() = runTest {
        // 執行測試
        val result = repository.addWordToWordBank("cat")

        // 驗證結果
        assertTrue(result.isSuccess)
        val savedWords = repository.getSavedWords()
        assertEquals(1, savedWords.size)
        assertEquals("cat", savedWords[0].word)
    }

    @Test
    fun removeWordFromWordBank_removesFlag() = runTest {
        // 新增一個單字到單字庫
        fakeDictionaryDao.insertWord(testWordEntity)

        // 執行測試
        val result = repository.removeWordFromWordBank("cat")

        // 驗證結果
        assertTrue(result.isSuccess)
        val savedWords = repository.getSavedWords()
        assertEquals(0, savedWords.size)
    }

    @Test
    fun removeWordFromWordBank_whenWordNotInBank_returnsFalse() = runTest {
        // 執行測試
        val result = repository.removeWordFromWordBank("nonexistent")

        // 驗證結果
        assertTrue(result.isFailure)
    }

    @Test
    fun isWordInWordBank_returnsTrueForBankedWords() = runTest {
        // 新增兩個單字到數據庫，一個在單字庫中，一個不在
        fakeDictionaryDao.insertWord(testWordEntity) // isInWordBank = true
        fakeDictionaryDao.insertWord(testWordEntity.copy(word = "dog", isInWordBank = false))

        // 執行測試
        assertTrue(repository.isWordInWordBank("cat"))
        assertFalse(repository.isWordInWordBank("dog"))
        assertFalse(repository.isWordInWordBank("nonexistent"))
    }

    @Test
    fun getWordBankCount_returnsCorrectCount() = runTest {
        // 新增三個單字到數據庫，兩個在單字庫中，一個不在
        fakeDictionaryDao.insertWord(testWordEntity) // cat, isInWordBank = true
        fakeDictionaryDao.insertWord(testWordEntity.copy(word = "dog", isInWordBank = true))
        fakeDictionaryDao.insertWord(testWordEntity.copy(word = "bird", isInWordBank = false))

        // 執行測試
        val count = repository.getWordBankCount()

        // 驗證結果
        assertEquals(2, count)
    }
}