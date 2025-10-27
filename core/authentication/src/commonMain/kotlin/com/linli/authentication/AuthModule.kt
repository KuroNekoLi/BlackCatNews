package com.linli.authentication

import com.linli.authentication.data.AnonymousAuthProvider
import com.linli.authentication.data.AuthManager
import com.linli.authentication.data.AuthProvider
import com.linli.authentication.data.EmailPasswordAuthProvider
import com.linli.authentication.domain.usecase.CheckEmailVerificationUseCase
import com.linli.authentication.domain.usecase.GetCurrentUserUseCase
import com.linli.authentication.domain.usecase.SendEmailVerificationUseCase
import com.linli.authentication.domain.usecase.SendPasswordResetEmailUseCase
import com.linli.authentication.domain.usecase.SignInUseCase
import com.linli.authentication.domain.usecase.SignOutUseCase
import com.linli.authentication.domain.usecase.SignUpUseCase
import com.linli.authentication.presentation.SignInViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * 認證模組的 Koin 配置
 *
 * 改進後的設計：
 * 1. Data 層：Firebase Auth、AuthManager、AuthProvider
 * 2. Domain 層：UseCases（不再有 UIClientManager）
 * 3. Presentation 層：ViewModel（接收 UIClients Map 作為參數）
 *
 * UIClient 的創建：
 * - UI 層使用 SignInUIClientBuilder 建立 UIClients Map
 * - 通過 parametersOf 傳遞給 ViewModel
 * - ViewModel 將 Map 傳遞給 UseCase
 *
 * 優點：
 * - UI 層負責創建平台特定的 UIClient（有 Context/Activity）
 * - ViewModel 和 UseCase 只依賴抽象接口
 * - 避免生命週期和依賴注入的複雜問題
 */
val authModule = module {
    // Data 層 - Firebase Auth
    single { Firebase.auth }

    // Data 層 - AuthManager（Repository 層 - 後端認證）
    single {
        AuthManager(getAll<AuthProvider>().toSet())
    }

    // Data 層 - AnonymousAuthProvider（跨平台通用）
    single {
        AnonymousAuthProvider(auth = get())
    } bind AuthProvider::class

    // Data 層 - EmailPasswordAuthProvider
    single {
        EmailPasswordAuthProvider(auth = get())
    } bind AuthProvider::class

    // Domain 層 - UseCases
    single {
        SignInUseCase(authManager = get())
    }

    singleOf(::SignOutUseCase)

    singleOf(::GetCurrentUserUseCase)

    singleOf(::SendEmailVerificationUseCase)

    singleOf(::SendPasswordResetEmailUseCase)

    singleOf(::CheckEmailVerificationUseCase)

    single {
        SignUpUseCase(
            emailPasswordAuthProvider = get<EmailPasswordAuthProvider>()
        )
    }

    // Presentation 層 - ViewModel
    // 使用 parametersOf 接收 UIClients Map
    viewModel { params ->
        SignInViewModel(
            signInUseCase = get(),
            uiClients = params.get()
        )
    }
}
