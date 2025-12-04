package com.linli.dictionary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.linli.dictionary.data.local.entity.WordEntity

/**
 * 字典資料庫存取介面，提供基本的增刪查改操作
 *
 * 這個 DAO (Data Access Object) 定義了所有與字典詞彙相關的資料庫操作
 */
@Dao
interface DictionaryDao {

    /**
     * 根據單字查詢定義
     *
     * 此方法僅返回一個詞彙結果（如果存在）
     *
     * @param word 要查詢的單字
     * @return 字典詞彙實體，如果不存在則返回 null
     */
    @Query("SELECT * FROM words WHERE word = :word LIMIT 1")
    suspend fun getWordByName(word: String): WordEntity?

    /**
     * 插入或更新單字詞彙
     *
     * 如果已存在相同 word 的詞彙，則替換已存在的記錄
     *
     * @param wordEntity 要儲存的單字詞彙實體
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(wordEntity: WordEntity)

    /**
     * 取得最近搜尋的詞彙記錄
     *
     * 按時間戳 (timestamp) 降序排列，返回最近查詢過的詞彙
     *
     * @param limit 限制回傳的結果數量，預設為 10 筆
     * @return 最近搜尋的詞彙列表
     */
    @Query("SELECT * FROM words ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentSearches(limit: Int = 10): List<WordEntity>

    /**
     * 從資料庫中刪除單字
     *
     * 移除指定的詞彙記錄
     *
     * @param word 要刪除的單字
     */
    @Query("DELETE FROM words WHERE word = :word")
    suspend fun deleteWord(word: String)

    /**
     * 刪除所有詞彙
     *
     * 清空整個字典表
     */
    @Query("DELETE FROM words")
    suspend fun deleteAllWords()

    /**
     * 獲取所有標記為單字庫中的單字
     *
     * 返回所有 isInWordBank 為 true 的詞彙
     *
     * @return 單字庫中的單字列表
     */
    @Query("SELECT * FROM words WHERE isInWordBank = 1")
    suspend fun getWordsInWordBank(): List<WordEntity>

    /**
     * 取得到期需要複習的單字
     *
     * @param now 目前時間戳
     * @return 符合到期條件的單字
     */
    @Query("SELECT * FROM words WHERE isInWordBank = 1 AND reviewDueAt <= :now")
    suspend fun getDueReviewWords(now: Long): List<WordEntity>

    /**
     * 將單字加入到單字庫
     *
     * 更新單字的 isInWordBank 字段為 true
     *
     * @param word 要加入單字庫的單字
     * @return 影響的行數，通常為 1
     */
    @Query("UPDATE words SET isInWordBank = 1 WHERE word = :word")
    suspend fun addToWordBank(word: String): Int

    /**
     * 從單字庫移除單字
     *
     * 更新單字的 isInWordBank 字段為 false
     *
     * @param word 要從單字庫移除的單字
     * @return 影響的行數，通常為 1
     */
    @Query("UPDATE words SET isInWordBank = 0 WHERE word = :word")
    suspend fun removeFromWordBank(word: String): Int

    /**
     * 獲取單字庫中的單字數量
     *
     * 計算 isInWordBank 為 true 的詞彙數量
     *
     * @return 單字庫中的單字總數
     */
    @Query("SELECT COUNT(*) FROM words WHERE isInWordBank = 1")
    suspend fun getWordBankCount(): Int
}
