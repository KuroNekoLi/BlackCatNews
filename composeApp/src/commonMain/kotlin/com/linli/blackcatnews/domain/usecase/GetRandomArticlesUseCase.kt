package com.linli.blackcatnews.domain.usecase

import com.linli.blackcatnews.domain.model.NewsItem
import com.linli.blackcatnews.domain.model.ArticleSection
import com.linli.blackcatnews.domain.repository.ArticleRepository
import com.linli.blackcatnews.domain.repository.Result
import kotlinx.coroutines.flow.Flow

/**
 * 獲取隨機文章 Use Case
 *
 * 負責從 Repository 獲取隨機文章列表
 * 會優先從本地數據庫讀取，如果沒有數據或需要刷新則從遠端 API 獲取
 *
 * ## 使用場景
 * - 首頁文章列表
 * - 下拉刷新
 * - 首次啟動應用
 *
 * ## 數據流向
 * ```
 * UI → Use Case → Repository → Room Database (SSOT)
 *                            ↓
 *                     Remote API (如果需要)
 * ```
 *
 * @property repository 文章數據倉庫
 *
 * @see ArticleRepository
 * @see NewsItem
 */
class GetRandomArticlesUseCase(
    private val repository: ArticleRepository
) {

    /**
     * 執行獲取隨機文章操作
     *
     * @param count 要獲取的文章數量，默認為 10，最大為 20
     * @param section 新聞分類（可選）：
     *                - ArticleSection.News - 最新新聞
     *                - ArticleSection.World - 世界新聞
     *                - ArticleSection.Technology - 科技新聞
     *                - null - 所有分類
     * @param forceRefresh 是否強制從遠程刷新數據，默認為 false
     *                     - true: 忽略本地緩存，直接從 API 獲取最新數據
     *                     - false: 優先使用本地緩存
     *
     * @return Flow<Result<List<NewsItem>>> 文章列表的數據流
     *         - Result.Loading: 正在加載
     *         - Result.Success: 加載成功，包含文章列表
     *         - Result.Error: 加載失敗，包含錯誤信息
     *
     * ## 使用範例
     * ```kotlin
     * viewModelScope.launch {
     *     getRandomArticlesUseCase(
     *         count = 10,
     *         section = ArticleSection.News,
     *         forceRefresh = false
     *     ).collect { result ->
     *         when (result) {
     *             is Result.Loading -> showLoading()
     *             is Result.Success -> showArticles(result.data)
     *             is Result.Error -> showError(result.message)
     *         }
     *     }
     * }
     * ```
     */
    operator fun invoke(
        count: Int = 5,
        section: ArticleSection? = null,
        forceRefresh: Boolean = false
    ): Flow<Result<List<NewsItem>>> {
        // 驗證參數
        require(count in 1..20) { "文章數量必須在 1-20 之間" }

        return repository.getRandomArticles(
            count = count,
            section = section,
            forceRefresh = forceRefresh
        )
    }
}