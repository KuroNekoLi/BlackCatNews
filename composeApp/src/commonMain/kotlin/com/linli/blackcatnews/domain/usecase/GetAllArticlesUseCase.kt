package com.linli.blackcatnews.domain.usecase

import com.linli.blackcatnews.domain.model.ArticleSection
import com.linli.blackcatnews.domain.model.NewsItem
import com.linli.blackcatnews.domain.repository.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * 獲取所有文章 Use Case
 *
 * 通過遍歷所有 ArticleSection 來獲取所有文章
 * 用於搜尋功能
 *
 * @property getArticlesBySectionUseCase 根據分類獲取文章的 Use Case
 */
class GetAllArticlesUseCase(
    private val getArticlesBySectionUseCase: GetArticlesBySectionUseCase
) {

    /**
     * 獲取所有分類的文章
     *
     * @param count 每個分類獲取的文章數量，默認 10 篇
     * @param forceRefresh 是否強制刷新
     * @return Flow<Result<List<NewsItem>>> 所有文章列表的數據流
     */
    operator fun invoke(
        count: Int = 5,
        forceRefresh: Boolean = false
    ): Flow<Result<List<NewsItem>>> {
        // 獲取所有分類
        val sections = ArticleSection.all

        // 為每個分類建立 Flow
        val flows = sections.map { section ->
            getArticlesBySectionUseCase(
                section = section,
                count = count,
                forceRefresh = forceRefresh
            )
        }

        // 合併所有 Flow 的結果
        return combine(flows) { results ->
            print("results: $results")
            // 檢查是否有任何錯誤
            val errors = results.filterIsInstance<Result.Error>()
            if (errors.isNotEmpty() && results.all { it is Result.Error }) {
                // 如果全部都是錯誤，返回第一個錯誤
                return@combine errors.first()
            }

            // 檢查是否全部都在加載中
            if (results.all { it is Result.Loading }) {
                return@combine Result.Loading
            }

            // 收集所有成功的結果
            val allArticles = mutableListOf<NewsItem>()
            results.forEach { result ->
                if (result is Result.Success) {
                    allArticles.addAll(result.data)
                }
            }

            // 去重（以 id 為準）
            val uniqueArticles = allArticles.distinctBy { it.id }

            Result.Success(uniqueArticles)
        }
    }
}
