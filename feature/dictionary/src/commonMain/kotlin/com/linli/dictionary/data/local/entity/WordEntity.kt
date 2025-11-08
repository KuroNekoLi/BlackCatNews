package com.linli.dictionary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 表示儲存在本地數據庫中的字典單字實體。
 * 使用 PrimaryKey 標註 word 字段以確保唯一性。
 * 使用 TypeConverters 處理複雜的嵌套數據結構。
 *
 * @property word 單字本身，作為主鍵
 * @property ukPronunciation 英式發音
 * @property usPronunciation 美式發音
 * @property entriesJson 詞性與定義列表的 JSON 字符串
 * @property isInWordBank 表示該單字是否加入用戶的單字庫
 * @property timestamp 上次更新時間的時間戳
 */
@Serializable
@Entity(tableName = "words")
@TypeConverters(WordConverters::class)
data class WordEntity @OptIn(ExperimentalTime::class) constructor(
    @PrimaryKey
    val word: String,
    val ukPronunciation: String,
    val usPronunciation: String,
    val entriesJson: String,
    val isInWordBank: Boolean = false,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)

/**
 * 單字詞典中的詞性與定義實體，用於 JSON 序列化/反序列化
 *
 * @property partOfSpeech 詞性，如 "n.", "v.", "adj."
 * @property definitions 該詞性下的定義列表
 */
@Serializable
data class EntryEntity(
    val partOfSpeech: String,
    val definitions: List<DefinitionEntity>
)

/**
 * 詞彙定義實體，包含英文定義、中文翻譯和例句
 *
 * @property enDefinition 英文定義
 * @property zhDefinition 中文翻譯定義
 * @property examples 相關例句列表
 */
@Serializable
data class DefinitionEntity(
    val enDefinition: String,
    val zhDefinition: String,
    val examples: List<String>
)

/**
 * 提供實體類與 JSON 之間的類型轉換
 */
class WordConverters {
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * 將 EntryEntity 列表轉換為 JSON 字符串
     *
     * @param entries 要轉換的詞條列表
     * @return JSON 字符串
     */
    @TypeConverter
    fun fromEntriesToString(entries: List<EntryEntity>): String {
        return json.encodeToString<List<EntryEntity>>(entries)
    }

    /**
     * 將 JSON 字符串轉換為 EntryEntity 列表
     *
     * @param entriesJson 包含詞條的 JSON 字符串
     * @return 詞條實體列表
     */
    @TypeConverter
    fun fromStringToEntries(entriesJson: String): List<EntryEntity> {
        return json.decodeFromString<List<EntryEntity>>(entriesJson)
    }
}