package com.linli.authentication.domain.usecase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

/**
 * 寄送信箱驗證信 Use Case
 *
 * 職責：
 * 1. 向當前已登入的使用者發送信箱驗證郵件
 * 2. 處理寄送失敗的情況
 */
class SendEmailVerificationUseCase {
    /**
     * 寄送驗證信給當前使用者
     *
     * @return Result<Unit> 成功或失敗訊息
     */
    suspend operator fun invoke(): Result<Unit> = runCatching {
        val currentUser = Firebase.auth.currentUser
            ?: throw IllegalStateException("無法取得當前使用者，請先登入")

        currentUser.sendEmailVerification()
    }

    /**
     * 重新寄送驗證信
     * （與 invoke 相同，但語意上更明確）
     */
    suspend fun resend(): Result<Unit> = invoke()
}
