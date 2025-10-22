package com.linli.authentication.data

import cocoapods.GoogleSignIn.GIDSignIn
import com.linli.authentication.AuthCredential
import com.linli.authentication.ProviderType
import com.linli.authentication.domain.model.UserSession
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UIKit.UIViewController
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import dev.gitlive.firebase.auth.GoogleAuthProvider as GitliveGoogleAuthProvider

/**
 * iOS 平台的 Google Sign-In 認證供應商
 * 使用 GoogleSignIn SDK (透過 CocoaPods) + GitLive Firebase SDK
 */
@OptIn(ExperimentalForeignApi::class)
class GoogleAuthProvider(
    private val auth: FirebaseAuth
) : AuthProvider {
    override val type: ProviderType = ProviderType.Google

    /**
     * 執行 Google 登入流程
     * 使用已經取得的憑證進行 Firebase 登入
     * @param credential 包含 idToken 和 accessToken 的認證憑證
     */
    override suspend fun signIn(credential: AuthCredential): Result<UserSession> = runCatching {
        // 驗證必要的 token
        val idToken = requireNotNull(credential.idToken) { "缺少 Google ID token" }

        // 使用 ID Token 建立 Firebase 憑證並登入
        val gitliveCred = GitliveGoogleAuthProvider.credential(
            idToken = idToken,
            accessToken = credential.accessToken
        )

        val result = auth.signInWithCredential(gitliveCred)
        val user = result.user
            ?: throw IllegalStateException("Firebase 登入成功但無法取得使用者資訊")

        // 轉換為 UserSession
        UserSession(
            uid = user.uid,
            email = user.email,
            providerIds = user.providerData.map { it.providerId }
        )
    }

    /**
     * 使用 UIViewController 取得 Google 登入憑證（不進行 Firebase 登入）
     * @param presentingViewController 用於呈現登入畫面的 ViewController
     * @return AuthCredential 包含 idToken 和 accessToken
     */
    suspend fun getCredentialWithViewController(presentingViewController: UIViewController): AuthCredential {
        // 步驟 1: 使用 GoogleSignIn SDK 取得 ID Token
        val googleSignInResult = performGoogleSignIn(presentingViewController)

        // 步驟 2: 返回 AuthCredential
        return AuthCredential(
            idToken = googleSignInResult.idToken,
            accessToken = googleSignInResult.accessToken
        )
    }

    /**
     * 使用 UIViewController 執行 Google 登入並完成 Firebase 認證
     * @param presentingViewController 用於呈現登入畫面的 ViewController
     * @return UserSession 登入成功的使用者資訊
     */
    suspend fun signInWithViewController(presentingViewController: UIViewController): Result<UserSession> =
        runCatching {
            // 步驟 1: 使用 GoogleSignIn SDK 取得 ID Token
            val googleSignInResult = performGoogleSignIn(presentingViewController)

            // 步驟 2: 使用 ID Token 建立 Firebase 憑證並登入
            val gitliveCred = GitliveGoogleAuthProvider.credential(
                idToken = googleSignInResult.idToken,
                accessToken = googleSignInResult.accessToken
            )

            val result = auth.signInWithCredential(gitliveCred)
            val user = result.user
                ?: throw IllegalStateException("Firebase 登入成功但無法取得使用者資訊")

            // 步驟 3: 轉換為 UserSession
            UserSession(
                uid = user.uid,
                email = user.email,
                providerIds = user.providerData.map { it.providerId }
            )
        }

    /**
     * 執行 Google Sign-In 流程並取得 ID Token
     */
    private suspend fun performGoogleSignIn(presentingViewController: UIViewController): GoogleSignInResult =
        suspendCancellableCoroutine { continuation ->
            dispatch_async(dispatch_get_main_queue()) {
                // Google Sign-In 已在應用啟動時配置（在 iOSApp.swift 中）
                // 直接執行登入
                GIDSignIn.sharedInstance.signInWithPresentingViewController(
                    presentingViewController
                ) { result, error ->
                    if (error != null) {
                        continuation.resumeWithException(
                            Exception(error.localizedDescription ?: "Google Sign-In 失敗")
                        )
                        return@signInWithPresentingViewController
                    }

                    val user = result?.user
                    val idToken = user?.idToken?.tokenString
                    val accessToken = user?.accessToken?.tokenString
                    val email = user?.profile?.email

                    if (idToken == null) {
                        continuation.resumeWithException(
                            Exception("無法取得 Google ID Token")
                        )
                        return@signInWithPresentingViewController
                    }

                    continuation.resume(
                        GoogleSignInResult(
                            idToken = idToken,
                            accessToken = accessToken,
                            email = email
                        )
                    )
                }
            }
        }

    override suspend fun signOut(): Result<Unit> = runCatching {
        // Firebase 登出
        auth.signOut()

        // Google Sign-In SDK 登出
        GIDSignIn.sharedInstance.signOut()
    }
}

/**
 * Google Sign-In 結果資料類別
 */
private data class GoogleSignInResult(
    val idToken: String,
    val accessToken: String?,
    val email: String?
)
