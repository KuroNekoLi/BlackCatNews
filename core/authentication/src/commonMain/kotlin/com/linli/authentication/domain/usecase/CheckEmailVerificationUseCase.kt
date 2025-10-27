package com.linli.authentication.domain.usecase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

/**
 * 檢查信箱是否已驗證 Use Case
 *
 * 職責：
 * 1. 重新載入使用者資料（確保取得最新狀態）
 * 2. 檢查信箱是否已驗證
 */
class CheckEmailVerificationUseCase {
    /**
     * 檢查當前使用者的信箱是否已驗證
     *
     * @return Boolean true 表示已驗證，false 表示未驗證或使用者未登入
     */
    suspend operator fun invoke(): Boolean {
        val currentUser = Firebase.auth.currentUser ?: return false

        // 重新載入使用者資料，確保 isEmailVerified 是最新的
        runCatching { currentUser.reload() }

        return currentUser.isEmailVerified
    }
}
