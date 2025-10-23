package com.linli.authentication.presentation

import com.linli.authentication.AuthCredential
import com.linli.authentication.domain.AppleSignInUIClient
import com.linli.authentication.util.NonceGenerator
import com.linli.authentication.util.SHA256
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationErrorCanceled
import platform.AuthenticationServices.ASAuthorizationErrorFailed
import platform.AuthenticationServices.ASAuthorizationErrorInvalidResponse
import platform.AuthenticationServices.ASAuthorizationErrorNotHandled
import platform.AuthenticationServices.ASAuthorizationErrorUnknown
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASAuthorizationScopeFullName
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.setValue
import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * iOS 平台的 Apple Sign-In UI 客戶端實作
 *
 * 使用 iOS 原生的 AuthenticationServices framework 實現 Sign in with Apple
 *
 * 安全性特性：
 * - 使用 nonce 防止重放攻擊
 * - 支援多場景（Multi-Scene）架構
 * - 主線程執行 UI 操作
 * - 防止並發請求
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
     * 同時用於防止並發請求
     */
    private var currentDelegate: AppleAuthDelegate? = null
    private var currentController: ASAuthorizationController? = null

    /**
     * 啟動 Apple 登入 UI 並取得憑證
     *
     * 安全性流程：
     * 1. 生成隨機 nonce
     * 2. 使用 SHA-256(nonce) 設置到請求中
     * 3. 原始 nonce 回傳給 Firebase 驗證
     * 4. 顯示 Apple 登入 UI
     * 5. 使用者授權後獲取 identityToken
     * 6. 包裝成 AuthCredential（包含原始 nonce）返回
     *
     * @return AuthCredential 包含 idToken 和 rawNonce，或 null（使用者取消）
     * @throws Exception 當授權失敗或已有請求進行中時
     */
    override suspend fun getCredential(): AuthCredential? = withContext(Dispatchers.Main) {
        // 防止並發請求
        check(currentDelegate == null && currentController == null) {
            "Apple 登入正在進行中，請勿重複請求"
        }

        suspendCancellableCoroutine { continuation ->
            try {
                // 1. 生成 nonce（安全性要件）
                val originalNonce = NonceGenerator.generate()      // 原始亂數
                val hashedNonceHex = SHA256.hashHex(originalNonce) // 16進位字串

                // 2. 創建 Apple ID Provider
                val provider = ASAuthorizationAppleIDProvider()

                // 3. 創建請求，要求 fullName 和 email，並設置 nonce
                val request = provider.createRequest().apply {
                    requestedScopes =
                        listOf(ASAuthorizationScopeFullName, ASAuthorizationScopeEmail)
                    setValue(hashedNonceHex, forKey = "nonce")     // ← 這裡要放 hex，不是 Base64
                }

                // 4. 創建並保存 delegate（防止被 GC）
                val delegate = AppleAuthDelegate(
                    onSuccess = { credential ->
                        clearReferences()

                        val identityToken = credential.identityToken?.toUtf8String()
                        val authorizationCode = credential.authorizationCode?.toUtf8String()

                        if (identityToken != null) {
                            // 包裝成 AuthCredential，包含原始 nonce 供 Firebase 驗證
                            val authCredential = AuthCredential(
                                idToken = identityToken,
                                accessToken = authorizationCode,
                                rawNonce = originalNonce  // 原始 nonce，非 hash 版本
                            )
                            if (continuation.isActive) {
                                continuation.resume(authCredential)
                            }
                        } else {
                            if (continuation.isActive) {
                                continuation.resumeWithException(
                                    IllegalStateException("Apple Sign-In 失敗：無法取得 Identity Token")
                                )
                            }
                        }
                    },
                    onCancel = {
                        clearReferences()
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    },
                    onError = { error ->
                        clearReferences()
                        if (continuation.isActive) {
                            val errorMessage = mapAuthError(error)
                            continuation.resumeWithException(
                                Exception("Apple Sign-In 失敗：$errorMessage")
                            )
                        }
                    }
                )
                currentDelegate = delegate

                // 5. 創建並保存 controller
                val controller = ASAuthorizationController(authorizationRequests = listOf(request))
                controller.delegate = delegate
                controller.presentationContextProvider = delegate
                currentController = controller

                // 6. 設置取消處理
                continuation.invokeOnCancellation {
                    clearReferences()
                }

                // 7. 啟動授權流程（已在主線程）
                controller.performRequests()

            } catch (e: Exception) {
                clearReferences()
                if (continuation.isActive) {
                    continuation.resumeWithException(
                        Exception("啟動 Apple Sign-In 失敗：${e.message}", e)
                    )
                }
            }
        }
    }

    /**
     * 清除引用，防止記憶體洩漏
     */
    private fun clearReferences() {
        currentDelegate = null
        currentController = null
    }

    /**
     * 將 Apple 錯誤代碼映射為可讀的錯誤訊息
     */
    private fun mapAuthError(error: NSError): String {
        return when (error.code) {
            ASAuthorizationErrorCanceled -> "使用者取消登入"
            ASAuthorizationErrorFailed -> "授權失敗：${error.localizedDescription}"
            ASAuthorizationErrorInvalidResponse -> "無效的回應"
            ASAuthorizationErrorNotHandled -> "請求未被處理"
            ASAuthorizationErrorUnknown -> "未知錯誤"
            else -> error.localizedDescription ?: "Apple 登入錯誤 (code: ${error.code})"
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
     *
     * iOS 13+ 多場景支援：
     * 1. 優先查找前景活躍的 UIWindowScene
     * 2. 在該 scene 中找到 key window
     * 3. 降級策略：任何可用的 window
     */
    override fun presentationAnchorForAuthorizationController(
        controller: ASAuthorizationController
    ): UIWindow {
        // 獲取當前連接的場景
        val scenes = UIApplication.sharedApplication.connectedScenes

        // 查找前景活躍的 UIWindowScene
        val windowScene = scenes
            .toList()
            .filterIsInstance<UIWindowScene>()
            .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }
            ?: scenes.toList().filterIsInstance<UIWindowScene>().firstOrNull()
            ?: throw IllegalStateException("無法找到可用的 UIWindowScene")

        // 在 scene 的 windows 中查找可用的 window
        val windows = windowScene.windows as? List<*>
            ?: throw IllegalStateException("無法獲取 windows 列表")

        // 優先返回 key window，否則返回第一個可用 window
        return windows.firstOrNull { window ->
            (window as? UIWindow)?.keyWindow == true
        } as? UIWindow
            ?: windows.firstOrNull() as? UIWindow
            ?: throw IllegalStateException("無法找到可用的 UIWindow")
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
