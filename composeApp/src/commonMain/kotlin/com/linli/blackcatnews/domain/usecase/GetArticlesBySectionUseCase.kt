package com.linli.blackcatnews.domain.usecase

import com.linli.blackcatnews.domain.model.NewsItem
import com.linli.blackcatnews.domain.model.ArticleSection
import com.linli.blackcatnews.domain.repository.ArticleRepository
import com.linli.blackcatnews.domain.repository.Result
import kotlinx.coroutines.flow.Flow

/**
 * 根據分類獲取文章 Use Case
 *
 * 負責從 Repository 獲取指定分類的文章列表
 *
 * ## 使用場景
 * - 分類頁面（新聞、世界、科技）
 * - 分類篩選
 * - Tab 切換
 *
 * @property repository 文章數據倉庫
 *
 * @see ArticleRepository
 * @see NewsItem
 */
class GetArticlesBySectionUseCase(
    private val repository: ArticleRepository
) {

    /**
     * 執行根據分類獲取文章操作
     *
     * @param section 新聞分類（必填）：
     *                - ArticleSection.News - 最新新聞
     *                - ArticleSection.World - 世界新聞
     *                - ArticleSection.Technology - 科技新聞
     * @param count 要獲取的文章數量，默認為 10，最大為 20
     * @param forceRefresh 是否強制從遠程刷新數據，默認為 false
     *
     * @return Flow<Result<List<NewsItem>>> 文章列表的數據流
     *
     * @throws IllegalArgumentException 當分類無效時
     *
     * ## 使用範例
     * ```kotlin
     * viewModelScope.launch {
     *     getArticlesBySectionUseCase(
     *         section = ArticleSection.Technology,
     *         count = 15
     *     ).collect { result ->
     *         when (result) {
     *             is Result.Success -> updateUI(result.data)
     *             is Result.Error -> showError(result.message)
     *             is Result.Loading -> showLoading()
     *         }
     *     }
     * }
     * ```
     */
    operator fun invoke(
        section: ArticleSection,
        count: Int = 10,
        forceRefresh: Boolean = false
    ): Flow<Result<List<NewsItem>>> {
        // 驗證參數
        require(count in 1..20) { "文章數量必須在 1-20 之間" }

        return repository.getArticlesBySection(
            section = section,
            count = count,
            forceRefresh = forceRefresh
        )
    }

    companion object {
        /**
         * 有效的新聞分類列表
         */
        val VALID_SECTIONS =
            setOf(ArticleSection.News, ArticleSection.World, ArticleSection.Technology)

        /**
         * 分類顯示名稱映射
         */
        val SECTION_DISPLAY_NAMES = mapOf(
            ArticleSection.News to "最新",
            ArticleSection.World to "世界",
            ArticleSection.Technology to "科技"
        )
    }
}