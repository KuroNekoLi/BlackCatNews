package com.linli.blackcatnews.domain.model

import kotlinx.serialization.Serializable

/**
 * 文章分類
 *
 * 使用 sealed interface 提供類型安全的分類定義
 * 避免使用字符串造成的拼寫錯誤和類型不安全問題
 *
 * ## 支持的分類
 * - [News] - 最新新聞
 * - [World] - 世界新聞
 * - [Technology] - 科技新聞
 *
 * ## 使用範例
 * ```kotlin
 * val section: ArticleSection = ArticleSection.News
 * val apiValue = section.value  // "news"
 * val displayName = section.displayName  // "最新"
 * ```
 */
@Serializable
sealed interface ArticleSection {
    /**
     * API 使用的值（英文小寫）
     */
    val value: String

    /**
     * 顯示用的名稱（繁體中文）
     */
    val displayName: String

    /**
     * 圖標 emoji
     */
    val icon: String

    /**
     * 最新新聞
     */
    @Serializable
    data object News : ArticleSection {
        override val value: String = "news"
        override val displayName: String = "最新"
        override val icon: String = "📰"
    }

    /**
     * 世界新聞
     */
    @Serializable
    data object World : ArticleSection {
        override val value: String = "world"
        override val displayName: String = "世界"
        override val icon: String = "🌍"
    }

    /**
     * 科技新聞
     */
    @Serializable
    data object Technology : ArticleSection {
        override val value: String = "technology"
        override val displayName: String = "科技"
        override val icon: String = "💻"
    }

    companion object {
        /**
         * 所有可用的分類列表
         */
        val all: List<ArticleSection> = listOf(News, World, Technology)

        /**
         * 從字符串值轉換為 ArticleSection
         *
         * @param value API 返回的字符串值
         * @return ArticleSection 對應的分類，如果無效則返回 null
         *
         * ## 使用範例
         * ```kotlin
         * val section = ArticleSection.fromValue("news")  // ArticleSection.News
         * val invalid = ArticleSection.fromValue("invalid")  // null
         * ```
         */
        fun fromValue(value: String): ArticleSection? = when (value.lowercase()) {
            "news" -> News
            "world" -> World
            "technology" -> Technology
            else -> null
        }

        /**
         * 從字符串值轉換為 ArticleSection，提供默認值
         *
         * @param value API 返回的字符串值
         * @param default 無效時的默認值，默認為 News
         * @return ArticleSection 對應的分類
         */
        fun fromValueOrDefault(
            value: String,
            default: ArticleSection = News
        ): ArticleSection = fromValue(value) ?: default

        /**
         * 驗證字符串是否為有效的分類值
         *
         * @param value 要驗證的字符串
         * @return 是否為有效分類
         */
        fun isValid(value: String): Boolean = fromValue(value) != null

        /**
         * 獲取所有分類的值列表
         *
         * @return 分類值的列表 ["news", "world", "technology"]
         */
        fun values(): List<String> = all.map { it.value }
    }
}

/**
 * ArticleSection 擴展函數：轉換為 API 參數
 */
fun ArticleSection.toApiParam(): String = value

/**
 * ArticleSection 擴展函數：轉換為顯示文本（帶圖標）
 */
fun ArticleSection.toDisplayText(): String = "$icon $displayName"