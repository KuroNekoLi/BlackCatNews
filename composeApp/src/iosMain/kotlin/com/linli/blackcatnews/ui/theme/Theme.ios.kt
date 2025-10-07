package com.linli.blackcatnews.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/**
 * iOS 平台的动态颜色支持实现
 * iOS 目前不支持 Material You 风格的动态颜色，返回 null 使用默认配色
 */
@Composable
actual fun getDynamicColorScheme(darkTheme: Boolean): ColorScheme? {
    // iOS 不支持动态颜色，返回 null
    return null
}
