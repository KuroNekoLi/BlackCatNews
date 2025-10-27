package com.linli.authentication.domain.usecase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

/**
 * 寄送密碼重設信 Use Case
 *
 * 職責：
 * 1. 向指定的電子郵件地址發送密碼重設郵件
 * 2. 處理無效的電子郵件格式或不存在的帳號
 */
class SendPasswordResetEmailUseCase {
    /**
     * 寄送密碼重設信
     *
     * @param email 要重設密碼的電子郵件地址
     * @return Result<Unit> 成功或失敗訊息
     */
    suspend operator fun invoke(email: String): Result<Unit> = runCatching {
        if (email.isBlank()) {
            throw IllegalArgumentException("電子郵件不能為空")
        }

        Firebase.auth.sendPasswordResetEmail(email)
    }
}
