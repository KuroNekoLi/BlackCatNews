package com.linli.authentication.data

import com.linli.authentication.AuthCredential
import com.linli.authentication.ProviderType
import com.linli.authentication.domain.model.UserSession

/**
 * 認證供應商介面
 * 每個供應商（Google, Apple, Facebook 等）都需要實作這個介面
 */
interface AuthProvider {
    val type: ProviderType

    /**
     * 使用憑證登入
     */
    suspend fun signIn(credential: AuthCredential): Result<UserSession>

    /**
     * 登出
     */
    suspend fun signOut(): Result<Unit> = Result.success(Unit)
}
