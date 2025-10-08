package com.linli.blackcatnews.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * 建立 Android 平台的 RoomDatabase.Builder
 *
 * @param context Application Context（建議傳入 applicationContext）
 * @return [RoomDatabase.Builder] 用於後續跨平台共用設定
 */
fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<NewsDatabase> {
    val appContext = context.applicationContext
    val databaseFile = appContext.getDatabasePath(NewsDatabase.DATABASE_NAME)
    return Room.databaseBuilder(
        appContext,
        NewsDatabase::class.java,
        databaseFile.absolutePath
    )
}