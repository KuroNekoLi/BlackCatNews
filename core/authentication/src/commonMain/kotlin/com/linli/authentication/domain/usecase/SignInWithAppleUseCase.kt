package com.linli.authentication.domain.usecase

import com.linli.authentication.data.AuthManager
import com.linli.authentication.ProviderType
import com.linli.authentication.domain.AppleSignInUIClient
import com.linli.authentication.domain.model.UserSession

/**
 * Apple 登入 Use Case
 *
 * 職責：
 * 1. 透過 AppleSignInUIClient 取得憑證
 * 2. 驗證憑證
 * 3. 呼叫 AuthManager 進行認證
 * 4. 返回結果
 *
 * ViewModel 只需要呼叫這個 UseCase，不需要知道內部細節
 * UI Client 在 UseCase 層注入，ViewModel 和 UI 層完全不知道它的存在
 */
class SignInWithAppleUseCase(
    private val authManager: AuthManager,
    private val appleUIClient: AppleSignInUIClient? = null  // 由 Koin 在平台層注入
) {
    /**
     * 執行 Apple 登入
     *
     * @return Result<UserSession> 登入結果
     */
    suspend operator fun invoke(): Result<UserSession> {
        // 1. 檢查平台支援
        if (appleUIClient == null) {
            return Result.failure(UnsupportedOperationException("當前平台不支援 Apple 登入"))
        }

        // 2. 取得憑證（這裡處理所有 UI 互動）
        val credential = try {
            appleUIClient.getCredential()
                ?: return Result.failure(UserCancelledException("使用者取消 Apple 登入"))
        } catch (e: Exception) {
            return Result.failure(Exception("取得 Apple 憑證失敗: ${e.message}", e))
        }

        // 3. 驗證憑證
        if (credential.idToken.isNullOrBlank()) {
            return Result.failure(IllegalStateException("Apple ID Token 不能為空"))
        }

        // 4. 執行登入
        return authManager.signIn(
            type = ProviderType.Apple,
            credential = credential
        )
    }
}
