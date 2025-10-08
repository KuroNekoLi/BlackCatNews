package com.linli.blackcatnews.domain.model

import kotlinx.serialization.Serializable

/**
 * 文章詳情數據模型
 * 支持雙語學習功能
 */
@Serializable
data class ArticleDetail(
    val id: String,
    val title: BilingualText,
    val summary: BilingualText,
    val content: BilingualContent,
    val imageUrl: String?,
    val source: String,
    val publishTime: String,
    val category: NewsCategory,
    // 學習輔助功能
    val glossary: List<GlossaryItem> = emptyList(),
    val grammarPoints: List<GrammarPoint> = emptyList(),
    val quiz: Quiz? = null
)

/**
 * 雙語文本
 */
@Serializable
data class BilingualText(
    val english: String,
    val chinese: String
)

/**
 * 雙語內容（段落級別對齊）
 */
@Serializable
data class BilingualContent(
    val paragraphs: List<BilingualParagraph>
)

@Serializable
data class BilingualParagraph(
    val english: String,
    val chinese: String,
    val order: Int = 0
)

/**
 * 重點單字條目
 */
@Serializable
data class GlossaryItem(
    val word: String,
    val translation: String,
    val pronunciation: String,
    val example: String,
    val audioUrl: String? = null
)

/**
 * 文法說明
 */
@Serializable
data class GrammarPoint(
    val title: String,
    val explanation: String,
    val examples: List<String>
)

/**
 * 閱讀測驗
 */
@Serializable
data class Quiz(
    val questions: List<QuizQuestion>
)

@Serializable
data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int, // 正確答案的索引
    val explanation: String
)

/**
 * 閱讀模式
 */
enum class ReadingMode {
    ENGLISH_ONLY,      // 僅英文
    CHINESE_ONLY,      // 僅中文
    SIDE_BY_SIDE,      // 左右並排
    STACKED           // 上下堆疊
}
