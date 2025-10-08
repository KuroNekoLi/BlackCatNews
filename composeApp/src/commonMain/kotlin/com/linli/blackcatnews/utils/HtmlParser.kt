// File: HtmlParser.kt
package com.linli.blackcatnews.utils

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode
import com.linli.blackcatnews.model.*

/**
 * 解析 HTML 並轉換為 ArticleData
 * @param html HTML 字串
 * @param baseUri 基礎 URI，用於解析相對路徑
 * @return ArticleData 物件
 */
fun parseHtmlToArticle(html: String, baseUri: String? = null): ArticleData {
    return try {
        val doc = Ksoup.parse(html, baseUri ?: "")

        // 提取標題（優先順序：h1 > h2 > 第一個段落開頭）
        val titleElement = doc.selectFirst("h1") ?: doc.selectFirst("h2")
        val title = extractTitle(doc)

        // 解析所有區塊
        val blocks = mutableListOf<Block>()
        var blockIndex = 0

        // 找到 article 或 body 標籤
        val container = doc.selectFirst("article") ?: doc.body()

        if (container != null) {
            for (element in container.children()) {
                // 跳過已經用作標題的第一個 h1
                if (element == titleElement) {
                    continue
                }

                val parsedBlocks = parseElement(element, blockIndex)
                blocks.addAll(parsedBlocks)
                blockIndex += parsedBlocks.size
            }
        }

        ArticleData(
            title = title,
            blocks = blocks,
            originalHtml = html,
            baseUri = baseUri
        )
    } catch (e: Exception) {
        // 發生錯誤時返回空白文章
        ArticleData(
            title = Title("解析錯誤"),
            blocks = listOf(
                Block.HtmlFallback(
                    html = html,
                    blockIndex = 0
                )
            ),
            originalHtml = html,
            baseUri = baseUri
        )
    }
}

/**
 * 提取文章標題
 */
private fun extractTitle(doc: Document): Title {
    // 優先使用 h1
    doc.selectFirst("h1")?.let {
        return Title(text = it.text().trim())
    }

    // 其次使用 h2
    doc.selectFirst("h2")?.let {
        return Title(text = it.text().trim())
    }

    // 最後使用第一個段落的前 50 字
    doc.selectFirst("p")?.let {
        val text = it.text().trim()
        return Title(text = if (text.length > 50) text.take(50) + "..." else text)
    }

    return Title(text = "無標題")
}

/**
 * 解析單一元素為 Block 列表
 */
private fun parseElement(element: Element, startIndex: Int): List<Block> {
    return try {
        when (element.tagName().lowercase()) {
            "h1" -> listOf(
                Block.Heading(
                    level = 1,
                    text = element.text().trim(),
                    blockIndex = startIndex,
                    originalHtml = element.outerHtml()
                )
            )

            "h2" -> listOf(
                Block.Heading(
                    level = 2,
                    text = element.text().trim(),
                    blockIndex = startIndex,
                    originalHtml = element.outerHtml()
                )
            )

            "h3" -> listOf(
                Block.Heading(
                    level = 3,
                    text = element.text().trim(),
                    blockIndex = startIndex,
                    originalHtml = element.outerHtml()
                )
            )

            "p" -> {
                val text = element.text().trim()
                if (text.isNotEmpty()) {
                    listOf(
                        Block.Paragraph(
                            text = text,
                            blockIndex = startIndex,
                            originalHtml = element.outerHtml()
                        )
                    )
                } else {
                    emptyList()
                }
            }

            "figure" -> parseFigure(element, startIndex)
            "img" -> listOf(parseImage(element, startIndex))
            "ul" -> listOf(parseUnorderedList(element, startIndex))
            "ol" -> listOf(parseOrderedList(element, startIndex))
            "div", "section", "article" -> {
                // 展平容器，遞迴解析子元素
                val blocks = mutableListOf<Block>()
                var currentIndex = startIndex
                for (child in element.children()) {
                    val childBlocks = parseElement(child, currentIndex)
                    blocks.addAll(childBlocks)
                    currentIndex += childBlocks.size
                }
                blocks
            }

            "iframe", "video", "form", "script" -> {
                // 不支援的複雜元素，使用 fallback
                listOf(
                    Block.HtmlFallback(
                        html = element.outerHtml(),
                        blockIndex = startIndex
                    )
                )
            }

            else -> emptyList()
        }
    } catch (e: Exception) {
        // 解析失敗時使用 fallback
        listOf(
            Block.HtmlFallback(
                html = element.outerHtml(),
                blockIndex = startIndex
            )
        )
    }
}

