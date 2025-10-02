package com.linli.blackcatnews.utils

/**
 * 将 JSON 格式的 HTML 字符串转换为标准 HTML
 * 去掉 JSON 中的转义字符（如反斜线）
 *
 * 使用示例：
 * ```
 * val jsonHtml = "<h1>Title<\/h1><p>Content with \"quotes\"<\/p>"
 * val html = jsonHtml.fromJsonHtml()
 * // 结果: <h1>Title</h1><p>Content with "quotes"</p>
 * ```
 *
 * @return 转换后的标准 HTML 字符串
 */
fun String.fromJsonHtml(): String {
    return this
        .replace("\\\"", "\"")  // 去掉转义的双引号
        .replace("\\/", "/")     // 去掉转义的斜线
        .replace("\\n", "\n")    // 转换换行符
        .replace("\\r", "\r")    // 转换回车符
        .replace("\\t", "\t")    // 转换制表符
        .replace("\\\\", "\\")   // 去掉转义的反斜线（需要放在最后处理）
        .trim()
}

/**
 * 将标准 HTML 转换为 JSON 格式的 HTML 字符串
 *
 * 使用示例：
 * ```
 * val html = "<h1>Title</h1><p>Content with \"quotes\"</p>"
 * val jsonHtml = html.toJsonHtml()
 * // 结果: <h1>Title<\/h1><p>Content with \"quotes\"<\/p>
 * ```
 *
 * @return 转换后的 JSON 格式 HTML 字符串
 */
fun String.toJsonHtml(): String {
    return this
        .replace("\\", "\\\\")   // 转义反斜线（需要放在最前面）
        .replace("\"", "\\\"")   // 转义双引号
        .replace("/", "\\/")     // 转义斜线
        .replace("\n", "\\n")    // 转义换行符
        .replace("\r", "\\r")    // 转义回车符
        .replace("\t", "\\t")    // 转义制表符
}

/**
 * 将 HTML 片段包装成完整的 HTML 文档
 * 添加必要的 meta 标签和基础样式，确保在移动设备上正确显示
 * 
 * 使用示例：
 * ```
 * val htmlFragment = "<h1>Title</h1><p>Content</p>"
 * val fullHtml = htmlFragment.wrapHtml()
 * HtmlText(html = fullHtml)
 * ```
 * 
 * @return 包装后的完整 HTML 文档
 */
fun String.wrapHtml(): String {
    return """
        <!doctype html>
        <html lang="en">
        <head>
          <meta charset="utf-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=5.0, user-scalable=yes" />
          <style>
            * { 
              box-sizing: border-box; 
              margin: 0;
              padding: 0;
            }
            html {
              font-size: 16px;
              -webkit-text-size-adjust: 100%;
            }
            body {
              margin: 0;
              padding: 16px;
              font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
              font-size: 1rem;
              line-height: 1.6;
              color: #333;
              background: #fff;
              -webkit-font-smoothing: antialiased;
            }
            h1 { 
              font-size: 1.75em; 
              font-weight: 700;
              margin: 0.67em 0; 
              line-height: 1.2;
            }
            h2 { 
              font-size: 1.5em; 
              font-weight: 700;
              margin: 0.83em 0; 
              line-height: 1.3;
            }
            h3 { 
              font-size: 1.25em; 
              font-weight: 600;
              margin: 1em 0; 
              line-height: 1.4;
            }
            p { 
              margin: 1em 0; 
              font-size: 1em; 
            }
            img { 
              max-width: 100%; 
              height: auto; 
              display: block; 
              margin: 0.5em 0;
            }
            figure { 
              margin: 1.5em 0; 
            }
            figcaption {
              font-size: 0.875em;
              color: #666;
              margin-top: 0.5em;
            }
            a { 
              color: #0066cc; 
              text-decoration: underline; 
            }
            ul, ol {
              margin: 1em 0;
              padding-left: 2em;
            }
            li {
              margin: 0.5em 0;
            }
            blockquote {
              margin: 1em 0;
              padding-left: 1em;
              border-left: 4px solid #ddd;
              color: #666;
            }
            strong, b {
              font-weight: 700;
            }
            em, i {
              font-style: italic;
            }
          </style>
        </head>
        <body>
          $this
        </body>
        </html>
    """.trimIndent()
}
