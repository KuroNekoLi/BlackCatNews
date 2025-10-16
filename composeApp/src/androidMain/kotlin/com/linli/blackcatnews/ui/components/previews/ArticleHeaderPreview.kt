package com.linli.blackcatnews.ui.components.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.linli.blackcatnews.domain.model.BilingualText
import com.linli.blackcatnews.domain.model.ReadingMode
import com.linli.blackcatnews.ui.components.ArticleHeader
import com.linli.blackcatnews.ui.theme.AppTheme

@Preview(showBackground = true, name = "Article Header - English Only")
@Composable
private fun ArticleHeaderEnglishPreview() {
    AppTheme {
        ArticleHeader(
            title = getSampleTitle(),
            source = "BBC News",
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