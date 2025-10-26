package com.linli.authentication

/**
 * 認證供應商類型
 */
enum class ProviderType {
    Apple,
    Google,
    Facebook,
    EmailPassword,
    Anonymous
}

/**
 * 認證憑證資料類別
 * 用於封裝不同供應商的登入憑證
 */
data class AuthCredential(
    val idToken: String? = null,
    val accessToken: String? = null,
    val rawNonce: String? = null
)

/**
 * 認證錯誤封裝
 */
sealed class AuthError(message: String) : Throwable(message) {
    data object NoProviderFound : AuthError("找不到對應的認證供應商")
    data class ProviderFailed(val reason: String) : AuthError(reason)
}
