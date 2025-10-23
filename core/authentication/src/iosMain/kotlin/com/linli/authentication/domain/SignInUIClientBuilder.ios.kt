package com.linli.authentication.domain

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
 * 使用 iOS 原生的 AuthenticationServices framework
 * 不需要額外參數，直接創建即可
 */
fun SignInUIClientBuilder.createAppleClient(): SignInUIClientBuilder {
    val client = AppleUIClientImpl()
    return addClient(client)
}
