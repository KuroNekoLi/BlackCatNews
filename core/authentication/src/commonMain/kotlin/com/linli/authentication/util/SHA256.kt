package com.linli.authentication.util

import okio.ByteString.Companion.encodeUtf8

/**
 * SHA-256 哈希工具
 *
 * 使用 Okio 实现跨平台的 SHA-256 哈希功能
 */
object SHA256 {
    /** 回傳 SHA-256 的 16 進位字串（小寫），這才是要塞進 request.nonce 的值 */
    fun hashHex(input: String): String {
        return input.encodeUtf8().sha256().hex()   // ← 改用 hex()
    }
}
