package com.linli.blackcatnews.data.remote.api

import com.linli.blackcatnews.data.remote.dto.AiArticleDto
import com.linli.blackcatnews.data.remote.dto.AiArticlesResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * 新聞 API 服務
 *
 * 負責與後端 API 通信，獲取新聞數據
 *
 * Base URL: https://linlinews.zeabur.app/
 *
 * @property httpClient Ktor HttpClient 實例
 */
class NewsApiService(
    private val httpClient: HttpClient
) {

    companion object {
//        private const val BASE_URL = "https://linlinews.zeabur.app"
        private const val BASE_URL = "https://api.kuronekoli.uk"
        private const val RANDOM_ARTICLES_ENDPOINT = "/api/ai-articles/random"
    }

    /**
     * 獲取隨機文章
     *
     * 從 API 獲取指定數量和分類的隨機文章
     *
     * @param count 文章數量，最多 20 篇
     * @param section 新聞分類（可選）：news, world, technology
     * @param source 新聞來源（可選）：例如 BBC, CNN
     * @return 文章 DTO 列表
     * @throws Exception 當 API 請求失敗時
     *
     * @see AiArticleDto
     */
    suspend fun getRandomArticles(
        count: Int = 10,
        section: String? = null,
        source: String? = null
    ): List<AiArticleDto> {
        val response: AiArticlesResponseDto = httpClient.get("$BASE_URL$RANDOM_ARTICLES_ENDPOINT") {
            parameter("count", count)
            parameter("difficulty", "NORMAL")
            section?.let { parameter("section", it) }
            source?.let { parameter("source", it) }
        }.body()

        return response.articles
    }

    /**
     * 關閉 HTTP Client
     *
     * 在不再需要時調用，釋放資源
     */
    fun close() {
        httpClient.close()
    }
}