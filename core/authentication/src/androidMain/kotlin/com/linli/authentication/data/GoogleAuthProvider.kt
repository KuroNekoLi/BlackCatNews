package com.linli.authentication.data

import com.linli.authentication.AuthCredential
import com.linli.authentication.ProviderType
import com.linli.authentication.domain.model.UserSession
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider as GitliveGoogleAuthProvider

/**
 * Android 平台的 Google Sign-In 認證供應商
 * 使用 GitLive 的 GoogleAuthProvider 建憑證並登入
 */
class GoogleAuthProvider(
    private val auth: FirebaseAuth
) : AuthProvider {
    override val type: ProviderType = ProviderType.Google

    override suspend fun signIn(credential: AuthCredential): Result<UserSession> = runCatching {
        val idToken = requireNotNull(credential.idToken) { "缺少 Google ID token" }

        // GitLive 的 GoogleAuthProvider：等效於官方 Android SDK 建憑證
        val gitliveCred = GitliveGoogleAuthProvider.credential(idToken, /* accessToken = */ null)
        val result = auth.signInWithCredential(gitliveCred)
        val user = result.user

        UserSession(
            uid = user?.uid ?: error("登入成功但無法取得使用者 UID"),
            email = user.email,
            providerIds = user.providerData.map { it.providerId }
        )
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        auth.signOut()
    }
}
