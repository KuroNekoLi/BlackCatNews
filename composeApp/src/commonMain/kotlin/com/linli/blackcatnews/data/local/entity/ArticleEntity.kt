package com.linli.blackcatnews.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 文章實體
 *
 * Room 數據庫表結構，用於本地存儲文章數據
 * 遵循 SSOT（Single Source of Truth）原則
 *
 * @property id 文章唯一標識符（主鍵）
 * @property title 文章標題
 * @property titleZh 中文翻譯標題
 * @property summary 文章摘要（英文）
 * @property summaryZh 文章摘要（中文，可能為空）
 * @property contentHtml 原始或優化後的 HTML 內容
 * @property cleanedText 英文淨化內容
 * @property cleanedTextZh 中文翻譯內容（可能為空）
 * @property imageUrl 文章封面圖片 URL
 * @property sourceName 新聞來源
 * @property publishTime 發布時間
 * @property section 新聞分類
 * @property url 文章原始連結
 * @property language 文章語言
 * @property learningVocabulary 學習詞彙列表
 * @property learningGrammar 學習文法列表
 * @property learningSentencePatterns 學習句型列表
 * @property learningPhrases 學習片語／習語列表
 * @property learningQuiz 閱讀測驗題目列表
 * @property isFavorite 是否收藏
 * @property createdAt 創建時間戳
 * @property updatedAt 更新時間戳
 */
@Entity(tableName = "articles")
@TypeConverters(Converters::class)
data class ArticleEntity(
    @PrimaryKey
    val id: String,

    val title: String,
    val titleZh: String? = null,
    val summary: String,
    val summaryZh: String? = null,
    val contentHtml: String,
    val cleanedText: String,
    val cleanedTextZh: String? = null,
    val imageUrl: String? = null,
    val sourceName: String,
    val publishTime: String,
    val section: String,
    val url: String,
    val language: String = "en",

    // 學習功能（存儲為 JSON 字符串）
    val learningVocabulary: List<VocabularyItemEntity> = emptyList(),
    val learningGrammar: List<GrammarItemEntity> = emptyList(),
    val learningSentencePatterns: List<SentencePatternEntity> = emptyList(),
    val learningPhrases: List<PhraseIdiomEntity> = emptyList(),
    val learningQuiz: List<ComprehensionQuestionEntity> = emptyList(),

    // 本地狀態
    val isFavorite: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Room 類型轉換器
 *
 * 用於將複雜類型（List, Object）轉換為 Room 支持的基本類型
 */
class Converters {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @TypeConverter
    fun fromVocabularyList(value: List<VocabularyItemEntity>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toVocabularyList(value: String?): List<VocabularyItemEntity> {
        return value?.let { json.decodeFromString(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromGrammarList(value: List<GrammarItemEntity>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toGrammarList(value: String?): List<GrammarItemEntity> {
        return value?.let { json.decodeFromString(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromSentencePatternList(value: List<SentencePatternEntity>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toSentencePatternList(value: String?): List<SentencePatternEntity> {
        return value?.let { json.decodeFromString(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromPhraseList(value: List<PhraseIdiomEntity>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toPhraseList(value: String?): List<PhraseIdiomEntity> {
        return value?.let { json.decodeFromString(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromQuizList(value: List<ComprehensionQuestionEntity>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toQuizList(value: String?): List<ComprehensionQuestionEntity> {
        return value?.let { json.decodeFromString(it) } ?: emptyList()
    }
}

@Serializable
data class VocabularyItemEntity(
    val partOfSpeech: String? = null,
    val wordEnglish: String,
    val wordChinese: String? = null,
    val definitionEnglish: String,
    val definitionChinese: String? = null,
    val exampleEnglish: String,
    val exampleChinese: String? = null,
    val pronunciation: String? = null
)

@Serializable
data class GrammarItemEntity(
    val ruleEnglish: String,
    val explanationEnglish: String,
    val explanationChinese: String,
    val exampleEnglish: String,
    val exampleChinese: String
)

@Serializable
data class SentencePatternEntity(
    val patternEnglish: String,
    val explanationEnglish: String,
    val explanationChinese: String,
    val exampleEnglish: String,
    val exampleChinese: String
)

@Serializable
data class PhraseIdiomEntity(
    val phraseEnglish: String,
    val explanationEnglish: String,
    val explanationChinese: String,
    val exampleEnglish: String,
    val exampleChinese: String
)

@Serializable
data class ComprehensionQuestionEntity(
    val id: String,
    val questionEnglish: String,
    val questionChinese: String? = null,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val correctAnswerKey: String,
    val explanationEnglish: String? = null,
    val explanationChinese: String? = null
)