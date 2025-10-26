package com.linli.authentication.data

import com.linli.authentication.AuthCredential
import com.linli.authentication.ProviderType
import com.linli.authentication.domain.model.UserSession
import dev.gitlive.firebase.auth.FirebaseAuth

/**
 * 匿名登入認證供應商
 *
 * 跨平台通用（Android + iOS）
 * 使用 GitLive Firebase KMP SDK 實現
 *
 * 特點：
 * - 不需要使用者輸入任何資訊
 * - 快速建立臨時帳號
 * - 可以稍後升級為正式帳號
 * - 適合新手流程或暫存數據
 *
 * 注意：
 * - 需要在 Firebase Console 啟用匿名登入
 * - 匿名帳號會在登出或清除 App 資料後消失（除非有升級）
 */
class AnonymousAuthProvider(
    private val auth: FirebaseAuth
) : AuthProvider {
    override val type: ProviderType = ProviderType.Anonymous

    /**
     * 執行匿名登入
     *
     * 如果使用者已經登入（包括匿名或正式帳號），則直接返回現有使用者
     * 否則創建新的匿名使用者
     *
     * @param credential 匿名登入不需要憑證，此參數會被忽略
     * @return Result<UserSession> 登入結果
     */
    override suspend fun signIn(credential: AuthCredential): Result<UserSession> = runCatching {
        // 檢查是否已經登入
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // 已登入，直接返回
            return@runCatching UserSession(
                uid = currentUser.uid,
                email = currentUser.email,
                providerIds = currentUser.providerData.map { it.providerId }
            )
        }

        // 執行匿名登入
        val result = auth.signInAnonymously()
        val user = result.user
            ?: throw IllegalStateException("匿名登入成功但無法取得使用者資訊")

        // 轉換為 UserSession
        UserSession(
            uid = user.uid,
            email = null,  // 匿名使用者沒有 email
            providerIds = listOf("anonymous")  // 標記為匿名
        )
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        auth.signOut()
    }
}
