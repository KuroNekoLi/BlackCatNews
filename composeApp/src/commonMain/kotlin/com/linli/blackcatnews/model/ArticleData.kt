// File: ArticleData.kt
package com.linli.blackcatnews.model

/**
 * 文章資料結構
 * @param title 文章標題
 * @param blocks 文章內容區塊列表
 * @param originalHtml 原始 HTML（可選）
 * @param baseUri 基礎 URI，用於解析相對路徑（可選）
 */
data class ArticleData(
    val title: Title,
    val blocks: List<Block>,
    val originalHtml: String? = null,
    val baseUri: String? = null
)

/**
 * 標題資料
 * @param text 標題文字
 * @param style 文字樣式（可選）
 */
data class Title(
    val text: String,
    val style: TextStyleAttr? = null
)

/**
 * 內容區塊基類（sealed class 確保類型安全）
 */
sealed class Block {
    /**
     * 段落區塊
     * @param text 段落文字
     * @param style 文字樣式（可選）
     * @param blockIndex 區塊索引（在文章中的順序）
     * @param originalHtml 原始 HTML（可選）
     */
    data class Paragraph(
        val text: String,
        val style: TextStyleAttr? = null,
        val blockIndex: Int = 0,
        val originalHtml: String? = null
    ) : Block()

    /**
     * 標題區塊
     * @param level 標題層級（1-6）
     * @param text 標題文字
     * @param style 文字樣式（可選）
     * @param blockIndex 區塊索引
     * @param originalHtml 原始 HTML（可選）
     */
    data class Heading(
        val level: Int,
        val text: String,
        val style: TextStyleAttr? = null,
        val blockIndex: Int = 0,
        val originalHtml: String? = null
    ) : Block()

    /**
     * 圖片區塊
     * @param src 圖片來源 URL
     * @param alt 替代文字
     * @param caption 圖片說明（通常來自 figcaption）
     * @param style 圖片樣式屬性（可選）
     * @param blockIndex 區塊索引
     * @param originalHtml 原始 HTML（可選）
     */
    data class ImageBlock(
        val src: String,
        val alt: String?,
        val caption: String?,
        val style: ImageAttr? = null,
        val blockIndex: Int = 0,
        val originalHtml: String? = null
    ) : Block()

    /**
     * 無序列表區塊
     * @param items 列表項目文字列表
     * @param blockIndex 區塊索引
     * @param originalHtml 原始 HTML（可選）
     */
    data class UnorderedList(
        val items: List<String>,
        val blockIndex: Int = 0,
        val originalHtml: String? = null
    ) : Block()

    /**
     * 有序列表區塊
     * @param items 列表項目文字列表
     * @param blockIndex 區塊索引
     * @param originalHtml 原始 HTML（可選）
     */
    data class OrderedList(
        val items: List<String>,
        val blockIndex: Int = 0,
        val originalHtml: String? = null
    ) : Block()

    /**
     * HTML Fallback 區塊（用於不支援的複雜元素）
     * @param html 原始 HTML 字串
     * @param blockIndex 區塊索引
     */
    data class HtmlFallback(
        val html: String,
        val blockIndex: Int = 0
    ) : Block()
}

/**
 * 文字樣式屬性
 * @param colorHex 顏色（HEX 格式，例如 "#FF0000"）
 * @param sizeSp 文字大小（單位：sp）
 * @param fontWeight 字重（例如 "bold", "normal"）
 * @param isItalic 是否斜體
 */
data class TextStyleAttr(
    val colorHex: String? = null,
    val sizeSp: Float? = null,
    val fontWeight: String? = null,
    val isItalic: Boolean = false
)

/**
 * 圖片屬性
 * @param widthPx 寬度（單位：px）
 * @param heightPx 高度（單位：px）
 * @param fit 填充模式（"cover" 或 "contain"）
 */
data class ImageAttr(
    val widthPx: Int? = null,
    val heightPx: Int? = null,
    val fit: String? = null // "cover"|"contain"
)
