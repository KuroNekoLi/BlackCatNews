package com.linli.blackcatnews.di

import androidx.room.RoomDatabase
import com.linli.blackcatnews.data.local.database.NewsDatabase
import com.linli.blackcatnews.data.local.database.getDatabaseBuilder
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module

val iosPlatformModule = module {
    single<RoomDatabase.Builder<NewsDatabase>> {
        getDatabaseBuilder()
    }
}

fun initKoin(vararg extraModules: Module) {
    stopKoin()
    startKoin {
        modules(listOf(databaseModule, iosPlatformModule) + extraModules)
    }
}
