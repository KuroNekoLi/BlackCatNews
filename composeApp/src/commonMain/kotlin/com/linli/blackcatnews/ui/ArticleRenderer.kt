// File: ArticleRenderer.kt
package com.linli.blackcatnews.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.linli.blackcatnews.model.*

/**
 * 文章渲染器 Composable
 * @param article 要渲染的文章資料
 * @param modifier Modifier
 */
@Composable
fun ArticleView(
    article: ArticleData,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 渲染標題
        item {
            TitleView(title = article.title)
        }

        // 渲染所有區塊
        itemsIndexed(article.blocks) { index, block ->
            BlockView(block = block)
        }

        // 底部留白
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * 標題視圖
 */
@Composable
private fun TitleView(title: Title) {
    val fontSize = title.style?.sizeSp?.sp ?: 28.sp
    val color = title.style?.colorHex?.let { parseColor(it) }
        ?: MaterialTheme.colorScheme.onBackground
    val fontWeight = when (title.style?.fontWeight?.lowercase()) {
        "bold" -> FontWeight.Bold
        "normal" -> FontWeight.Normal
        else -> FontWeight.Bold
    }

    Text(
        text = title.text,
        style = MaterialTheme.typography.headlineLarge.copy(
            fontSize = fontSize,
            color = color,
            fontWeight = fontWeight
        ),
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

/**
 * 區塊視圖
 */
@Composable
private fun BlockView(block: Block) {
    when (block) {
        is Block.Heading -> HeadingView(heading = block)
        is Block.Paragraph -> ParagraphView(paragraph = block)
        is Block.ImageBlock -> ImageBlockView(image = block)
        is Block.UnorderedList -> UnorderedListView(list = block)
        is Block.OrderedList -> OrderedListView(list = block)
        is Block.HtmlFallback -> HtmlFallbackView(fallback = block)
    }
}

/**
 * 標題區塊視圖
 */
@Composable
private fun HeadingView(heading: Block.Heading) {
    val fontSize = heading.style?.sizeSp?.sp ?: when (heading.level) {
        1 -> 26.sp
        2 -> 22.sp
        3 -> 18.sp
        else -> 16.sp
    }
    val color = heading.style?.colorHex?.let { parseColor(it) }
        ?: MaterialTheme.colorScheme.onBackground
    val fontWeight = when (heading.style?.fontWeight?.lowercase()) {
        "bold" -> FontWeight.Bold
        "normal" -> FontWeight.Normal
        else -> FontWeight.SemiBold
    }

    Text(
        text = heading.text,
        style = MaterialTheme.typography.titleLarge.copy(
            fontSize = fontSize,
            color = color,
            fontWeight = fontWeight
        ),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

/**
 * 段落視圖
 */
@Composable
private fun ParagraphView(paragraph: Block.Paragraph) {
    val fontSize = paragraph.style?.sizeSp?.sp ?: 16.sp
    val color = paragraph.style?.colorHex?.let { parseColor(it) }
        ?: MaterialTheme.colorScheme.onBackground
    val fontWeight = when (paragraph.style?.fontWeight?.lowercase()) {
        "bold" -> FontWeight.Bold
        "normal" -> FontWeight.Normal
        else -> FontWeight.Normal
    }

    Text(
        text = paragraph.text,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = fontSize,
            color = color,
            fontWeight = fontWeight,
            lineHeight = 24.sp
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

/**
 * 圖片區塊視圖
 */
@Composable
private fun ImageBlockView(image: Block.ImageBlock) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        AsyncImage(
            model = image.src,
            contentDescription = image.alt ?: "圖片",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.FillWidth,  // 寬度填滿，高度自動調整
            onError = { error ->
                // 錯誤處理
                println("❌ Image load failed: ${image.src}")
                println("Error: ${error.result.throwable?.message}")
            },
            onSuccess = { success ->
                println("✅ Image loaded successfully: ${image.src}")
            }
        )

        // 圖片說明
        image.caption?.let { caption ->
            Text(
                text = caption,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * 無序列表視圖
 */
@Composable
private fun UnorderedListView(list: Block.UnorderedList) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        list.items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * 有序列表視圖
 */
@Composable
private fun OrderedListView(list: Block.OrderedList) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        list.items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "${index + 1}.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * HTML Fallback 視圖（簡單顯示提示）
 */
@Composable
private fun HtmlFallbackView(fallback: Block.HtmlFallback) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "[不支援的內容區塊]",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * 解析 HEX 顏色字串
 */
private fun parseColor(hex: String): Color {
    return try {
        val cleanHex = hex.removePrefix("#")
        val colorInt = cleanHex.toLong(16)

        when (cleanHex.length) {
            6 -> Color(0xFF000000 or colorInt) // RGB
            8 -> Color(colorInt) // ARGB
            else -> Color.Unspecified
        }
    } catch (e: Exception) {
        Color.Unspecified
    }
}
