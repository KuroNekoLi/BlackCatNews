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
        .replace("\\\"", "\"")
        .replace("\\/", "/")
        .replace("\\n", "\n")
        .replace("\\r", "\r")
        .replace("\\t", "\t")
        .replace("\\\\", "\\")
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
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("/", "\\/")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
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
            * { box-sizing: border-box; margin: 0; padding: 0; }
            html { font-size: 16px; -webkit-text-size-adjust: 100%; }
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
            h1 { font-size: 1.75em; font-weight: 700; margin: 0.67em 0; line-height: 1.2; }
            h2 { font-size: 1.5em; font-weight: 700; margin: 0.83em 0; line-height: 1.3; }
            h3 { font-size: 1.25em; font-weight: 600; margin: 1em 0; line-height: 1.4; }
            p { margin: 1em 0; font-size: 1em; }
            img { max-width: 100%; height: auto; display: block; margin: 0.5em 0; }
            figure { margin: 1.5em 0; }
            figcaption { font-size: 0.875em; color: #666; margin-top: 0.5em; }
            a { color: #0066cc; text-decoration: underline; }
            ul, ol { margin: 1em 0; padding-left: 2em; }
            li { margin: 0.5em 0; }
            blockquote { margin: 1em 0; padding-left: 1em; border-left: 4px solid #ddd; color: #666; }
            strong, b { font-weight: 700; }
            em, i { font-style: italic; }
          </style>
        </head>
        <body>
          $this
        </body>
        </html>
    """.trimIndent()
}

/**
 * HTML entity 解碼，支援常見字元與數值實體
 */
fun String.decodeHtmlEntities(): String {
    if (!contains("&")) return this

    val builder = StringBuilder(length)
    var index = 0
    while (index < length) {
        val currentChar = this[index]
        if (currentChar != '&') {
            builder.append(currentChar)
            index += 1
            continue
        }

        val semicolonIndex = indexOf(';', startIndex = index + 1)
        if (semicolonIndex == -1) {
            builder.append(currentChar)
            index += 1
            continue
        }

        val entity = substring(index + 1, semicolonIndex)
        val decoded = decodeNamedEntity(entity) ?: decodeNumericEntity(entity)
        if (decoded != null) {
            builder.append(decoded)
            index = semicolonIndex + 1
        } else {
            builder.append(currentChar)
            index += 1
        }
    }
    return builder.toString()
}

private fun decodeNamedEntity(entity: String): String? {
    return when (entity) {
        "amp" -> "&"
        "lt" -> "<"
        "gt" -> ">"
        "quot" -> "\""
        "apos" -> "'"
        "nbsp" -> "\u00A0"
        "rsquo", "lsquo" -> "'"
        "ldquo" -> "\u201C"
        "rdquo" -> "\u201D"
        "hellip" -> "\u2026"
        "mdash" -> "\u2014"
        "ndash" -> "\u2013"
        else -> null
    }
}

private fun decodeNumericEntity(entity: String): String? {
    if (entity.isEmpty()) return null
    val numericPart = when {
        entity.startsWith("#x", ignoreCase = true) -> entity.substring(2)
        entity.startsWith("#") -> entity.substring(1)
        else -> return null
    }

    val radix = if (entity.startsWith("#x", ignoreCase = true)) 16 else 10
    return numericPart.toIntOrNull(radix)?.let { codePoint ->
        when {
            codePoint in 0..Char.MAX_VALUE.code -> codePoint.toChar().toString()
            codePoint in 0x10000..0x10FFFF -> {
                val highSurrogate = ((codePoint - 0x10000) shr 10).or(0xD800)
                val lowSurrogate = ((codePoint - 0x10000) and 0x3FF).or(0xDC00)
                charArrayOf(highSurrogate.toChar(), lowSurrogate.toChar()).concatToString()
            }
            else -> null
        }
    }
}
