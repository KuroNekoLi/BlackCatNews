package com.linli.blackcatnews.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.linli.blackcatnews.data.local.dao.ArticleDao
import com.linli.blackcatnews.data.local.entity.ArticleEntity
import com.linli.blackcatnews.data.local.entity.Converters

/**
 * 黑貓新聞數據庫
 *
 * Room 數據庫抽象類，定義數據庫配置和 DAO 訪問
 *
 * ## 數據庫配置
 * - 版本: 1
 * - Entity: ArticleEntity
 * - DAO: ArticleDao
 * - @ConstructedBy: KMP 非 Android 平台必需
 *
 * ## 使用範例
 * ```kotlin
 * val database = getRoomDatabase(context)
 * val articleDao = database.articleDao()
 * ```
 *
 * @see ArticleEntity
 * @see ArticleDao
 */
@Database(
    entities = [ArticleEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
@ConstructedBy(NewsDatabaseConstructor::class)
abstract class NewsDatabase : RoomDatabase() {

    /**
     * 獲取文章 DAO
     *
     * @return ArticleDao 文章數據訪問對象
     */
    abstract fun articleDao(): ArticleDao

    companion object {
        /**
         * 數據庫名稱
         */
        const val DATABASE_NAME = "black_cat_news.db"
    }
}

/**
 * NewsDatabase 構造器（KMP 必需）
 *
 * Room KSP 會自動生成此類的實現
 */
@Suppress("KotlinNoActualForExpect")
expect object NewsDatabaseConstructor : RoomDatabaseConstructor<NewsDatabase> {
    override fun initialize(): NewsDatabase
}