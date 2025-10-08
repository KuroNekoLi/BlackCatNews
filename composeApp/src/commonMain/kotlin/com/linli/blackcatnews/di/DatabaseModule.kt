package com.linli.blackcatnews.di

import androidx.room.RoomDatabase
import com.linli.blackcatnews.data.local.database.NewsDatabase
import com.linli.blackcatnews.data.local.database.buildDatabase
import org.koin.dsl.module

/**
 * 共用資料庫 DI 模組
 *
 * 平台層須額外提供 [RoomDatabase.Builder]，本模組負責統一建置 [NewsDatabase]
 * 並暴露 DAO 供其他模組使用。
 */
val databaseModule = module {
    single<NewsDatabase> {
        val builder: RoomDatabase.Builder<NewsDatabase> = get()
        buildDatabase(builder)
    }

    single {
        get<NewsDatabase>().articleDao()
    }
}
