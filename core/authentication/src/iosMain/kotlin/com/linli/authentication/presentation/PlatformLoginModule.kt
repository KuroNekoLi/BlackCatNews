package com.linli.authentication.presentation

import org.koin.dsl.module

/**
 * iOS 平台專屬的登入模組
 *
 * 改進後的設計：
 * - SignInUIClient 不再在這裡註冊
 * - UI 層使用 SignInUIClientBuilder 直接創建
 * - 通過 parametersOf 傳遞給 ViewModel
 *
 * 這個模組現在是空的，因為：
 * - AuthProvider 在 platformAuthProvidersModule 中註冊
 * - UIClient 在 UI 層創建（rememberSignInUIClients）
 *
 * 保留這個文件是為了保持模組結構的一致性
 */
val platformLoginModule = module {
    // 空模組
    // AuthProvider 由 platformAuthProvidersModule 提供
    // UIClient 由 UI 層的 Builder 創建
}
