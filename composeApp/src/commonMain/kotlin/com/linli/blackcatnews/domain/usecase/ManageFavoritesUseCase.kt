package com.linli.blackcatnews.domain.usecase

import com.linli.blackcatnews.domain.model.NewsItem
import com.linli.blackcatnews.domain.repository.ArticleRepository
import com.linli.blackcatnews.domain.repository.Result
import kotlinx.coroutines.flow.Flow

/**
 * 管理收藏 Use Case
 *
 * 負責管理用戶的文章收藏功能
 * 包含獲取收藏列表、添加收藏、移除收藏
 *
 * ## 使用場景
 * - 收藏頁面
 * - 文章詳情頁的收藏按鈕
 * - 收藏列表管理
 *
 * @property repository 文章數據倉庫
 *
 * @see ArticleRepository
 * @see NewsItem
 */
class ManageFavoritesUseCase(
    private val repository: ArticleRepository
) {

    /**
     * 獲取收藏的文章列表
     *
     * @return Flow<Result<List<NewsItem>>> 收藏文章列表的數據流
     *
     * ## 使用範例
     * ```kotlin
     * viewModelScope.launch {
     *     getFavoriteArticles().collect { result ->
     *         when (result) {
     *             is Result.Success -> showFavorites(result.data)
     *             is Result.Error -> showError(result.message)
     *             is Result.Loading -> showLoading()
     *         }
     *     }
     * }
     * ```
     */
    fun getFavoriteArticles(): Flow<Result<List<NewsItem>>> {
        return repository.getFavoriteArticles()
    }

    /**
     * 添加文章到收藏
     *
     * @param articleId 文章 ID
     * @return Result<Unit> 操作結果
     *
     * ## 使用範例
     * ```kotlin
     * viewModelScope.launch {
     *     val result = addToFavorites("article-123")
     *     when (result) {
     *         is Result.Success -> showToast("已添加到收藏")
     *         is Result.Error -> showError(result.message)
     *     }
     * }
     * ```
     */
    suspend fun addToFavorites(articleId: String): Result<Unit> {
        require(articleId.isNotBlank()) { "文章 ID 不能為空" }
        return repository.addToFavorites(articleId)
    }

    /**
     * 從收藏中移除文章
     *
     * @param articleId 文章 ID
     * @return Result<Unit> 操作結果
     *
     * ## 使用範例
     * ```kotlin
     * viewModelScope.launch {
     *     val result = removeFromFavorites("article-123")
     *     when (result) {
     *         is Result.Success -> showToast("已移除收藏")
     *         is Result.Error -> showError(result.message)
     *     }
     * }
     * ```
     */
    suspend fun removeFromFavorites(articleId: String): Result<Unit> {
        require(articleId.isNotBlank()) { "文章 ID 不能為空" }
        return repository.removeFromFavorites(articleId)
    }

    /**
     * 切換收藏狀態
     *
     * 如果已收藏則移除，未收藏則添加
     *
     * @param articleId 文章 ID
     * @param isFavorite 當前是否已收藏
     * @return Result<Unit> 操作結果
     *
     * ## 使用範例
     * ```kotlin
     * viewModelScope.launch {
     *     val result = toggleFavorite(
     *         articleId = "article-123",
     *         isFavorite = false
     *     )
     *     when (result) {
     *         is Result.Success -> updateFavoriteIcon()
     *         is Result.Error -> showError(result.message)
     *     }
     * }
     * ```
     */
    suspend fun toggleFavorite(
        articleId: String,
        isFavorite: Boolean
    ): Result<Unit> {
        return if (isFavorite) {
            removeFromFavorites(articleId)
        } else {
            addToFavorites(articleId)
        }
    }
}