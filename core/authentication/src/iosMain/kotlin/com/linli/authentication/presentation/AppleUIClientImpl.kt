package com.linli.authentication.presentation

import com.linli.authentication.AuthCredential
import com.linli.authentication.domain.AppleSignInUIClient

/**
 * iOS 平台的 Apple Sign-In UI 客戶端實作
 *
 * 實作 Domain 層的 AppleSignInUIClient 介面
 * TODO: 實作真正的 Apple Sign-In 流程
 */
class AppleUIClientImpl : AppleSignInUIClient {
    /**
     * 啟動 Apple 登入 UI 並取得憑證
     *
     * TODO: 實作 Apple Sign-In
     * - 使用 AuthenticationServices framework
     * - 顯示 Apple 登入 UI
     * - 返回憑證或 null
     */
    override suspend fun getCredential(): AuthCredential? {
        // TODO: 實作 Apple Sign-In
        return null
    }
}
