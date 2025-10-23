package com.linli.authentication.util

/**
 * 生成隨機 nonce 用於 Apple Sign-In
 *
 * nonce 用於防止重放攻擊：
 * 1. 生成隨機字串
 * 2. iOS 端使用 SHA-256 hash 後的版本設置到請求
 * 3. 原始 nonce 傳給 Firebase 驗證
 */
object NonceGenerator {

    /**
     * 生成隨機 nonce（32 字元）
     */
    fun generate(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..32)
            .map { allowedChars.random() }
            .joinToString("")
    }
}
