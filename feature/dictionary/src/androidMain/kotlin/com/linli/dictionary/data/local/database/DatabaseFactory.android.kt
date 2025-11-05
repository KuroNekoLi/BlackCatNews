package com.linli.dictionary.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.context.GlobalContext

/**
 * 安卓平台上的數據庫建構器
 */
actual fun getDatabaseBuilder(): RoomDatabase.Builder<DictionaryDatabase> {
    // 使用 Application Context 來創建數據庫建構器
    val context = GlobalContext.get().get<Context>()
    val appContext = context.applicationContext
    val databaseFile = appContext.getDatabasePath(DictionaryDatabase.DATABASE_NAME)
    return Room.databaseBuilder(
        appContext,
        DictionaryDatabase::class.java,
        databaseFile.absolutePath
    )
}

