package com.linli.authentication.domain

import com.linli.authentication.ProviderType

/**
 * SignInUIClient 建構器
 *
 * 用於在 UI 層建立和管理 SignInUIClient 的集合
 *
 * 使用方式：
 * ```
 * val clients = SignInUIClientBuilder()
 *     .createGoogleClient(activity)
 *     .createAppleClient()
 *     .build()
 * ```
 */
class SignInUIClientBuilder {
    private val clients = mutableMapOf<ProviderType, SignInUIClient>()

    /**
     * 添加自定義的 UIClient
     */
    fun addClient(client: SignInUIClient): SignInUIClientBuilder {
        clients[client.providerType] = client
        return this
    }

    /**
     * 建立最終的 Map
     */
    fun build(): Map<ProviderType, SignInUIClient> {
        return clients.toMap()
    }

    /**
     * 取得當前已註冊的 client 數量
     */
    fun size(): Int = clients.size
}

/**
 * 擴展函數：檢查是否支援某個供應商
 */
fun Map<ProviderType, SignInUIClient>.isSupported(type: ProviderType): Boolean {
    return this.containsKey(type)
}

/**
 * 擴展函數：根據供應商類型取得 UIClient
 */
fun Map<ProviderType, SignInUIClient>.getClient(type: ProviderType): SignInUIClient? {
    return this[type]
}
