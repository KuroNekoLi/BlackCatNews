package com.linli.authentication.domain

import com.linli.authentication.AuthCredential
import com.linli.authentication.ProviderType

/**
 * 登入 UI 客戶端基礎接口
 *
 * 類似 AuthProvider 的設計，每個 UI Client 負責一種認證方式的 UI 互動
 */
interface SignInUIClient {
    /**
     * 此 UI Client 支援的認證供應商類型
     */
    val providerType: ProviderType

    /**
     * 啟動登入 UI 並取得憑證
     * @return AuthCredential 包含認證所需的 token，null 表示使用者取消
     */
    suspend fun getCredential(): AuthCredential?
}

/**
 * Google 登入 UI 客戶端接口
 *
 * 實作：在各平台的 login 模組實作
 * - iOS: GoogleUIClientImpl（使用 CocoaPods GoogleSignIn SDK）
 * - Android: AndroidGoogleUIClient（使用 Credential Manager API，動態獲取 Activity）
 */
interface GoogleSignInUIClient : SignInUIClient {
    override val providerType: ProviderType
        get() = ProviderType.Google
}

/**
 * Apple 登入 UI 客戶端接口
 *
 * 實作：在各平台的 login 模組實作
 * - iOS: AppleUIClientImpl（使用 AuthenticationServices）
 * - Android: 不支援
 */
interface AppleSignInUIClient : SignInUIClient {
    override val providerType: ProviderType
        get() = ProviderType.Apple
}
