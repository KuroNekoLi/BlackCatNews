package com.linli.blackcatnews.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * AI 文章 API 響應 DTO
 *
 * 對應 API: GET /api/ai-articles/random
 * 用於接收從後端返回的文章數據
 *
 * @property id 文章唯一識別碼
 * @property title 文章標題
 * @property summary 文章摘要
 * @property content 文章完整內容
 * @property imageUrl 文章封面圖片 URL
 * @property source 新聞來源（例如: BBC, CNN）
 * @property publishTime 發布時間
 * @property section 新聞分類（news, world, technology）
 * @property url 文章原始連結
 * @property language 文章語言（預設為英文）
 * @property translatedTitle 翻譯後的標題（繁體中文）
 * @property translatedSummary 翻譯後的摘要（繁體中文）
 * @property translatedContent 翻譯後的內容（繁體中文）
 * @property glossary 重點單字列表
 * @property grammarPoints 文法要點列表
 * @property quiz 閱讀測驗
 *
 * @see com.linli.blackcatnews.domain.model.Article
 */
@Serializable
data class AiArticleDto(
    @SerialName("id")
    val id: String,

    @SerialName("title")
    val title: String,

    @SerialName("summary")
    val summary: String,

    @SerialName("content")
    val content: String,

    @SerialName("imageUrl")
    val imageUrl: String? = null,

    @SerialName("source")
    val source: String,

    @SerialName("publishTime")
    val publishTime: String,

    @SerialName("section")
    val section: String,

    @SerialName("url")
    val url: String,

    @SerialName("language")
    val language: String = "en",

    // 雙語功能
    @SerialName("translatedTitle")
    val translatedTitle: String? = null,

    @SerialName("translatedSummary")
    val translatedSummary: String? = null,

    @SerialName("translatedContent")
    val translatedContent: String? = null,

    // 學習功能
    @SerialName("glossary")
    val glossary: List<GlossaryItemDto>? = null,

    @SerialName("grammarPoints")
    val grammarPoints: List<GrammarPointDto>? = null,

    @SerialName("quiz")
    val quiz: QuizDto? = null
)

/**
 * 重點單字 DTO
 *
 * @property word 單字
 * @property translation 翻譯
 * @property pronunciation 發音（音標）
 * @property example 例句
 * @property audioUrl 發音音頻 URL
 */
@Serializable
data class GlossaryItemDto(
    @SerialName("word")
    val word: String,

    @SerialName("translation")
    val translation: String,

    @SerialName("pronunciation")
    val pronunciation: String,

    @SerialName("example")
    val example: String,

    @SerialName("audioUrl")
    val audioUrl: String? = null
)

/**
 * 文法要點 DTO
 *
 * @property title 文法標題
 * @property explanation 文法解釋
 * @property examples 例句列表
 */
@Serializable
data class GrammarPointDto(
    @SerialName("title")
    val title: String,

    @SerialName("explanation")
    val explanation: String,

    @SerialName("examples")
    val examples: List<String>
)

/**
 * 閱讀測驗 DTO
 *
 * @property questions 問題列表
 */
@Serializable
data class QuizDto(
    @SerialName("questions")
    val questions: List<QuizQuestionDto>
)

/**
 * 測驗問題 DTO
 *
 * @property id 問題 ID
 * @property question 問題文本
 * @property options 選項列表
 * @property correctAnswer 正確答案的索引
 * @property explanation 解釋
 */
@Serializable
data class QuizQuestionDto(
    @SerialName("id")
    val id: String,

    @SerialName("question")
    val question: String,

    @SerialName("options")
    val options: List<String>,

    @SerialName("correctAnswer")
    val correctAnswer: Int,

    @SerialName("explanation")
    val explanation: String
)