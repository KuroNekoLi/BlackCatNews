package com.linli.authentication.domain.model

/**
 * 使用者會話資料模型
 * 用於儲存登入後的使用者資訊
 */
data class UserSession(
    val uid: String,
    val email: String?,
    val providerIds: List<String>
)
