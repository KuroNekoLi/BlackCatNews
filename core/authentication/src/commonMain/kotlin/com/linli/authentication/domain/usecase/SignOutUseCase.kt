package com.linli.authentication.domain.usecase

import com.linli.authentication.data.AuthManager

/**
 * 登出 Use Case
 *
 * 職責：
 * 1. 執行登出邏輯
 * 2. 清除所有認證供應商的狀態
 */
class SignOutUseCase(
    private val authManager: AuthManager
) {
    /**
     * 執行登出
     *
     * @return Result<Unit> 登出結果
     */
    suspend operator fun invoke(): Result<Unit> {
        return authManager.signOutAll()
    }
}
