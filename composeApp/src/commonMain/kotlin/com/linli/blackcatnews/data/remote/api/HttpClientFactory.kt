package com.linli.blackcatnews.data.remote.api

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * HTTP Client 工廠
 *
 * 負責創建和配置 Ktor HttpClient 實例
 * 包含 JSON 序列化、日誌記錄等配置
 *
 * @see HttpClient
 */
object HttpClientFactory {

    /**
     * 創建配置好的 HttpClient 實例
     *
     * 配置包含：
     * - Content Negotiation (JSON)
     * - Logging (開發環境)
     * - Timeout 設置
     *
     * @return 配置好的 HttpClient
     */
    fun create(): HttpClient {
        return HttpClient {
            // JSON 序列化配置
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true  // 忽略未知欄位
                    isLenient = true          // 寬鬆模式
                    prettyPrint = true        // 美化輸出
                })
            }

            // 日誌配置（開發環境）
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }

            // Timeout 配置
            engine {
                // 根據平台配置不同的 timeout
            }
        }
    }
}