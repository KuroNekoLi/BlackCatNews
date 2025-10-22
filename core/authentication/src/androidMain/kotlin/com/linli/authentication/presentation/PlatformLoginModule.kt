package com.linli.authentication.presentation

import com.linli.authentication.data.GoogleAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth
import org.koin.dsl.module

private const val TAG = "PlatformLoginModule"

/**
 * Android 平台專屬的登入模組
 *
 * 改進後的設計：
 * - SignInUIClient 不再在這裡註冊
 * - UI 層使用 SignInUIClientBuilder 直接創建
 * - 通過 parametersOf 傳遞給 ViewModel
 *
 * 這個模組只負責註冊 AuthProvider
 */
val platformLoginModule = module {
    // Google AuthProvider (後端認證)
    single {
        GoogleAuthProvider(auth = get<FirebaseAuth>())
    }
}
