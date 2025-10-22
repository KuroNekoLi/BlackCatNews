package com.linli.authentication.presentation

import com.linli.authentication.AuthCredential
import com.linli.authentication.data.GoogleAuthProvider
import com.linli.authentication.domain.GoogleSignInUIClient
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIApplication
import platform.UIKit.UINavigationController
import platform.UIKit.UITabBarController
import platform.UIKit.UIViewController
import platform.UIKit.UIWindowScene

/**
 * iOS 平台的 Google Sign-In UI 客戶端實作
 * 
 * 實作 Domain 層的 GoogleSignInUIClient 介面
 * 使用 GoogleAuthProvider 透過 CocoaPods interop 呼叫 GoogleSignIn SDK
 */
@OptIn(ExperimentalForeignApi::class)
class GoogleUIClientImpl(
    private val googleAuthProvider: GoogleAuthProvider
) : GoogleSignInUIClient {

    /**
     * 啟動 Google 登入 UI 並取得憑證
     * 
     * Domain 層介面實作：
     * - 自動取得當前的 UIViewController
     * - 呼叫 GoogleAuthProvider 顯示登入 UI
     * - 返回憑證或 null（使用者取消）
     */
    override suspend fun getCredential(): AuthCredential? {
        return try {
            val viewController = getCurrentViewController()
                ?: throw Exception("無法取得當前的 UIViewController")

            // 使用 GoogleAuthProvider 執行 Google 登入並取得憑證
            googleAuthProvider.getCredentialWithViewController(viewController)
            
        } catch (e: Exception) {
            throw Exception("iOS Google 登入錯誤: ${e.message}", e)
        }
    }

    /**
     * 取得當前最上層的 UIViewController
     */
    private fun getCurrentViewController(): UIViewController? {
        val windowScene = UIApplication.sharedApplication.connectedScenes
            .firstOrNull() as? UIWindowScene
            ?: return null

        val window = windowScene.windows.firstOrNull { window ->
            (window as? platform.UIKit.UIWindow)?.isKeyWindow() == true
        } as? platform.UIKit.UIWindow
            ?: return null

        return getTopViewController(window.rootViewController ?: return null)
    }

    /**
     * 遞迴尋找最上層的 ViewController
     */
    private fun getTopViewController(viewController: UIViewController): UIViewController {
        viewController.presentedViewController?.let {
            return getTopViewController(it)
        }

        if (viewController is UINavigationController) {
            viewController.visibleViewController?.let {
                return getTopViewController(it)
            }
        }

        if (viewController is UITabBarController) {
            viewController.selectedViewController?.let {
                return getTopViewController(it)
            }
        }

        return viewController
    }
}
