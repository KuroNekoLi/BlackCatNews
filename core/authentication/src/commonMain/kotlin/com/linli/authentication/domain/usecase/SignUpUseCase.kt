package com.linli.authentication.domain.usecase

import com.linli.authentication.data.EmailPasswordAuthProvider
import com.linli.authentication.domain.model.UserSession
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

/**
 * 註冊新帳號 Use Case
 *
 * 職責：
 * 1. 驗證輸入（Email 格式、密碼長度）
 * 2. 呼叫 EmailPasswordAuthProvider 註冊
 * 3. 自動寄送信箱驗證信
 * 4. 返回結果
 */
class SignUpUseCase(
    private val emailPasswordAuthProvider: EmailPasswordAuthProvider
) {
    /**
     * 執行註冊
     *
     * @param email 使用者 Email
     * @param password 密碼（至少 6 個字元）
     * @return Result<UserSession> 註冊結果
     */
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<UserSession> {
        // 1. 基本驗證
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email 不能為空"))
        }

        if (!isValidEmail(email)) {
            return Result.failure(IllegalArgumentException("Email 格式不正確"))
        }

        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("密碼至少需要 6 個字元"))
        }

        // 2. 呼叫 Provider 執行註冊
        val result = emailPasswordAuthProvider.signUp(email, password)

        // 3. 註冊成功後自動寄送驗證信
        if (result.isSuccess) {
            try {
                Firebase.auth.currentUser?.sendEmailVerification()
            } catch (e: Exception) {
                // 註冊成功但寄信失敗，不影響註冊結果
                // 使用者可以稍後手動重寄
                println("註冊成功但寄送驗證信失敗: ${e.message}")
            }
        }

        return result
    }

    /**
     * 簡單的 Email 格式驗證
     */
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
}