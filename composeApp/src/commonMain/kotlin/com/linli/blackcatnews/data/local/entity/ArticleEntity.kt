package com.linli.blackcatnews.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
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
 * @property summary 文章摘要
 * @property content 文章完整內容
 * @property imageUrl 文章封面圖片 URL
 * @property source 新聞來源
 * @property publishTime 發布時間
 * @property section 新聞分類
 * @property url 文章原始連結
 * @property language 文章語言
 * @property translatedTitle 翻譯後的標題
 * @property translatedSummary 翻譯後的摘要
 * @property translatedContent 翻譯後的內容
 * @property glossary 重點單字列表（JSON 字符串）
 * @property grammarPoints 文法要點列表（JSON 字符串）
 * @property quiz 閱讀測驗（JSON 字符串）
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
    val summary: String,
    val content: String,
    val imageUrl: String?,
    val source: String,
    val publishTime: String,
    val section: String,
    val url: String,
    val language: String = "en",

    // 雙語功能
    val translatedTitle: String? = null,
    val translatedSummary: String? = null,
    val translatedContent: String? = null,

    // 學習功能（存儲為 JSON 字符串）
    val glossary: String? = null,
    val grammarPoints: String? = null,
    val quiz: String? = null,

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

    /**
     * 將字符串列表轉換為 JSON 字符串
     */
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    /**
     * 將 JSON 字符串轉換為字符串列表
     */
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { json.decodeFromString(it) }
    }
}