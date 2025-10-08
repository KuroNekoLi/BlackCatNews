package com.linli.blackcatnews.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 渲染 HTML 内容的通用组件
 * @param html HTML 字符串内容
 * @param modifier Compose 修饰符
 */
@Composable
expect fun HtmlText(
    html: String,
    modifier: Modifier = Modifier
)
