package com.linli.dictionary.di

import androidx.room.RoomDatabase
import com.linli.dictionary.data.local.database.DictionaryDatabase
import com.linli.dictionary.data.local.database.getDatabaseBuilder
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Android 平台特定的字典模組配置
 */
val androidDictionaryModule = module {
    // 提供 Room 數據庫建構器，用於創建完整初始化的數據庫實例
    single<RoomDatabase.Builder<DictionaryDatabase>>(named("dictionaryDatabaseBuilder")) {
        getDatabaseBuilder()
    }
}