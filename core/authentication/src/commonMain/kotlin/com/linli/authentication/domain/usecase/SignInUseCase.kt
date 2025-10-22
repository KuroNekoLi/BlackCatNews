package com.linli.authentication.domain.usecase

import com.linli.authentication.ProviderType
import com.linli.authentication.data.AuthManager
import com.linli.authentication.domain.SignInUIClient
import com.linli.authentication.domain.model.UserSession

/**
 * 統一的登入 Use Case
 *
 * 改進後的設計：
 * - UI 層建立 UIClient Map (使用 Builder)
 * - ViewModel 將 Map 傳遞給 UseCase
 * - UseCase 專注於業務邏輯
 *
 * 職責：
 * 1. 檢查平台是否支援此認證方式
 * 2. 透過 UIClient 取得使用者憑證
 * 3. 驗證憑證
 * 4. 透過 AuthManager 執行認證
 * 5. 返回結果
 */
class SignInUseCase(
    private val authManager: AuthManager
) {
    /**
     * 執行登入
     *
     * @param providerType 認證供應商類型（Google, Apple, Facebook 等）
     * @param uiClients UIClient Map（由 UI 層建立並傳入）
     * @return Result<UserSession> 登入結果
     */
    suspend operator fun invoke(
        providerType: ProviderType,
        uiClients: Map<ProviderType, SignInUIClient>
    ): Result<UserSession> {
        // 1. 檢查平台是否支援此認證方式
        val uiClient = uiClients[providerType]
            ?: return Result.failure(
                UnsupportedOperationException("當前平台不支援 ${providerType.name} 登入")
            )

        // 2. 取得憑證（這裡處理所有 UI 互動）
        val credential = try {
            uiClient.getCredential()
                ?: return Result.failure(UserCancelledException("使用者取消 ${providerType.name} 登入"))
        } catch (e: Exception) {
            return Result.failure(Exception("取得 ${providerType.name} 憑證失敗: ${e.message}", e))
        }

        // 3. 驗證憑證
        if (credential.idToken.isNullOrBlank()) {
            return Result.failure(IllegalStateException("${providerType.name} ID Token 不能為空"))
        }

        // 4. 執行登入
        return authManager.signIn(
            type = providerType,
            credential = credential
        )
    }
}

/**
 * 使用者取消操作的異常
 */
class UserCancelledException(message: String) : Exception(message)
