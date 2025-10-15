package com.linli.blackcatnews.navigation

import kotlinx.serialization.Serializable

/**
 * Type-Safe Navigation 路由定義
 * 使用 @Serializable 標記實現類型安全的導航
 */

// 底部導航的主要路由
@Serializable
object HomeRoute

@Serializable
object CategoriesRoute

@Serializable
object FavoritesRoute

@Serializable
object SettingsRoute

// 搜尋和通知路由
@Serializable
object SearchRoute

@Serializable
object NotificationsRoute

// 詳情頁路由
@Serializable
data class ArticleDetailRoute(
    val articleId: String,
    val title: String = ""
)

// 雙語學習文章詳情頁路由
@Serializable
data class BilingualArticleDetailRoute(
    val articleId: String,
    val fromLang: String = "en", // 原文語言
    val toLang: String = "zh",   // 目標語言
    val title: String = ""
)

// 搜尋結果路由
@Serializable
data class SearchResultRoute(
    val query: String
)
