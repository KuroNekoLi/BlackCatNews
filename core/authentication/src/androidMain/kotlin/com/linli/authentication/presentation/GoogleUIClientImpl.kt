package com.linli.authentication.presentation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.linli.authentication.AuthCredential
import com.linli.authentication.domain.GoogleSignInUIClient
import com.linli.authentication.getGoogleIdToken

/**
 * Android 平台的 Google Sign-In UI 客戶端實作
 *
 * 使用 Credential Manager API 取得 Google 憑證
 *
 * 注意：Android 平台需要 Activity context 才能使用 Credential Manager
 * 但因為 Activity 生命週期問題，無法在 Koin 模組初始化時注入
 * 目前的設計是讓 SignInUseCase 檢測到 Android 平台時返回不支援錯誤
 *
 * 建議：在 Android 端直接使用 Credential Manager，不經過 UIClientManager
 */
class GoogleUIClientImpl(
    private val context: Context,
    private val serverClientId: String
) : GoogleSignInUIClient {

    /**
     * 啟動 Google 登入 UI 並取得憑證
     */
    override suspend fun getCredential(): AuthCredential? {
        return try {
            Log.d(TAG, "開始 Google 登入, serverClientId: $serverClientId")

            val activity = context.findActivity()
                ?: throw IllegalStateException("無法找到 Activity context")

            Log.d(TAG, "找到 Activity: ${activity.javaClass.simpleName}")

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

    /**
     * 從 Context 中遞迴尋找 Activity
     */
    private fun Context.findActivity(): Activity? {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        return null
    }

    companion object {
        private const val TAG = "GoogleUIClientImpl"
    }
}
