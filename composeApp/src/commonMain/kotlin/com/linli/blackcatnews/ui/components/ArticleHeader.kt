package com.linli.blackcatnews.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.domain.model.BilingualText
import com.linli.blackcatnews.domain.model.ReadingMode
import com.linli.blackcatnews.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 文章頭部組件
 * 包含標題、元信息和閱讀模式選擇器
 */
@Composable
fun ArticleHeader(
    title: BilingualText,
    source: String,
    url: String,
    publishTime: String,
    imageUrl: String?,
    readingMode: ReadingMode,
    onReadingModeChange: (ReadingMode) -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // 頂部布局：返回按鈕和閱讀模式切換器
        // 模式切換按鈕
        ReadingModeSelector(
            currentMode = readingMode,
            onModeChange = onReadingModeChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 標題（根據模式顯示）
        when (readingMode) {
            ReadingMode.ENGLISH_ONLY -> {
                Text(
                    text = title.english,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            ReadingMode.CHINESE_ONLY -> {
                Text(
                    text = title.chinese,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            else -> {
                Text(
                    text = title.english,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title.chinese,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 元信息
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = source,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = publishTime,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "📊 8 min read",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (url.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            val uriHandler = LocalUriHandler.current
            Text(
                text = "本文改寫自 $source，點此查看原始文章",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    uriHandler.openUri(url)
                }
            )
        }

        HorizontalDivider(
            Modifier.padding(vertical = 16.dp),
            DividerDefaults.Thickness, MaterialTheme.colorScheme.outlineVariant
        )
    }
}


@Preview(showBackground = true, name = "Article Header - English Only")
@Composable
private fun ArticleHeaderEnglishPreview() {
    AppTheme {
        ArticleHeader(
            title = getSampleTitle(),
            source = "BBC News",
            url = "https://www.bbc.com",
            publishTime = "2024年1月15日",
            imageUrl = null,
            readingMode = ReadingMode.ENGLISH_ONLY,
            onReadingModeChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Article Header - Chinese Only")
@Composable
private fun ArticleHeaderChinesePreview() {
    AppTheme {
        ArticleHeader(
            title = getSampleTitle(),
            source = "BBC News",
            url = "https://www.bbc.com",
            publishTime = "2024年1月15日",
            imageUrl = null,
            readingMode = ReadingMode.CHINESE_ONLY,
            onReadingModeChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Article Header - Bilingual Stack")
@Composable
private fun ArticleHeaderBilingualStackPreview() {
    AppTheme {
        ArticleHeader(
            title = getSampleTitle(),
            source = "CNN",
            url = "https://www.cnn.com",
            publishTime = "2024年1月15日",
            imageUrl = null,
            readingMode = ReadingMode.STACKED,
            onReadingModeChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Article Header - Side by Side")
@Composable
private fun ArticleHeaderSideBySidePreview() {
    AppTheme {
        ArticleHeader(
            title = getSampleTitle(),
            source = "Reuters",
            url = "https://www.reuters.com",
            publishTime = "2024年1月15日",
            imageUrl = null,
            readingMode = ReadingMode.SIDE_BY_SIDE,
            onReadingModeChange = {}
        )
    }
}

private fun getSampleTitle(): BilingualText = BilingualText(
    english = "Revolutionary AI Technology Transforms Healthcare Industry",
    chinese = "革命性AI技術改變醫療保健行業"
)