package com.linli.authentication.presentation

import android.app.Activity
import android.util.Log
import com.linli.authentication.AuthCredential
import com.linli.authentication.domain.GoogleSignInUIClient
import com.linli.authentication.getGoogleIdToken

/**
 * Android 平台的 Google Sign-In UI 客戶端實作
 *
 * 此實現接受 Activity 作為構造參數
 * Activity 在創建時由 UI 層提供
 */
class AndroidGoogleUIClient(
    private val activity: Activity,
    private val serverClientId: String
) : GoogleSignInUIClient {

    /**
     * 啟動 Google 登入 UI 並取得憑證
     */
    override suspend fun getCredential(): AuthCredential? {
        return try {
            Log.d(TAG, "開始 Google 登入, serverClientId: $serverClientId")
            Log.d(TAG, "使用 Activity: ${activity.javaClass.simpleName}")

            val idToken = activity.getGoogleIdToken(serverClientId)

            Log.d(TAG, "成功取得 ID Token")

            AuthCredential(
                idToken = idToken,
                accessToken = null  // Credential Manager 只提供 ID Token
            )
        } catch (e: Exception) {
            // 記錄詳細錯誤
            Log.e(TAG, "Google 登入失敗", e)

            // 重新拋出異常讓 UseCase 處理
            throw e
        }
    }

    companion object {
        private const val TAG = "AndroidGoogleUIClient"
    }
}
