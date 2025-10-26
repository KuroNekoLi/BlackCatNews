package com.linli.authentication.data

import com.linli.authentication.AuthCredential
import com.linli.authentication.AuthError
import com.linli.authentication.ProviderType
import com.linli.authentication.domain.model.UserSession

/**
 * 認證管理器
 * 負責協調和分派不同的認證供應商
 */
class AuthManager(
    private val providers: Set<AuthProvider>
) {
    /**
     * 使用指定的供應商類型和憑證登入
     *
     * @param type 認證供應商類型
     * @param credential 認證憑證（匿名登入時為 null）
     */
    suspend fun signIn(type: ProviderType, credential: AuthCredential?): Result<UserSession> {
        val provider = providers.firstOrNull { it.type == type }
            ?: return Result.failure(AuthError.NoProviderFound)

        // 對於需要憑證的 provider（Google, Apple 等），必須提供憑證
        // 對於匿名登入，credential 為 null，但 AnonymousAuthProvider 會忽略它
        return if (credential != null) {
            provider.signIn(credential)
        } else if (type == ProviderType.Anonymous) {
            // 匿名登入不需要憑證，創建一個空憑證
            val emptyCredential = AuthCredential(idToken = "", accessToken = null)
            provider.signIn(emptyCredential)
        } else {
            Result.failure(IllegalArgumentException("$type 登入需要提供憑證"))
        }
    }

    /**
     * 登出所有供應商
     */
    suspend fun signOutAll(): Result<Unit> {
        providers.forEach { it.signOut() }
        return Result.success(Unit)
    }
}
