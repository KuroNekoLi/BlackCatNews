package com.linli.blackcatnews.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSURL
import platform.WebKit.WKPreferences
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration

/**
 * iOS 平台的 HTML 渲染实现
 * 使用 WKWebView 来渲染 HTML 内容
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun HtmlText(
    html: String,
    modifier: Modifier
) {
    // 添加调试日志
    println("iOS HtmlText - HTML length: ${html.length}")
    println("iOS HtmlText - HTML preview: ${html.take(200)}")

    // 创建 WKWebView 配置
    val configuration = remember {
        WKWebViewConfiguration().apply {
            // 配置偏好设置
            preferences = WKPreferences().apply {
                javaScriptCanOpenWindowsAutomatically = false
            }

            // 允许内联播放媒体
            allowsInlineMediaPlayback = true

            // 允许图片元素自动播放
            @Suppress("CAST_NEVER_SUCCEEDS")
            mediaTypesRequiringUserActionForPlayback = 0u

            // 允许空中播放
            allowsAirPlayForMediaPlayback = true
        }
    }

    UIKitView(factory = {
        WKWebView(
            frame = CGRectZero.readValue(),
            configuration = configuration
        ).apply {
            // 设置背景
            opaque = false

            // 配置滚动视图
            scrollView.apply {
                scrollEnabled = true
                bounces = true
                showsVerticalScrollIndicator = true
                showsHorizontalScrollIndicator = false
            }

            // 允许链接预览
            allowsLinkPreview = true
        }
    },
        modifier = modifier,
        update = { webView ->
            // 设置 baseURL 以支持加载外部资源
            val baseURL = NSURL.URLWithString("https://www.bbc.com/")

            // 加载 HTML 内容
            webView.loadHTMLString(
                string = html,
                baseURL = baseURL
            )
        }, properties = UIKitInteropProperties(isInteractive = true, isNativeAccessibilityEnabled = true))
}
