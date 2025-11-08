package com.linli.dictionary.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.linli.dictionary.data.local.dao.DictionaryDao
import com.linli.dictionary.data.local.entity.WordConverters
import com.linli.dictionary.data.local.entity.WordEntity

/**
 * 字典數據庫
 *
 * Room 數據庫抽象類，定義數據庫結構與 DAO 存取方式
 *
 * ## 數據庫設定
 * - 版本：1
 * - 實體：WordEntity
 * - DAO：DictionaryDao
 * - @ConstructedBy：KMP 非 Android 平台必需
 *
 * ## 使用範例
 * ```kotlin
 * val database = buildDatabase()
 * val dictionaryDao = database.dictionaryDao()
 * ```
 *
 * @see WordEntity
 * @see DictionaryDao
 */
@Database(
    entities = [WordEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(WordConverters::class)
@ConstructedBy(DictionaryDatabaseConstructor::class)
abstract class DictionaryDatabase : RoomDatabase() {

    /**
     * 取得字典 DAO
     *
     * @return DictionaryDao 字典資料存取元件
     */
    abstract fun dictionaryDao(): DictionaryDao

    companion object {
        /**
         * 數據庫名稱
         */
        const val DATABASE_NAME = "dictionary.db"
    }
}

/**
 * DictionaryDatabase 建構器（KMP 必需）
 *
 * Room KSP 會自動生成此類的實現
 */
@Suppress("KotlinNoActualForExpect")
expect object DictionaryDatabaseConstructor : RoomDatabaseConstructor<DictionaryDatabase> {
    override fun initialize(): DictionaryDatabase
}