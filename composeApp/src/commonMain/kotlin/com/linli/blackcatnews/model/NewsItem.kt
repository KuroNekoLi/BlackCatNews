package com.linli.blackcatnews.model

/**
 * 新聞條目數據模型
 * 用於新聞列表展示
 */
data class NewsItem(
    val id: String,
    val title: String,
    val summary: String,
    val imageUrl: String?,
    val source: String,
    val publishTime: String,
    val category: NewsCategory = NewsCategory.LATEST
)

/**
 * 新聞分類
 */
enum class NewsCategory(val displayName: String) {
    LATEST("最新"),
    WORLD("世界新聞"),
    TECH("科技"),
    BUSINESS("商業"),
    SPORTS("體育"),
    ENTERTAINMENT("娛樂"),
    HEALTH("健康"),
    SCIENCE("科學")
}
