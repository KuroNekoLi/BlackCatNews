package com.linli.blackcatnews.data.remote.api

import MockData
import com.linli.blackcatnews.data.remote.dto.AiArticleDto
import kotlinx.serialization.json.Json

/**
 * Mock 新聞 API 服務
 *
 * 用於開發和測試環境，直接返回本地 mock 數據
 * 不需要連接實際的後端 API
 *
 * 可用的 mock 數據：
 * - MockData.mockResponseEasy: VERY_EASY 難度文章
 * - MockData.mockResponseNormal: NORMAL 難度文章
 */
class MockNewsApiService : INewsApiService {

    companion object {
        // 選擇要使用的 mock 數據（可以切換）
        private const val USE_EASY_DATA = false // true: Easy, false: Normal
    }

    // 配置 JSON 解析器，使其能夠處理未知的 JSON 字段
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    /**
     * 獲取隨機文章（Mock 版本）
     *
     * 返回預定義的 mock 數據，參數會被忽略
     *
     * @param count 文章數量（參數會被忽略）
     * @param section 新聞分類（參數會被忽略）
     * @param source 新聞來源（參數會被忽略）
     * @return 文章 DTO 列表
     *
     * @see AiArticleDto
     */
    override suspend fun getRandomArticles(
        count: Int,
        section: String?,
        source: String?
    ): List<AiArticleDto> {
        // 根據配置選擇 mock 數據
        val mockJson = if (USE_EASY_DATA) {
            MockData.mockResponseEasy
        } else {
            MockData.mockResponseNormal
        }

        return try {
            // 解析整個響應
            val response =
                json.decodeFromString<com.linli.blackcatnews.data.remote.dto.AiArticlesResponseDto>(
                    mockJson
                )
            println("MockNewsApiService: 成功解析 ${response.articles.size} 篇文章")
            response.articles
        } catch (e: Exception) {
            // 如果解析失敗，返回空列表並打印錯誤
            println("MockNewsApiService: 解析 mock 數據失敗 - ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 關閉資源（Mock 版本不需要實際操作）
     */
    override fun close() {
        // Mock 版本不需要實際關閉任何資源
        println("MockNewsApiService: close() called (no-op)")
    }
}
