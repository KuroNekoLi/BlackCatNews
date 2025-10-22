package com.linli.authentication

import android.app.Activity
import android.util.Base64
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.security.SecureRandom

private const val TAG = "GoogleCredentialHelper"

/**
 * 使用 Credential Manager 取得 Google ID Token
 *
 * 採用官方建議的兩階段流程：
 * 1. 先嘗試使用已授權帳號 (filterByAuthorizedAccounts = true)
 * 2. 若沒有已授權帳號，則顯示所有帳號供選擇 (filterByAuthorizedAccounts = false)
 *
 * @param serverClientId Web 客戶端 ID（從 Firebase Console 取得）
 * @return Google ID Token
 * @throws GetCredentialCancellationException 當使用者取消選擇帳號時
 * @throws GetCredentialException 當登入失敗時
 */
suspend fun Activity.getGoogleIdToken(serverClientId: String): String {
    Log.d(TAG, "getGoogleIdToken called with serverClientId: $serverClientId")

    val credentialManager = CredentialManager.create(this)

    // 階段 1：先嘗試「已授權帳號登入」(filter = true，這是預設值)
    // 如果使用者之前已經授權過此 Web Client ID，可以直接登入
    return try {
        Log.d(TAG, "階段 1: 嘗試使用已授權帳號")
        getIdTokenInternal(
            credentialManager = credentialManager,
            serverClientId = serverClientId,
            filterByAuthorizedAccounts = true
        )
    } catch (e: NoCredentialException) {
        Log.d(TAG, "階段 1 失敗: 沒有已授權帳號，進入階段 2")
        // 階段 2：若沒有已授權帳號，改用「所有帳號可選」(filter = false)
        // 這是「Sign up with Google」流程，會顯示設備上所有 Google 帳號
        getIdTokenInternal(
            credentialManager = credentialManager,
            serverClientId = serverClientId,
            filterByAuthorizedAccounts = false
        )
    }
}

/**
 * 內部實作：使用指定的 filterByAuthorizedAccounts 設定取得 ID Token
 */
private suspend fun Activity.getIdTokenInternal(
    credentialManager: CredentialManager,
    serverClientId: String,
    filterByAuthorizedAccounts: Boolean
): String {
    // 生成 nonce 以提高安全性
    val nonce = generateNonce()

    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(serverClientId)  // 使用 Web Client ID
        .setNonce(nonce)                     // 防止重放攻擊
        .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val response = credentialManager.getCredential(
            context = this,
            request = request
        )

        val credential = GoogleIdTokenCredential.createFrom(response.credential.data)
        return credential.idToken

    } catch (e: GetCredentialCancellationException) {
        // 使用者取消了選擇
        Log.d(TAG, "使用者取消登入")
        throw Exception("使用者取消登入", e)
    } catch (e: NoCredentialException) {
        // 讓上層兩段式流程來處理是否要 fallback
        Log.d(TAG, "沒有可用的憑證")
        throw e
    } catch (e: GetCredentialException) {
        // 其他憑證錯誤
        Log.d(TAG, "Google 登入失敗: ${e.message}")
        throw Exception("Google 登入失敗: ${e.message}", e)
    }
}

/**
 * 生成隨機 nonce 字符串
 * 用於防止重放攻擊
 * 使用 android.util.Base64 以確保兼容所有 Android 版本
 */
private fun generateNonce(): String {
    val random = SecureRandom()
    val bytes = ByteArray(32)
    random.nextBytes(bytes)
    return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
}
