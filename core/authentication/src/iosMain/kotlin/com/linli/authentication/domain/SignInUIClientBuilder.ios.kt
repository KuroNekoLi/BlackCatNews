package com.linli.authentication.domain

import com.linli.authentication.data.AppleAuthProvider
import com.linli.authentication.data.GoogleAuthProvider
import com.linli.authentication.presentation.AppleUIClientImpl
import com.linli.authentication.presentation.GoogleUIClientImpl

/**
 * iOS 平台的 SignInUIClientBuilder 擴展函數
 *
 * 提供便利的方法來創建 iOS 平台的 UIClient
 */

/**
 * 創建 Google Sign-In UIClient (iOS)
 *
 * @param googleAuthProvider Google 認證提供者（使用 GoogleSignIn SDK）
 */
fun SignInUIClientBuilder.createGoogleClient(
    googleAuthProvider: GoogleAuthProvider
): SignInUIClientBuilder {
    val client = GoogleUIClientImpl(googleAuthProvider)
    return addClient(client)
}

/**
 * 創建 Apple Sign-In UIClient (iOS)
 *
 * @param appleAuthProvider Apple 認證提供者（使用 AuthenticationServices）
 */
fun SignInUIClientBuilder.createAppleClient(
    appleAuthProvider: AppleAuthProvider
): SignInUIClientBuilder {
    val client = AppleUIClientImpl(appleAuthProvider)
    return addClient(client)
}
