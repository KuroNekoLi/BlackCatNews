package com.linli.blackcatnews.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.model.BilingualParagraph
import com.linli.blackcatnews.model.ReadingMode

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
    when (readingMode) {
        ReadingMode.ENGLISH_ONLY -> {
            SingleLanguageText(
                text = paragraph.english,
                language = "English",
                modifier = modifier
            )
        }

        ReadingMode.CHINESE_ONLY -> {
            SingleLanguageText(
                text = paragraph.chinese,
                language = "Chinese",
                modifier = modifier
            )
        }

        ReadingMode.SIDE_BY_SIDE -> {
            SideBySideText(
                englishText = paragraph.english,
                chineseText = paragraph.chinese,
                modifier = modifier
            )
        }

        ReadingMode.STACKED -> {
            StackedText(
                englishText = paragraph.english,
                chineseText = paragraph.chinese,
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
            )
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
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // 中文列
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            )
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
                    style = MaterialTheme.typography.bodyMedium
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
