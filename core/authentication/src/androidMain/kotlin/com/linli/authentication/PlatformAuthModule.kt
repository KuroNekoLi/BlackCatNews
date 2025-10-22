package com.linli.authentication

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.dsl.bind
import org.koin.dsl.module
import com.linli.authentication.data.AuthProvider
import com.linli.authentication.data.GoogleAuthProvider

/**
 * Android 平台專屬的認證模組
 */
val platformAuthProvidersModule = module {
    // GitLive 的 FirebaseAuth
    single { Firebase.auth }

    // Google Provider
    single { GoogleAuthProvider(get()) } bind AuthProvider::class
}
