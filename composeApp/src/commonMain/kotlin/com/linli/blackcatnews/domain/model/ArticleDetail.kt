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
    val url: String,
    val publishTime: String,
    val category: NewsCategory,
    // 學習輔助功能
    val glossary: List<GlossaryItem> = emptyList(),
    val grammarPoints: List<GrammarPoint> = emptyList(),
    val sentencePatterns: List<SentencePattern> = emptyList(),
    val phrases: List<PhraseIdiom> = emptyList(),
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
    val type: BilingualParagraphType = BilingualParagraphType.TEXT,
    val english: String? = null,
    val chinese: String? = null,
    val order: Int = 0,
    val headingLevel: Int? = null,
    val listItems: List<String> = emptyList(),
    val listItemsChinese: List<String> = emptyList(),
    val imageUrl: String? = null,
    val imageAlt: String? = null,
    val imageCaption: String? = null,
    val originalHtml: String? = null
)

@Serializable
enum class BilingualParagraphType {
    TEXT,
    HEADING,
    IMAGE,
    UNORDERED_LIST,
    ORDERED_LIST,
    HTML_FALLBACK
}

/**
 * 重點單字條目
 */
@Serializable
data class GlossaryItem(
    val word: String,
    val partOfSpeech: String?,
    val translation: String,
    val pronunciation: String?,
    val definitionEnglish: String,
    val definitionChinese: String?,
    val exampleEnglish: String,
    val exampleChinese: String?,
    val audioUrl: String? = null
)

/**
 * 文法說明
 */
@Serializable
data class GrammarPoint(
    val rule: String,
    val explanationEnglish: String,
    val explanationChinese: String,
    val exampleEnglish: String,
    val exampleChinese: String
)

/**
 * 句子模式
 */
@Serializable
data class SentencePattern(
    val patternEnglish: String,
    val explanationEnglish: String,
    val explanationChinese: String,
    val exampleEnglish: String,
    val exampleChinese: String
)

/**
 * 片語／習語
 */
@Serializable
data class PhraseIdiom(
    val phraseEnglish: String,
    val explanationEnglish: String,
    val explanationChinese: String,
    val exampleEnglish: String,
    val exampleChinese: String
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
    val questionEnglish: String,        // 英文題目
    val questionChinese: String?,       // 中文題目
    val options: List<String>,
    val correctAnswerIndex: Int,
    val correctAnswerKey: String,
    val explanationEnglish: String?,    // 英文解釋
    val explanationChinese: String?     // 中文解釋
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
