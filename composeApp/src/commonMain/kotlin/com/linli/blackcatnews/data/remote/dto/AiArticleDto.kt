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
    val sourceName: String? = null,

    @SerialName("section")
    val section: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("titleFrom")
    val titleFrom: String? = null,

    @SerialName("titleTo")
    val titleTo: String? = null,

    @SerialName("title_zh")
    val titleZh: String? = null,

    @SerialName("summaryFrom")
    val summaryFrom: String? = null,

    @SerialName("summaryTo")
    val summaryTo: String? = null,

    @SerialName("summary_en")
    val summaryEnglishLegacy: String? = null,

    @SerialName("summary_zh")
    val summaryChineseLegacy: String? = null,

    @SerialName("publishedAt")
    val publishedAt: String? = null,

    @SerialName("originalUrl")
    val originalUrl: String,

    @SerialName("contentHtml")
    val contentHtml: String? = null,

    @SerialName("requested_difficulty")
    val requestedDifficulty: String? = null,

    @SerialName("from_language")
    val fromLanguage: String,

    @SerialName("to_language")
    val toLanguage: String,

    @SerialName("content")
    val content: DifficultyContentDto? = null,

    @SerialName("available_difficulties")
    val availableDifficulties: List<String> = emptyList(),

    @SerialName("available_views")
    val availableViews: List<AiViewAvailabilityDto> = emptyList()
)

@Serializable
data class AiViewAvailabilityDto(
    @SerialName("from_language")
    val fromLanguage: String,

    @SerialName("to_language")
    val toLanguage: String,

    @SerialName("difficulty")
    val difficulty: String
)

@Serializable
data class DifficultyContentDto(
    @SerialName("difficulty")
    val difficulty: String,

    @SerialName("from_language")
    val fromLanguage: String,

    @SerialName("to_language")
    val toLanguage: String,

    @SerialName("optimized_html_from")
    val optimizedHtmlFrom: String? = null,

    @SerialName("optimized_html_to")
    val optimizedHtmlTo: String? = null,

    @SerialName("cleaned_text_from")
    val cleanedTextFrom: String? = null,

    @SerialName("cleaned_text_to")
    val cleanedTextTo: String? = null,

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

    @SerialName("word_from")
    val wordFrom: String,

    @SerialName("word_to")
    val wordTo: String? = null,

    @SerialName("definition_from")
    val definitionFrom: String,

    @SerialName("definition_to")
    val definitionTo: String? = null,

    @SerialName("example_from")
    val exampleFrom: String,

    @SerialName("example_to")
    val exampleTo: String? = null
)

/**
 * 文法說明 DTO
 */
@Serializable
data class GrammarItemDto(
    @SerialName("rule_from")
    val ruleFrom: String,

    @SerialName("rule_to")
    val ruleTo: String? = null,

    @SerialName("explanation_from")
    val explanationFrom: String,

    @SerialName("explanation_to")
    val explanationTo: String,

    @SerialName("example_from")
    val exampleFrom: String,

    @SerialName("example_to")
    val exampleTo: String
)

/**
 * 句型說明 DTO
 */
@Serializable
data class SentencePatternDto(
    @SerialName("pattern_from")
    val patternFrom: String,

    @SerialName("pattern_to")
    val patternTo: String? = null,

    @SerialName("explanation_from")
    val explanationFrom: String,

    @SerialName("explanation_to")
    val explanationTo: String,

    @SerialName("example_from")
    val exampleFrom: String,

    @SerialName("example_to")
    val exampleTo: String
)

/**
 * 片語／習語 DTO
 */
@Serializable
data class PhraseIdiomDto(
    @SerialName("phrase_from")
    val phraseFrom: String,

    @SerialName("phrase_to")
    val phraseTo: String? = null,

    @SerialName("explanation_from")
    val explanationFrom: String,

    @SerialName("explanation_to")
    val explanationTo: String,

    @SerialName("example_from")
    val exampleFrom: String,

    @SerialName("example_to")
    val exampleTo: String
)

/**
 * 閱讀測驗題目 DTO
 */
@Serializable
data class ComprehensionQuestionDto(
    @SerialName("question_from")
    val questionFrom: String,

    @SerialName("question_to")
    val questionTo: String? = null,

    @SerialName("options")
    val options: List<McqOptionDto>,

    @SerialName("answer")
    val answerKey: String? = null,

    @SerialName("explanation_from")
    val explanationFrom: String? = null,

    @SerialName("explanation_to")
    val explanationTo: String? = null
)

@Serializable
data class McqOptionDto(
    @SerialName("label")
    val label: String,

    @SerialName("text_from")
    val textFrom: String? = null,

    @SerialName("text_to")
    val textTo: String? = null
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
    val section: String? = null,

    @SerialName("difficulty")
    val difficulty: String? = null,

    @SerialName("from_language")
    val fromLanguage: String? = null,

    @SerialName("to_language")
    val toLanguage: String? = null
)