package com.linli.blackcatnews.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.linli.blackcatnews.domain.model.BilingualParagraph
import com.linli.blackcatnews.domain.model.BilingualParagraphType
import com.linli.blackcatnews.domain.model.ReadingMode

/**
 * 雙語文本展示組件
 * 支持多種閱讀模式：英文、中文、並排、堆疊
 */
@Composable
fun BilingualTextView(
    paragraph: BilingualParagraph,
    readingMode: ReadingMode,
    modifier: Modifier = Modifier,
    onWordClick: ((String) -> Unit)? = null
) {
    when (paragraph.type) {
        BilingualParagraphType.TEXT -> renderTextParagraph(
            paragraph = paragraph,
            readingMode = readingMode,
            modifier = modifier
        )

        BilingualParagraphType.HEADING -> HeadingParagraphView(
            paragraph = paragraph,
            modifier = modifier
        )

        BilingualParagraphType.IMAGE -> ImageParagraphView(
            paragraph = paragraph,
            modifier = modifier
        )

        BilingualParagraphType.UNORDERED_LIST -> UnorderedListParagraphView(
            paragraph = paragraph,
            readingMode = readingMode,
            modifier = modifier
        )

        BilingualParagraphType.ORDERED_LIST -> OrderedListParagraphView(
            paragraph = paragraph,
            readingMode = readingMode,
            modifier = modifier
        )

        BilingualParagraphType.HTML_FALLBACK -> HtmlFallbackParagraphView(
            paragraph = paragraph,
            modifier = modifier
        )
    }
}

@Composable
private fun renderTextParagraph(
    paragraph: BilingualParagraph,
    readingMode: ReadingMode,
    modifier: Modifier
) {
    val englishText = paragraph.english.orEmpty()
    val chineseText = paragraph.chinese.orEmpty()

    when (readingMode) {
        ReadingMode.ENGLISH_ONLY -> {
            SingleLanguageText(
                text = englishText,
                language = "English",
                modifier = modifier
            )
        }

        ReadingMode.CHINESE_ONLY -> {
            SingleLanguageText(
                text = chineseText,
                language = "Chinese",
                modifier = modifier
            )
        }

        ReadingMode.SIDE_BY_SIDE -> {
            SideBySideText(
                englishText = englishText,
                chineseText = chineseText,
                modifier = modifier
            )
        }

        ReadingMode.STACKED -> {
            StackedText(
                englishText = englishText,
                chineseText = chineseText,
                modifier = modifier
            )
        }
    }
}

/**
 * 單語言文本展示
 */
@Composable
private fun SingleLanguageText(
    text: String,
    language: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * 左右並排文本展示（適合平板/橫屏）
 */
@Composable
private fun SideBySideText(
    englishText: String,
    chineseText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 英文列
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "🇬🇧 English",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = englishText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp
                )
            }
        }

        // 中文列
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "🇹🇼 中文",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = chineseText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * 上下堆疊文本展示（適合手機直屏）
 */
@Composable
private fun StackedText(
    englishText: String,
    chineseText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 英文段落
        Column {
            Text(
                text = "🇬🇧 English",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = englishText,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Divider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.dp
        )

        // 中文段落
        Column {
            Text(
                text = "🇹🇼 中文",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = chineseText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HeadingParagraphView(
    paragraph: BilingualParagraph,
    modifier: Modifier
) {
    val headingLevel = paragraph.headingLevel ?: 1
    val typography = when (headingLevel.coerceIn(1, 6)) {
        1 -> MaterialTheme.typography.headlineLarge
        2 -> MaterialTheme.typography.headlineMedium
        3 -> MaterialTheme.typography.headlineSmall
        else -> MaterialTheme.typography.titleLarge
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = paragraph.english.orEmpty(),
            style = typography,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        paragraph.chinese?.takeIf { it.isNotBlank() }?.let { chineseText ->
            Text(
                text = chineseText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
private fun ImageParagraphView(
    paragraph: BilingualParagraph,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        val imageUrl = paragraph.imageUrl
        if (imageUrl.isNullOrBlank()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 1.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "圖片載入失敗",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = paragraph.imageAlt,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.FillWidth
            )
        }

        paragraph.imageCaption?.takeIf { it.isNotBlank() }?.let { caption ->
            Text(
                text = caption,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun UnorderedListParagraphView(
    paragraph: BilingualParagraph,
    readingMode: ReadingMode,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        val englishItems = paragraph.listItems
        val chineseItems = paragraph.listItemsChinese

        when (readingMode) {
            ReadingMode.ENGLISH_ONLY -> {
                englishItems.forEach { item ->
                    BulletRow(text = item)
                }
            }

            ReadingMode.CHINESE_ONLY -> {
                val itemsToRender = if (chineseItems.isNotEmpty()) chineseItems else englishItems
                itemsToRender.forEach { item ->
                    BulletRow(text = item)
                }
            }

            ReadingMode.STACKED -> {
                englishItems.forEachIndexed { index, item ->
                    val chinese = chineseItems.getOrNull(index)
                    StackedBulletRow(
                        englishText = item,
                        chineseText = chinese
                    )
                }
            }

            ReadingMode.SIDE_BY_SIDE -> {
                SideBySideList(
                    englishItems = englishItems,
                    chineseItems = chineseItems
                )
            }
        }
    }
}

@Composable
private fun OrderedListParagraphView(
    paragraph: BilingualParagraph,
    readingMode: ReadingMode,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        val englishItems = paragraph.listItems
        val chineseItems = paragraph.listItemsChinese

        when (readingMode) {
            ReadingMode.ENGLISH_ONLY -> {
                englishItems.forEachIndexed { index, item ->
                    NumberedRow(number = index + 1, text = item)
                }
            }

            ReadingMode.CHINESE_ONLY -> {
                val itemsToRender = if (chineseItems.isNotEmpty()) chineseItems else englishItems
                itemsToRender.forEachIndexed { index, item ->
                    NumberedRow(number = index + 1, text = item)
                }
            }

            ReadingMode.STACKED -> {
                englishItems.forEachIndexed { index, item ->
                    val chinese = chineseItems.getOrNull(index)
                    StackedNumberedRow(
                        number = index + 1,
                        englishText = item,
                        chineseText = chinese
                    )
                }
            }

            ReadingMode.SIDE_BY_SIDE -> {
                SideBySideList(
                    englishItems = englishItems,
                    chineseItems = chineseItems,
                    numbered = true
                )
            }
        }
    }
}

@Composable
private fun HtmlFallbackParagraphView(
    paragraph: BilingualParagraph,
    modifier: Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Text(
            text = "[暫不支援的內容區塊]",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun BulletRow(text: String) {
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
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun NumberedRow(number: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "$number.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StackedBulletRow(englishText: String, chineseText: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        BulletRow(text = englishText)
        chineseText?.takeIf { it.isNotBlank() }?.let { text ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StackedNumberedRow(number: Int, englishText: String, chineseText: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        NumberedRow(number = number, text = englishText)
        chineseText?.takeIf { it.isNotBlank() }?.let { text ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SideBySideList(
    englishItems: List<String>,
    chineseItems: List<String>,
    numbered: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            englishItems.forEachIndexed { index, item ->
                if (numbered) {
                    NumberedRow(number = index + 1, text = item)
                } else {
                    BulletRow(text = item)
                }
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val itemsToRender = if (chineseItems.isNotEmpty()) chineseItems else englishItems
            itemsToRender.forEachIndexed { index, item ->
                if (numbered) {
                    NumberedRow(number = index + 1, text = item)
                } else {
                    BulletRow(text = item)
                }
            }
        }
    }
}
