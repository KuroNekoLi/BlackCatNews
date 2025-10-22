package com.linli.blackcatnews.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.linli.authentication.ProviderType
import com.linli.authentication.domain.SignInUIClient
import com.linli.authentication.domain.SignInUIClientBuilder
import com.linli.authentication.domain.createGoogleClient

/**
 * Android 平台的 UIClient 建立輔助函數
 *
 * 在 Composable 中建立 UIClients Map
 * 可以訪問 Activity Context
 */
@Composable
actual fun rememberSignInUIClients(): Map<ProviderType, SignInUIClient> {
    val context = LocalContext.current
    val activity = context as Activity

    return SignInUIClientBuilder()
        .createGoogleClient(activity)
        .build()
}
