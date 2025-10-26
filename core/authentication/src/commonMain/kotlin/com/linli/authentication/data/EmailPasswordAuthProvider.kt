package com.linli.authentication.data

import com.linli.authentication.AuthCredential
import com.linli.authentication.ProviderType
import com.linli.authentication.domain.model.UserSession
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth

/**
 * 帳號密碼認證供應商
 *
 * 跨平台通用（Android + iOS）
 * 使用 GitLive Firebase KMP SDK 實現
 *
 * 特點：
 * - 支援註冊新帳號（Email + Password）
 * - 支援登入既有帳號
 * - 支援密碼重設
 * - 支援升級匿名帳號
 *
 * 注意：
 * - 需要在 Firebase Console 啟用 Email/Password 登入
 * - 密碼需符合 Firebase 規則（至少 6 個字元）
 * - Email 格式需正確
 */
class EmailPasswordAuthProvider(
    private val auth: FirebaseAuth
) : AuthProvider {
    override val type: ProviderType = ProviderType.EmailPassword

    /**
     * 執行帳號密碼登入
     *
     * @param credential 包含 email 和 password 的憑證
     *                   idToken = email
     *                   accessToken = password
     * @return Result<UserSession> 登入結果
     */
    override suspend fun signIn(credential: AuthCredential): Result<UserSession> = runCatching {
        val email = credential.idToken
            ?: throw IllegalArgumentException("Email 不能為空")
        val password = credential.accessToken
            ?: throw IllegalArgumentException("密碼不能為空")

        // 執行登入
        val result = auth.signInWithEmailAndPassword(email, password)
        val user = result.user
            ?: throw IllegalStateException("登入成功但無法取得使用者資訊")

        // 轉換為 UserSession
        UserSession(
            uid = user.uid,
            email = user.email,
            providerIds = user.providerData.map { it.providerId }
        )
    }

    /**
     * 註冊新帳號
     *
     * @param email 使用者 Email
     * @param password 密碼（至少 6 個字元）
     * @return Result<UserSession> 註冊結果
     */
    suspend fun signUp(email: String, password: String): Result<UserSession> = runCatching {
        // 驗證輸入
        require(email.isNotBlank()) { "Email 不能為空" }
        require(password.length >= 6) { "密碼至少需要 6 個字元" }

        // 執行註冊
        val result = auth.createUserWithEmailAndPassword(email, password)
        val user = result.user
            ?: throw IllegalStateException("註冊成功但無法取得使用者資訊")

        // 轉換為 UserSession
        UserSession(
            uid = user.uid,
            email = user.email,
            providerIds = user.providerData.map { it.providerId }
        )
    }

    /**
     * 發送密碼重設郵件
     *
     * @param email 使用者 Email
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
        require(email.isNotBlank()) { "Email 不能為空" }
        auth.sendPasswordResetEmail(email)
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        auth.signOut()
    }
}

/**
 * 擴展函數：將匿名帳號升級為 Email 帳號
 *
 * @param email 使用者 Email
 * @param password 密碼
 */
suspend fun EmailPasswordAuthProvider.linkAnonymousToEmail(
    email: String,
    password: String
): Result<UserSession> = runCatching {
    val currentUser = dev.gitlive.firebase.Firebase.auth.currentUser
        ?: throw IllegalStateException("沒有使用者登入")

    if (!currentUser.isAnonymous) {
        throw IllegalStateException("目前使用者不是匿名帳號")
    }

    // 創建 Email 憑證
    val credential = dev.gitlive.firebase.auth.EmailAuthProvider.credential(email, password)

    // 連結憑證
    currentUser.linkWithCredential(credential)

    // 返回更新後的使用者資訊
    UserSession(
        uid = currentUser.uid,
        email = currentUser.email,
        providerIds = currentUser.providerData.map { it.providerId }
    )
}