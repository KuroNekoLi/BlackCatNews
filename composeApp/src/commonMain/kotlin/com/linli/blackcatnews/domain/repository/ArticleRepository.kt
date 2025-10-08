package com.linli.blackcatnews.domain.repository

import com.linli.blackcatnews.domain.model.NewsItem
import com.linli.blackcatnews.domain.model.ArticleDetail
import com.linli.blackcatnews.domain.model.ArticleSection
import kotlinx.coroutines.flow.Flow

/**
 * 文章數據倉庫接口
 *
 * 定義文章數據的 CRUD 操作，遵循 SSOT（Single Source of Truth）原則
 * - 數據從 Room Database 讀取（SSOT）
 * - 遠程數據自動同步到本地數據庫
 *
 * @see com.linli.blackcatnews.data.repository.ArticleRepositoryImpl
 */
interface ArticleRepository {

    /**
     * 獲取隨機文章列表（從本地數據庫讀取）
     *
     * 使用 Flow 實現響應式數據流，當數據庫更新時自動通知 UI
     *
     * @param count 文章數量，默認 10 篇
     * @param section 新聞分類（可選）：News, World, Technology
     * @param forceRefresh 是否強制從遠程刷新，默認 false
     * @return Flow<Result<List<NewsItem>>> 文章列表的數據流
     */
    fun getRandomArticles(
        count: Int = 10,
        section: ArticleSection? = null,
        forceRefresh: Boolean = false
    ): Flow<Result<List<NewsItem>>>

    /**
     * 根據分類獲取文章列表
     *
     * @param section 新聞分類：News, World, Technology
     * @param count 文章數量，默認 10 篇
     * @param forceRefresh 是否強制刷新
     * @return Flow<Result<List<NewsItem>>> 文章列表的數據流
     */
    fun getArticlesBySection(
        section: ArticleSection,
        count: Int = 10,
        forceRefresh: Boolean = false
    ): Flow<Result<List<NewsItem>>>

    /**
     * 根據 ID 獲取文章詳情
     *
     * @param articleId 文章 ID
     * @return Flow<Result<ArticleDetail>> 文章詳情的數據流
     */
    fun getArticleDetail(articleId: String): Flow<Result<ArticleDetail>>

    /**
     * 刷新文章數據
     *
     * 從遠程 API 獲取最新數據並更新本地數據庫
     *
     * @param section 新聞分類（可選）
     * @param count 文章數量，默認 10 篇
     * @return Result<Unit> 刷新結果
     */
    suspend fun refreshArticles(
        section: ArticleSection? = null,
        count: Int = 10
    ): Result<Unit>

    /**
     * 獲取收藏的文章列表
     *
     * @return Flow<Result<List<NewsItem>>> 收藏文章列表的數據流
     */
    fun getFavoriteArticles(): Flow<Result<List<NewsItem>>>

    /**
     * 添加文章到收藏
     *
     * @param articleId 文章 ID
     * @return Result<Unit> 操作結果
     */
    suspend fun addToFavorites(articleId: String): Result<Unit>

    /**
     * 從收藏中移除文章
     *
     * @param articleId 文章 ID
     * @return Result<Unit> 操作結果
     */
    suspend fun removeFromFavorites(articleId: String): Result<Unit>

    /**
     * 清除本地緩存
     *
     * @return Result<Unit> 操作結果
     */
    suspend fun clearCache(): Result<Unit>
}

/**
 * 統一結果包裝類
 *
 * 用於封裝操作結果，包含成功、失敗和加載狀態
 *
 * @param T 數據類型
 */
sealed class Result<out T> {
    /**
     * 成功狀態
     *
     * @property data 返回的數據
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * 錯誤狀態
     *
     * @property exception 錯誤異常
     * @property message 錯誤消息
     */
    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: "Unknown error"
    ) : Result<Nothing>()

    /**
     * 加載中狀態
     */
    object Loading : Result<Nothing>()

    /**
     * 判斷是否為成功狀態
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * 判斷是否為錯誤狀態
     */
    val isError: Boolean
        get() = this is Error

    /**
     * 判斷是否為加載中狀態
     */
    val isLoading: Boolean
        get() = this is Loading

    /**
     * 獲取數據（如果是成功狀態）
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * 獲取錯誤消息（如果是錯誤狀態）
     */
    fun getErrorOrNull(): String? = when (this) {
        is Error -> message
        else -> null
    }
}