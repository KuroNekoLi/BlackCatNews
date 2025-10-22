package com.linli.authentication.presentation

import com.linli.authentication.data.GoogleAuthProvider
import com.linli.authentication.domain.AppleSignInUIClient
import com.linli.authentication.domain.GoogleSignInUIClient
import com.linli.authentication.domain.SignInUIClient
import org.koin.dsl.module

/**
 * iOS 平台專屬的登入模組
 *
 * 註冊平台特定的 UI Client，這些會被 UIClientManager 自動收集
 * UIClientManager 透過 getAll<SignInUIClient>() 取得所有註冊的 client
 */
val platformLoginModule = module {
    // Apple Sign-In UI 客戶端
    // 註冊為 SignInUIClient，讓 UIClientManager 可以找到它
    single<SignInUIClient> { AppleUIClientImpl() }

    // Google Sign-In UI 客戶端
    // 依賴 GoogleAuthProvider（從 core/authentication 模組注入）
    single<SignInUIClient> { GoogleUIClientImpl(get<GoogleAuthProvider>()) }
}
