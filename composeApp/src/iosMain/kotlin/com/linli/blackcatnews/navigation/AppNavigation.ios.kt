package com.linli.blackcatnews.navigation

import androidx.compose.runtime.Composable
import com.linli.authentication.ProviderType
import com.linli.authentication.data.GoogleAuthProvider
import com.linli.authentication.domain.SignInUIClient
import com.linli.authentication.domain.SignInUIClientBuilder
import com.linli.authentication.domain.createAppleClient
import com.linli.authentication.domain.createGoogleClient
import org.koin.compose.koinInject

/**
 * iOS 平台的 UIClient 建立輔助函數
 *
 * 在 Composable 中建立 UIClients Map
 * - Google: 使用 Koin 注入的 GoogleAuthProvider
 * - Apple: 直接創建（使用 iOS 原生 AuthenticationServices）
 */
@Composable
actual fun rememberSignInUIClients(): Map<ProviderType, SignInUIClient> {
    val googleAuthProvider = koinInject<GoogleAuthProvider>()

    return SignInUIClientBuilder()
        .createGoogleClient(googleAuthProvider)
        .createAppleClient()  // Apple 不需要參數
        .build()
}
