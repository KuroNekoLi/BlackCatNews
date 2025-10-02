package com.linli.blackcatnews.examples

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.components.HtmlText
import com.linli.blackcatnews.utils.fromJsonHtml

/**
 * HtmlText 组件使用示例
 *
 * 这个示例展示了如何：
 * 1. 使用 fromJsonHtml() extension function 将 JSON 格式的 HTML 转换为标准 HTML
 * 2. 使用 HtmlText 组件渲染 HTML 内容
 */
@Composable
fun HtmlTextExample() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "HTML 渲染示例",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 示例 1: 简单的 HTML
        Text(
            text = "示例 1: 基本 HTML",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val simpleHtml = """
            <h1>欢迎使用 BlackCat News<\/h1>
            <p>这是一个新闻应用<\/p>
        """.trimIndent().fromJsonHtml()

        HtmlText(
            html = simpleHtml,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = 16.dp)
        )

        // 示例 2: 带样式的 HTML
        Text(
            text = "示例 2: 带样式的 HTML",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val styledHtml = """
            <div style=\"padding: 16px; background-color: #f5f5f5;\">
                <h2 style=\"color: #333;\">新闻标题<\/h2>
                <p style=\"color: #666;\">这是新闻的内容描述，支持<strong>粗体<\/strong>和<em>斜体<\/em>文字。<\/p>
                <ul>
                    <li>新闻要点 1<\/li>
                    <li>新闻要点 2<\/li>
                    <li>新闻要点 3<\/li>
                <\/ul>
            <\/div>
        """.trimIndent().fromJsonHtml()

        HtmlText(
            html = styledHtml,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(bottom = 16.dp)
        )

        // 示例 3: 从 API 响应获取的 JSON 格式 HTML
        Text(
            text = "示例 3: API 响应中的 HTML",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 模拟从 API 获取的 JSON 格式 HTML
        val apiResponseHtml = """
            <article>
                <header>
                    <h1>重要新闻快讯<\/h1>
                    <time datetime=\"2025-10-02\">2025年10月2日<\/time>
                <\/header>
                <section>
                    <p>这是从 API 返回的新闻内容。<\/p>
                    <p>支持多段落、<a href=\"#\">链接<\/a>和其他 HTML 元素。<\/p>
                    <blockquote>
                        \"这是一段引用文字\"
                    <\/blockquote>
                <\/section>
            <\/article>
        """.trimIndent().fromJsonHtml()

        HtmlText(
            html = apiResponseHtml,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}
