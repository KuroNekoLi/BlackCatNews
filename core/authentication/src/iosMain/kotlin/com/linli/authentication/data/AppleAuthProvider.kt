package com.linli.authentication.data

import com.linli.authentication.AuthCredential
import com.linli.authentication.ProviderType
import com.linli.authentication.domain.model.UserSession
import dev.gitlive.firebase.auth.FirebaseAuth

/**
 * iOS 平台的 Apple Sign-In 認證供應商
 *
 * 注意：Firebase 登入在 Swift 端完成（AppleSignInManager.swift），
 * Kotlin 端只需要从 GitLive SDK 读取当前登入的用户信息。
 */
class AppleAuthProvider(
    private val auth: FirebaseAuth
) : AuthProvider {
    override val type: ProviderType = ProviderType.Apple

    override suspend fun signIn(credential: AuthCredential): Result<UserSession> = runCatching {
        // Swift 端已经完成 Firebase 登入，直接从 GitLive SDK 获取当前用户
        val user = auth.currentUser
            ?: throw IllegalStateException("Apple 登入失敗：無法取得 Firebase 使用者資訊")

        // 转换为 domain model
        UserSession(
            uid = user.uid,
            email = user.email,
            providerIds = user.providerData.map { it.providerId }
        )
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        auth.signOut()
    }
}
