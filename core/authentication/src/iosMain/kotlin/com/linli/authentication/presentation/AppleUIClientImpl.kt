package com.linli.authentication.presentation

import com.linli.authentication.AuthCredential
import com.linli.authentication.domain.AppleSignInUIClient
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationErrorCanceled
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASAuthorizationScopeFullName
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * iOS 平台的 Apple Sign-In UI 客戶端實作
 *
 * 使用 iOS 原生的 AuthenticationServices framework 實現 Sign in with Apple
 *
 * 注意：
 * - 需要在 Xcode 中啟用 "Sign in with Apple" Capability
 * - 不需要額外的 CocoaPods 依賴
 * - 使用 ASAuthorizationController 處理授權流程
 */
@OptIn(ExperimentalForeignApi::class)
class AppleUIClientImpl : AppleSignInUIClient {

    /**
     * 保存當前的 delegate 引用，防止被 GC 回收
     */
    private var currentDelegate: AppleAuthDelegate? = null
    private var currentController: ASAuthorizationController? = null

    /**
     * 啟動 Apple 登入 UI 並取得憑證
     *
     * 流程：
     * 1. 創建 ASAuthorizationAppleIDProvider
     * 2. 請求 fullName 和 email scope
     * 3. 顯示 Apple 登入 UI
     * 4. 使用者授權後獲取 identityToken 和 authorizationCode
     * 5. 包裝成 AuthCredential 返回
     *
     * @return AuthCredential 包含 idToken，或 null（使用者取消）
     * @throws Exception 當授權失敗時
     */
    override suspend fun getCredential(): AuthCredential? =
        suspendCancellableCoroutine { continuation ->
            try {
                // 1. 創建 Apple ID Provider
                val provider = ASAuthorizationAppleIDProvider()

                // 2. 創建請求，要求 fullName 和 email
                val request = provider.createRequest().apply {
                    requestedScopes = listOf(
                        ASAuthorizationScopeFullName,
                        ASAuthorizationScopeEmail
                    )
                }

                // 3. 創建並保存 delegate（防止被 GC）
                val delegate = AppleAuthDelegate(
                    onSuccess = { credential ->
                        currentDelegate = null
                        currentController = null
                        credential.user
                        val identityToken = credential.identityToken?.toUtf8String()
                        val authorizationCode = credential.authorizationCode?.toUtf8String()

                        if (identityToken != null) {
                            // 包裝成 AuthCredential
                            val authCredential = AuthCredential(
                                idToken = identityToken,
                                accessToken = authorizationCode,
                                rawNonce = null
                            )
                            continuation.resume(authCredential)
                        } else {
                            continuation.resumeWithException(
                                Exception("Apple Sign-In 失敗：無法取得 Identity Token")
                            )
                        }
                    },
                    onCancel = {
                        currentDelegate = null
                        currentController = null
                        continuation.resume(null)
                    },
                    onError = { error ->
                        currentDelegate = null
                        currentController = null
                        continuation.resumeWithException(
                            Exception("Apple Sign-In 錯誤：${error.localizedDescription}")
                        )
                    }
                )
                currentDelegate = delegate

                // 4. 創建並保存 controller
                val controller = ASAuthorizationController(authorizationRequests = listOf(request))
                controller.delegate = delegate
                controller.presentationContextProvider = delegate
                currentController = controller

                // 5. 設置取消處理
                continuation.invokeOnCancellation {
                    currentDelegate = null
                    currentController = null
                }

                // 6. 啟動授權流程
                controller.performRequests()

            } catch (e: Exception) {
                currentDelegate = null
                currentController = null
                continuation.resumeWithException(
                    Exception("啟動 Apple Sign-In 失敗：${e.message}", e)
                )
            }
        }

    /**
     * NSData 轉 UTF-8 字串的擴展函數
     */
    private fun NSData.toUtf8String(): String? {
        return NSString.create(
            data = this,
            encoding = NSUTF8StringEncoding
        )?.toString()
    }
}

/**
 * Apple 授權委派
 *
 * 實現 ASAuthorizationControllerDelegate 和 ASAuthorizationControllerPresentationContextProviding
 * 處理授權成功、失敗和取消的情況
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class AppleAuthDelegate(
    private val onSuccess: (ASAuthorizationAppleIDCredential) -> Unit,
    private val onCancel: () -> Unit,
    private val onError: (NSError) -> Unit
) : NSObject(),
    ASAuthorizationControllerDelegateProtocol,
    ASAuthorizationControllerPresentationContextProvidingProtocol {

    /**
     * 提供展示授權 UI 的窗口
     */
    override fun presentationAnchorForAuthorizationController(
        controller: ASAuthorizationController
    ): UIWindow {
        val window = UIApplication.sharedApplication.keyWindow
            ?: UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow
            ?: throw IllegalStateException("無法找到有效的 UIWindow")
        return window
    }

    /**
     * 授權成功回調
     */
    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization: ASAuthorization
    ) {
        when (val credential = didCompleteWithAuthorization.credential) {
            is ASAuthorizationAppleIDCredential -> {
                onSuccess(credential)
            }

            else -> {
                val error = NSError.errorWithDomain(
                    domain = "AppleAuthError",
                    code = -1,
                    userInfo = mapOf("message" to "未知的憑證類型")
                )
                onError(error)
            }
        }
    }

    /**
     * 授權失敗或取消回調
     */
    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError: NSError
    ) {
        when (didCompleteWithError.code) {
            ASAuthorizationErrorCanceled -> {
                onCancel()
            }

            else -> {
                onError(didCompleteWithError)
            }
        }
    }
}
