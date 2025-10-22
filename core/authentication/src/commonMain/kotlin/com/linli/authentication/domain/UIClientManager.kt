package com.linli.authentication.domain

import com.linli.authentication.AuthCredential
import com.linli.authentication.ProviderType

/**
 * UI Client 管理器
 *
 * 類似 AuthManager 的設計，負責管理和分派不同平台的 UI Client
 *
 * 職責：
 * - 根據 ProviderType 找到對應的 UI Client
 * - 呼叫 UI Client 取得使用者憑證
 * - 處理平台不支援的情況
 */
class UIClientManager(
    private val clients: Set<SignInUIClient>
) {
    /**
     * 根據供應商類型取得憑證
     *
     * @param type 認證供應商類型（Google, Apple, Facebook 等）
     * @return AuthCredential? 使用者憑證，null 表示使用者取消或不支援
     * @throws Exception 如果取得憑證過程發生錯誤
     */
    suspend fun getCredential(type: ProviderType): AuthCredential? {
        val client = clients.firstOrNull { it.providerType == type }
            ?: return null  // 平台不支援此認證方式

        return client.getCredential()
    }

    /**
     * 檢查是否支援某個認證方式
     */
    fun isSupported(type: ProviderType): Boolean {
        return clients.any { it.providerType == type }
    }
}
