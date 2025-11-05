package com.linli.dictionary.data.repository

import com.linli.dictionary.data.local.entity.DefinitionEntity
import com.linli.dictionary.data.local.entity.EntryEntity
import com.linli.dictionary.data.local.entity.WordEntity
import com.linli.dictionary.data.mock.MockDictionaryApi
import com.linli.dictionary.data.mock.MockDictionaryDao
import com.linli.dictionary.data.mock.MockDictionaryData
import com.linli.dictionary.domain.repository.DictionaryRepository
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * 字典存儲庫實作的測試類
 */
class DictionaryRepositoryImplTest {
    // 測試相關的相依元件
    private lateinit var mockApi: MockDictionaryApi
    private lateinit var mockDao: MockDictionaryDao
    private lateinit var repository: DictionaryRepository
    private val json = Json { ignoreUnknownKeys = true }

    // 測試用的範例單字與實體
    private val testWord = "to"
    private val mockWord = MockDictionaryData.getMockWord()

    /**
     * 每個測試案例執行前的設置
     */
    @BeforeTest
    fun setup() {
        mockApi = MockDictionaryApi()
        mockDao = MockDictionaryDao()
        repository = DictionaryRepositoryImpl(mockApi, mockDao)
    }

    /**
     * 每個測試案例完成後的清理
     */
    @AfterTest
    fun tearDown() {
        // 清除計數器
        mockDao.clearCounters()
    }

    /**
     * 測試在本地數據庫中取得單字
     */
    @Test
    fun lookupWord_localCacheHit_returnsFromCache() = runTest {
        // u6e96u5099uff1au9810u5148u5728u6578u64dau5eabu5b58u5165u4e00u500bu55aeu5b57
        val wordEntity = createTestWordEntity(testWord)
        mockDao.preloadWord(wordEntity)

        // u57f7u884cu6e2cu8a66
        val result = repository.lookupWord(testWord)

        // u9a57u8b49u7d50u679c
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals(testWord, result.getOrNull()?.word)
        assertEquals(
            1,
            mockDao.getWordByNameCallCount
        ) // u78bau8a8du78bau5be6u67e5u8a62u4e86u6578u64dau5eab
        assertEquals(
            0,
            mockDao.insertWordCallCount
        ) // u78bau8a8du6c92u6709u63d2u5165u65b0u8cc7u6599
    }

    /**
     * 測試在數據庫找不到則從 API 查詢單字
     */
    @Test
    fun lookupWord_localCacheMiss_fetchesFromApiAndSaves() = runTest {
        // 準備：確保數據庫中沒有單字
        mockApi.mockResponseDto = MockDictionaryData.getMockWordResponseDto()

        // 執行測試
        val result = repository.lookupWord(testWord)

        // 驗證結果
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals(testWord, result.getOrNull()?.word)
        assertEquals(1, mockDao.getWordByNameCallCount) // 確認查詢了數據庫
        assertEquals(1, mockDao.insertWordCallCount) // 確認存入了數據庫
    }

    /**
     * 測試查詢 API 出錯時的情況
     */
    @Test
    fun lookupWord_apiError_returnsFailure() = runTest {
        // 準備：設定 API 會拋出例外
        mockApi.exception = Exception("API Error")

        // 執行測試
        val result = repository.lookupWord(testWord)

        // 驗證結果
        assertTrue(result.isFailure)
        assertEquals("API Error", result.exceptionOrNull()?.message)
        assertEquals(1, mockDao.getWordByNameCallCount) // 確認查詢了數據庫
        assertEquals(
            0,
            mockDao.insertWordCallCount
        ) // 確認沒有存入數據庫
    }

    /**
     * 測試取得最近搜尋記錄
     */
    @Test
    fun getRecentSearches_returnsListOfWords() = runTest {
        // 準備：預先加入一些搜尋記錄
        mockDao.preloadWord(createTestWordEntity("apple"))
        mockDao.preloadWord(createTestWordEntity("banana"))
        mockDao.preloadWord(createTestWordEntity("orange"))

        // 執行測試
        val results = repository.getRecentSearches()

        // 驗證結果
        assertEquals(3, results.size)
        assertEquals("orange", results[0]) // 最新加入的應該在前面
        assertEquals("banana", results[1])
        assertEquals("apple", results[2])
        assertEquals(1, mockDao.getRecentSearchesCallCount)
    }

    /**
     * 測試保存最近搜尋
     */
    @Test
    fun saveRecentSearch_updatesSearchHistory() = runTest {
        // 執行測試
        repository.saveRecentSearch(testWord)

        // 驗證結果
        val results = repository.getRecentSearches()
        assertTrue(results.isEmpty()) // 因為需要完整資料才會顯示
        assertEquals(1, mockDao.getWordByNameCallCount) // 確認查詢了數據庫
    }

    /**
     * 建立一個用於測試的 WordEntity
     */
    private fun createTestWordEntity(word: String): WordEntity {
        val definitions = listOf(
            DefinitionEntity(
                enDefinition = "A fruit",
                zhDefinition = "一種水果",
                examples = listOf("This is a red apple.")
            )
        )

        val entries = listOf(
            EntryEntity(
                partOfSpeech = "n.",
                definitions = definitions
            )
        )

        return WordEntity(
            word = word,
            ukPronunciation = "/u00e6pu0259l/",
            usPronunciation = "/u00e6pu0259l/",
            entriesJson = json.encodeToString(entries)
        )
    }
}