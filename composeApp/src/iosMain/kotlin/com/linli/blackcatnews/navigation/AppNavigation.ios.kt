package com.linli.blackcatnews.navigation

import androidx.compose.runtime.Composable
import com.linli.authentication.ProviderType
import com.linli.authentication.data.AppleAuthProvider
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
 * 使用 Koin 注入的 AuthProvider
 */
@Composable
actual fun rememberSignInUIClients(): Map<ProviderType, SignInUIClient> {
    val googleAuthProvider = koinInject<GoogleAuthProvider>()
    val appleAuthProvider = koinInject<AppleAuthProvider>()

    return SignInUIClientBuilder()
        .createGoogleClient(googleAuthProvider)
        .createAppleClient(appleAuthProvider)
        .build()
}
