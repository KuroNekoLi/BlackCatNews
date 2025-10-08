package com.linli.blackcatnews.data.local.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

/**
 * 建立 Room 資料庫實例
 *
 * 平台端負責提供 [RoomDatabase.Builder]。
 * 此函數統一套用跨平台設定（BundledSQLiteDriver、預設 Dispatchers.IO）並建立資料庫實例。
 *
 * @param builder 由平台層提供的 Room 資料庫建構器
 * @return 建立好的 [NewsDatabase] 實例
 */
fun buildDatabase(builder: RoomDatabase.Builder<NewsDatabase>): NewsDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .build()
}