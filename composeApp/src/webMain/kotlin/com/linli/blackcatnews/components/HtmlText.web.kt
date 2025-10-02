package com.linli.blackcatnews.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Web 平台的 HTML 渲染实现
 * 目前使用简化版本，直接显示 HTML 文本
 * TODO: 在未来可以使用 Compose for Web 的 HTML DSL 来实现完整的 HTML 渲染
 */
@Composable
actual fun HtmlText(
    html: String,
    modifier: Modifier
) {
    // Web 平台的简化实现
    // 在实际应用中，您可以使用 iframe 或其他 Web 技术来渲染 HTML
    Text(
        text = html,
        modifier = modifier
    )
}
