package com.linli.blackcatnews.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * AI 文章 API 響應 DTO
 *
 * 對應 API: GET /api/ai-articles/random
 * 用於接收從後端返回的文章數據
 */
@Serializable
data class AiArticleDto(
    @SerialName("id")
    val id: Long,

    @SerialName("sourceName")
    val sourceName: String,

    @SerialName("section")
    val section: String,

    @SerialName("title")
    val title: String,

    @SerialName("title_zh")
    val titleZh: String? = null,

    @SerialName("summary_en")
    val summaryEnglish: String? = null,

    @SerialName("summary_zh")
    val summaryChinese: String? = null,

    @SerialName("publishedAt")
    val publishedAt: String,

    @SerialName("originalUrl")
    val originalUrl: String,

    @SerialName("contentHtml")
    val contentHtml: String,

    @SerialName("cleaned_text")
    val cleanedTextEnglish: String,

    @SerialName("cleaned_text_zh")
    val cleanedTextChinese: String? = null,

    @SerialName("optimized_html")
    val optimizedHtml: String? = null,

    @SerialName("optimized_zh_html")
    val optimizedZhHtml: String? = null,

    @SerialName("language")
    val language: String = "en",

    @SerialName("explanation")
    val explanation: ExplanationDto? = null
)

/**
 * 學習輔助資料 DTO
 */
@Serializable
data class ExplanationDto(
    @SerialName("vocabulary")
    val vocabulary: List<VocabularyItemDto>? = null,

    @SerialName("grammar")
    val grammar: List<GrammarItemDto>? = null,

    @SerialName("sentence_patterns")
    val sentencePatterns: List<SentencePatternDto>? = null,

    @SerialName("phrases_idioms")
    val phrasesIdioms: List<PhraseIdiomDto>? = null,

    @SerialName("comprehension_mcq")
    val comprehensionMcq: List<ComprehensionQuestionDto>? = null
)

/**
 * 重點單字 DTO
 */
@Serializable
data class VocabularyItemDto(
    @SerialName("pos")
    val partOfSpeech: String? = null,

    @SerialName("word_en")
    val wordEnglish: String,

    @SerialName("word_zh")
    val wordChinese: String? = null,

    @SerialName("definition_en")
    val definitionEnglish: String,

    @SerialName("definition_zh")
    val definitionChinese: String? = null,

    @SerialName("example_en")
    val exampleEnglish: String,

    @SerialName("example_zh")
    val exampleChinese: String? = null
)

/**
 * 文法說明 DTO
 */
@Serializable
data class GrammarItemDto(
    @SerialName("rule_en")
    val ruleEnglish: String,

    @SerialName("rule_zh")
    val ruleChinese: String? = null,

    @SerialName("explanation_en")
    val explanationEnglish: String,

    @SerialName("explanation_zh")
    val explanationChinese: String,

    @SerialName("example_en")
    val exampleEnglish: String,

    @SerialName("example_zh")
    val exampleChinese: String
)

/**
 * 句型說明 DTO
 */
@Serializable
data class SentencePatternDto(
    @SerialName("pattern_en")
    val patternEnglish: String,

    @SerialName("pattern_zh")
    val patternChinese: String? = null,

    @SerialName("explanation_en")
    val explanationEnglish: String,

    @SerialName("explanation_zh")
    val explanationChinese: String,

    @SerialName("example_en")
    val exampleEnglish: String,

    @SerialName("example_zh")
    val exampleChinese: String
)

/**
 * 片語／習語 DTO
 */
@Serializable
data class PhraseIdiomDto(
    @SerialName("phrase_en")
    val phraseEnglish: String,

    @SerialName("phrase_zh")
    val phraseChinese: String? = null,

    @SerialName("explanation_en")
    val explanationEnglish: String,

    @SerialName("explanation_zh")
    val explanationChinese: String,

    @SerialName("example_en")
    val exampleEnglish: String,

    @SerialName("example_zh")
    val exampleChinese: String
)

/**
 * 閱讀測驗題目 DTO
 */
@Serializable
data class ComprehensionQuestionDto(
    @SerialName("question_en")
    val questionEnglish: String,

    @SerialName("question_zh")
    val questionChinese: String? = null,

    @SerialName("options")
    val options: Map<String, String>,

    @SerialName("answer")
    val answerKey: String? = null,

    @SerialName("explanation_en")
    val explanationEnglish: String? = null,

    @SerialName("explanation_zh")
    val explanationChinese: String? = null
)

/**
 * AI 文章列表 API 外層響應
 */
@Serializable
data class AiArticlesResponseDto(
    @SerialName("message")
    val message: String? = null,

    @SerialName("requestedCount")
    val requestedCount: Int? = null,

    @SerialName("actualCount")
    val actualCount: Int? = null,

    @SerialName("totalAvailable")
    val totalAvailable: Int? = null,

    @SerialName("filters")
    val filters: FiltersDto? = null,

    @SerialName("articles")
    val articles: List<AiArticleDto> = emptyList()
)

/**
 * AI 文章列表 API 擴充過濾條件
 */
@Serializable
data class FiltersDto(
    @SerialName("source")
    val source: String? = null,

    @SerialName("section")
    val section: String? = null
)