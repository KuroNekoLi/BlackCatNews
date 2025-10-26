package com.linli.authentication.domain.usecase

import com.linli.authentication.data.EmailPasswordAuthProvider
import com.linli.authentication.domain.model.UserSession

/**
 * 註冊新帳號 Use Case
 *
 * 職責：
 * 1. 驗證輸入（Email 格式、密碼長度）
 * 2. 呼叫 EmailPasswordAuthProvider 註冊
 * 3. 返回結果
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
        return emailPasswordAuthProvider.signUp(email, password)
    }

    /**
     * 簡單的 Email 格式驗證
     */
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
}