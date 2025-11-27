package com.linli.blackcatnews.data.remote.api

import com.linli.blackcatnews.data.remote.dto.AiArticleDto

/**
 * 新聞 API 服務接口
 *
 * 定義獲取新聞數據的通用接口，可以有多種實現：
 * - NewsApiService: 真實的 API 實現
 * - MockNewsApiService: Mock 數據實現
 */
interface INewsApiService {
    /**
     * 獲取隨機文章
     *
     * @param count 文章數量，最多 20 篇
     * @param section 新聞分類（可選）：news, world, technology
     * @param source 新聞來源（可選）：例如 BBC, CNN
     * @return 文章 DTO 列表
     * @throws Exception 當 API 請求失敗時
     */
    suspend fun getRandomArticles(
        count: Int,
        section: String?,
        source: String?
    ): List<AiArticleDto>

    /**
     * 關閉資源
     */
    fun close()
}
