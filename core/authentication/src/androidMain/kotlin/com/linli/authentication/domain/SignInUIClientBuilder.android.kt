package com.linli.authentication.domain

import android.app.Activity
import android.content.Context
import com.linli.authentication.presentation.AndroidGoogleUIClient

/**
 * Android 平台的 SignInUIClientBuilder 擴展函數
 *
 * 提供便利的方法來創建 Android 平台的 UIClient
 */

/**
 * 創建 Google Sign-In UIClient (Android)
 *
 * @param activity 當前的 Activity (用於 Credential Manager)
 * @param serverClientId Google Web Client ID (可選，會從 resources 讀取)
 */
fun SignInUIClientBuilder.createGoogleClient(
    activity: Activity,
    serverClientId: String? = null
): SignInUIClientBuilder {
    val clientId = serverClientId ?: getServerClientIdFromResources(activity)
    val client = AndroidGoogleUIClient(
        activity = activity,
        serverClientId = clientId
    )
    return addClient(client)
}

/**
 * 從 Android Resources 讀取 Google Server Client ID
 *
 * Firebase plugin 會自動將 google-services.json 轉換為 string resource
 */
private fun getServerClientIdFromResources(context: Context): String {
    return try {
        val resourceId = context.resources.getIdentifier(
            "default_web_client_id",
            "string",
            context.packageName
        )
        if (resourceId != 0) {
            context.resources.getString(resourceId)
        } else {
            // Fallback: 從 google-services.json 中的值
            "181914763371-a4ud44e9k2f3hp1l9s48qnci7gplc65c.apps.googleusercontent.com"
        }
    } catch (e: Exception) {
        "181914763371-a4ud44e9k2f3hp1l9s48qnci7gplc65c.apps.googleusercontent.com"
    }
}
