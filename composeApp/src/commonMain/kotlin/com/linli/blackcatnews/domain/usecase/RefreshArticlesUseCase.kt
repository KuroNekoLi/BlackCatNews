package com.linli.blackcatnews.domain.usecase

import com.linli.blackcatnews.domain.model.ArticleSection
import com.linli.blackcatnews.domain.repository.ArticleRepository
import com.linli.blackcatnews.domain.repository.Result

/**
 * 刷新文章 Use Case
 *
 * 負責從遠程 API 獲取最新文章數據並更新本地數據庫
 *
 * ## 使用場景
 * - 下拉刷新
 * - 手動刷新按鈕
 * - 應用啟動時的後台刷新
 *
 * ## 行為
 * - 從遠程 API 獲取最新數據
 * - 更新本地 Room Database
 * - 觸發 Flow 更新 UI
 *
 * @property repository 文章數據倉庫
 *
 * @see ArticleRepository
 */
class RefreshArticlesUseCase(
    private val repository: ArticleRepository
) {

    /**
     * 執行刷新文章操作
     *
     * 這是一個 suspend 函數，會阻塞直到刷新完成
     *
     * @param section 新聞分類（可選）：
     *                - ArticleSection.News - 只刷新最新新聞
     *                - ArticleSection.World - 只刷新世界新聞
     *                - ArticleSection.Technology - 只刷新科技新聞
     *                - null - 刷新所有分類
     * @param count 要刷新的文章數量，默認為 10
     *
     * @return Result<Unit> 刷新結果
     *         - Result.Success: 刷新成功
     *         - Result.Error: 刷新失敗，包含錯誤信息
     *
     * ## 使用範例
     * ```kotlin
     * viewModelScope.launch {
     *     val result = refreshArticlesUseCase(
     *         section = ArticleSection.News,
     *         count = 10
     *     )
     *
     *     when (result) {
     *         is Result.Success -> {
     *             // 刷新成功，UI 會自動通過 Flow 更新
     *             showRefreshSuccess()
     *         }
     *         is Result.Error -> {
     *             // 刷新失敗
     *             showError(result.message)
     *         }
     *     }
     * }
     * ```
     */
    suspend operator fun invoke(
        section: ArticleSection? = null,
        count: Int = 10
    ): Result<Unit> {
        // 驗證參數
        require(count in 1..20) { "文章數量必須在 1-20 之間" }

        return try {
            repository.refreshArticles(
                section = section,
                count = count
            )
        } catch (e: Exception) {
            Result.Error(
                exception = e,
                message = "刷新失敗：${e.message}"
            )
        }
    }
}