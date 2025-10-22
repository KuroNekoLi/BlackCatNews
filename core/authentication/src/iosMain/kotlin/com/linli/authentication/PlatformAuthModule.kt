package com.linli.authentication

import com.linli.authentication.data.AppleAuthProvider
import com.linli.authentication.data.AuthProvider
import com.linli.authentication.data.GoogleAuthProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * iOS 平台專屬的認證模組
 * 使用 GitLive Firebase SDK
 */
val platformAuthProvidersModule = module {
    // GitLive Firebase Auth
    single { Firebase.auth }
    
    // Apple Provider
    single { AppleAuthProvider(get()) } bind AuthProvider::class

    // Google Provider
    single { GoogleAuthProvider(get()) } bind AuthProvider::class
}
