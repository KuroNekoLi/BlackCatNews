package com.linli.authentication.data

import cocoapods.FirebaseAuth.FIROAuthProvider
import com.linli.authentication.AuthCredential
import com.linli.authentication.ProviderType
import com.linli.authentication.domain.model.UserSession
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * iOS 平台的 Apple Sign-In 認證供應商
 *
 * 使用 Firebase OAuthProvider 進行 Apple 登入
 *
 * 流程：
 * 1. AppleUIClientImpl 從 AuthenticationServices 獲取 identityToken
 * 2. 使用 FIROAuthProvider 創建 Firebase 憑證
 * 3. 透過 Firebase Auth 登入
 * 4. 返回 UserSession
 *
 * 注意：iOS 使用原生的 FIROAuthProvider（通過 CocoaPods）
 */
@OptIn(ExperimentalForeignApi::class)
class AppleAuthProvider(
    private val auth: FirebaseAuth
) : AuthProvider {
    override val type: ProviderType = ProviderType.Apple

    override suspend fun signIn(credential: AuthCredential): Result<UserSession> = runCatching {
        val idToken = requireNotNull(credential.idToken) {
            "缺少 Apple Identity Token"
        }

        val rawNonce = credential.rawNonce

        // 使用原生 Firebase iOS SDK 創建 OAuthCredential
        // FIROAuthProvider.credentialWithProviderID:IDToken:rawNonce:
        // rawNonce 如果為 null 則傳入空字串（Firebase 的安全建議是使用 nonce，但不是必須）
        val oauthCredential = FIROAuthProvider.credentialWithProviderID(
            providerID = "apple.com",
            IDToken = idToken,
            rawNonce = rawNonce ?: ""
        )

        // 使用 GitLive SDK 的 signInWithCredential
        // 將原生的 FIRAuthCredential 轉換為 GitLive 的 AuthCredential
        val result = auth.signInWithCredential(
            dev.gitlive.firebase.auth.AuthCredential(oauthCredential)
        )

        val user = result.user
            ?: throw IllegalStateException("Apple 登入成功但無法取得使用者資訊")

        // 轉換為 domain model
        val session = UserSession(
            uid = user.uid,
            email = user.email,
            providerIds = user.providerData.map { it.providerId }
        )

        session
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        auth.signOut()
    }
}
