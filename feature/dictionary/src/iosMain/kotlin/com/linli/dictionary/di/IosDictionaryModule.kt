package com.linli.dictionary.di

import androidx.room.RoomDatabase
import com.linli.dictionary.data.local.database.DictionaryDatabase
import com.linli.dictionary.data.local.database.getDatabaseBuilder
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * iOS 專用字典模組。
 */
val iosDictionaryModule = module {
    // 字典數據庫建構器
    single<RoomDatabase.Builder<DictionaryDatabase>>(named("dictionaryDatabaseBuilder")) {
        getDatabaseBuilder()
    }
}