/**
 * 解析 figure 元素（通常包含圖片和說明）
 */
private fun parseFigure(figure: Element, blockIndex: Int): List<Block> {
    val img = figure.selectFirst("img")
    val figcaption = figure.selectFirst("figcaption")

    return if (img != null) {
        val src = chooseBestImageSrc(img)
        if (src.isNotEmpty()) {
            listOf(
                Block.ImageBlock(
                    src = src,
                    alt = img.attr("alt"),
                    caption = figcaption?.text()?.trim(),
                    blockIndex = blockIndex,
                    originalHtml = figure.outerHtml()
                )
            )
        } else {
            emptyList()
        }
    } else {
        emptyList()
    }
}

/**
 * 解析 img 元素
 */
private fun parseImage(img: Element, blockIndex: Int): Block.ImageBlock {
    val src = chooseBestImageSrc(img)
    return Block.ImageBlock(
        src = src,
        alt = img.attr("alt"),
        caption = null,
        blockIndex = blockIndex,
        originalHtml = img.outerHtml()
    )
}

/**
 * 選擇最佳圖片來源（處理 srcset）
 */
private fun chooseBestImageSrc(img: Element): String {
    val srcset = img.attr("srcset")

    if (srcset.isNotEmpty()) {
        val chosen = chooseSrcFromSrcset(srcset)
        if (chosen != null) return chosen
    }

    val src = img.attr("src")
    if (src.isNotEmpty()) {
        return img.absUrl("src").takeIf { it.isNotEmpty() } ?: src
    }

    return ""
}

/**
 * 從 srcset 中選擇最合適的圖片 URL
 * 優先順序：480w -> 640w -> 320w -> 第一個
 */
private fun chooseSrcFromSrcset(srcset: String?): String? {
    if (srcset.isNullOrEmpty()) return null

    // 解析 srcset：格式為 "url1 width1, url2 width2, ..."
    val candidates = srcset.split(",").mapNotNull { part ->
        val trimmed = part.trim()
        val tokens = trimmed.split(" ")
        if (tokens.size >= 2) {
            val url = tokens[0]
            val descriptor = tokens[1].lowercase()
            url to descriptor
        } else {
            null
        }
    }

    if (candidates.isEmpty()) return null

    // 優先順序：480w -> 640w -> 320w
    val preferredWidths = listOf("480w", "640w", "320w", "800w")
    for (width in preferredWidths) {
        candidates.find { it.second == width }?.let { return it.first }
    }

    // 如果沒有匹配，返回第一個
    return candidates.firstOrNull()?.first
}

/**
 * 解析無序列表
 */
private fun parseUnorderedList(ul: Element, blockIndex: Int): Block.UnorderedList {
    val items = ul.select("li").map { it.text().trim() }.filter { it.isNotEmpty() }
    return Block.UnorderedList(
        items = items,
        blockIndex = blockIndex,
        originalHtml = ul.outerHtml()
    )
}

/**
 * 解析有序列表
 */
private fun parseOrderedList(ol: Element, blockIndex: Int): Block.OrderedList {
    val items = ol.select("li").map { it.text().trim() }.filter { it.isNotEmpty() }
    return Block.OrderedList(
        items = items,
        blockIndex = blockIndex,
        originalHtml = ol.outerHtml()
    )
}
