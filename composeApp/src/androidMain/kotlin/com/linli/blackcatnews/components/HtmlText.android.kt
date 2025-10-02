package com.linli.blackcatnews.components

import android.webkit.WebView
import android.webkit.WebSettings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Android 平台的 HTML 渲染实现
 * 使用 WebView 来渲染 HTML 内容
 */
@Composable
actual fun HtmlText(
    html: String,
    modifier: Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    // 启用 JavaScript
                    javaScriptEnabled = true

                    // 启用 DOM 存储
                    domStorageEnabled = true

                    // 启用缓存
                    cacheMode = WebSettings.LOAD_DEFAULT

                    // 支持缩放
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false

                    // 自适应屏幕
                    useWideViewPort = true
                    loadWithOverviewMode = true

                    // 允许加载混合内容（HTTP 和 HTTPS）
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                    // 启用图片加载
                    loadsImagesAutomatically = true
                    blockNetworkImage = false
                    blockNetworkLoads = false
                }

                // 设置背景透明
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }
        },
        update = { webView ->
            // 使用 loadDataWithBaseURL 来渲染 HTML
            // 设置 baseURL 为 https:// 以支持加载外部资源
            webView.loadDataWithBaseURL(
                "https://www.bbc.com/",
                html,
                "text/html",
                "UTF-8",
                null
            )
        }
    )
}
