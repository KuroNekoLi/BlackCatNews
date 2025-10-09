package com.linli.blackcatnews.domain.usecase

import com.linli.blackcatnews.domain.model.ArticleDetail
import com.linli.blackcatnews.domain.repository.ArticleRepository
import com.linli.blackcatnews.domain.repository.Result
import kotlinx.coroutines.flow.Flow

/**
 * 獲取文章詳情 Use Case
 *
 * 負責從 Repository 獲取單篇文章的完整詳情
 * 包含雙語內容、重點單字、文法說明、閱讀測驗等
 *
 * ## 使用場景
 * - 文章詳情頁
 * - 點擊新聞卡片進入詳情
 * - 深度閱讀和學習
 *
 * @property repository 文章數據倉庫
 *
 * @see ArticleRepository
 * @see ArticleDetail
 */
class GetArticleDetailUseCase(
    private val repository: ArticleRepository
) {

    /**
     * 執行獲取文章詳情操作
     *
     * @param articleId 文章唯一識別碼（必填）
     *
     * @return Flow<Result<ArticleDetail>> 文章詳情的數據流
     *         - Result.Loading: 正在加載
     *         - Result.Success: 加載成功，包含完整文章詳情
     *         - Result.Error: 加載失敗，包含錯誤信息
     *
     * ## 使用範例
     * ```kotlin
     * viewModelScope.launch {
     *     getArticleDetailUseCase(articleId = "article-123")
     *         .collect { result ->
     *             when (result) {
     *                 is Result.Loading -> showLoadingIndicator()
     *                 is Result.Success -> {
     *                     val article = result.data
     *                     showArticleContent(article)
     *                     showGlossary(article.glossary)
     *                     showGrammar(article.grammarPoints)
     *                     showQuiz(article.quiz)
     *                 }
     *                 is Result.Error -> showError(result.message)
     *             }
     *         }
     * }
     * ```
     */
    operator fun invoke(articleId: String): Flow<Result<ArticleDetail>> {
        require(articleId.isNotBlank()) { "文章 ID 不能為空" }
        return repository.getArticleDetail(articleId)
    }
}