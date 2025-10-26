package com.linli.authentication.domain.usecase

import com.linli.authentication.domain.model.UserSession
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

/**
 * 取得當前使用者 Use Case
 *
 * 職責：
 * 1. 檢查 Firebase 是否有已登入的使用者
 * 2. 將 Firebase User 轉換為 UserSession
 */
class GetCurrentUserUseCase {
    /**
     * 取得當前已登入的使用者
     *
     * @return UserSession? 如果有使用者登入則返回 UserSession，否則返回 null
     */
    operator fun invoke(): UserSession? {
        val currentUser = Firebase.auth.currentUser ?: return null

        return UserSession(
            uid = currentUser.uid,
            email = currentUser.email,
            providerIds = currentUser.providerData.map { it.providerId }
        )
    }

    /**
     * 檢查是否有使用者已登入
     *
     * @return Boolean true 表示有使用者登入，false 表示未登入
     */
    fun isAuthenticated(): Boolean {
        return Firebase.auth.currentUser != null
    }
}
