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
     */
    suspend fun signIn(type: ProviderType, credential: AuthCredential): Result<UserSession> {
        val provider = providers.firstOrNull { it.type == type }
            ?: return Result.failure(AuthError.NoProviderFound)
        return provider.signIn(credential)
    }

    /**
     * 登出所有供應商
     */
    suspend fun signOutAll(): Result<Unit> {
        providers.forEach { it.signOut() }
        return Result.success(Unit)
    }
}
