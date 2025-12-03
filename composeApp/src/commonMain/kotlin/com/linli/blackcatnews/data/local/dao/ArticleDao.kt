package com.linli.blackcatnews.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.linli.blackcatnews.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

/**
 * 文章數據訪問對象
 *
 * Room DAO，定義所有文章相關的數據庫操作
 * 使用 Flow 實現響應式數據流，當數據變化時自動通知觀察者
 *
 * @see ArticleEntity
 */
@Dao
interface ArticleDao {

    /**
     * 插入文章列表
     *
     * 如果文章已存在（相同 ID），則替換
     *
     * @param articles 要插入的文章列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    /**
     * 插入單篇文章
     *
     * @param article 要插入的文章
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    /**
     * 更新文章
     *
     * @param article 要更新的文章
     */
    @Update
    suspend fun updateArticle(article: ArticleEntity)

    /**
     * 獲取所有文章（按更新時間降序）
     *
     * @return Flow<List<ArticleEntity>> 文章列表的數據流
     */
    @Query("SELECT * FROM articles ORDER BY updatedAt DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    /**
     * 根據分類獲取文章
     *
     * @param section 新聞分類（news, world, technology）
     * @param limit 返回數量限制
     * @return Flow<List<ArticleEntity>> 文章列表的數據流
     */
    @Query("SELECT * FROM articles WHERE section = :section ORDER BY updatedAt DESC LIMIT :limit")
    fun getArticlesBySection(section: String, limit: Int): Flow<List<ArticleEntity>>

    /**
     * 根據 ID 獲取單篇文章
     *
     * @param id 文章 ID
     * @return Flow<ArticleEntity?> 文章的數據流
     */
    @Query("SELECT * FROM articles WHERE id = :id")
    fun getArticleById(id: String): Flow<ArticleEntity?>

    /**
     * 取得單篇文章（非 Flow 版本）
     *
     * 用於需要同步查詢時（例如刷新數據時保持收藏狀態）
     *
     * @param id 文章 ID
     * @return ArticleEntity? 查詢結果
     */
    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticleByIdOnce(id: String): ArticleEntity?

    /**
     * 獲取收藏的文章
     *
     * @return Flow<List<ArticleEntity>> 收藏文章列表的數據流
     */
    @Query("SELECT * FROM articles WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteArticles(): Flow<List<ArticleEntity>>

    /**
     * 設置文章收藏狀態
     *
     * @param id 文章 ID
     * @param isFavorite 是否收藏
     */
    @Query("UPDATE articles SET isFavorite = :isFavorite, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean, updatedAt: Long)

    /**
     * 刪除單篇文章
     *
     * @param id 文章 ID
     */
    @Query("DELETE FROM articles WHERE id = :id")
    suspend fun deleteArticleById(id: String)

    /**
     * 刪除所有文章
     */
    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()

    /**
     * 刪除指定分類的文章
     *
     * @param section 新聞分類
     */
    @Query("DELETE FROM articles WHERE section = :section")
    suspend fun deleteArticlesBySection(section: String)

    /**
     * 獲取文章總數
     *
     * @return 文章總數
     */
    @Query("SELECT COUNT(*) FROM articles")
    suspend fun getArticleCount(): Int

    /**
     * 獲取指定分類的文章數量
     *
     * @param section 新聞分類
     * @return 文章數量
     */
    @Query("SELECT COUNT(*) FROM articles WHERE section = :section")
    suspend fun getArticleCountBySection(section: String): Int

    /**
     * 檢查文章是否存在
     *
     * @param id 文章 ID
     * @return 是否存在
     */
    @Query("SELECT EXISTS(SELECT 1 FROM articles WHERE id = :id)")
    suspend fun articleExists(id: String): Boolean

    /**
     * 獲取最後一次更新時間
     *
     * @return 最後更新的時間戳
     */
    @Query("SELECT MAX(updatedAt) FROM articles")
    suspend fun getLastUpdatedTime(): Long?

    /**
     * 根據分類獲取最後一次更新時間
     *
     * @param section 新聞分類
     * @return 最後更新的時間戳
     */
    @Query("SELECT MAX(updatedAt) FROM articles WHERE section = :section")
    suspend fun getLastUpdatedTimeBySection(section: String): Long?

    /**
     * 獲取最舊的文章（用於緩存管理）
     *
     * @param limit 返回數量
     * @return 最舊的文章列表
     */
    @Query("SELECT * FROM articles ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getOldestArticles(limit: Int): List<ArticleEntity>

    /**
     * 根據 ID 列表刪除文章（用於緩存清理）
     *
     * @param ids 要刪除的文章 ID 列表
     */
    @Query("DELETE FROM articles WHERE id IN (:ids)")
    suspend fun deleteArticlesByIds(ids: List<String>)
}