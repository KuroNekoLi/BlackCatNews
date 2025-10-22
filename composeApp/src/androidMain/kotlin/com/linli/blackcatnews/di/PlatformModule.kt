package com.linli.blackcatnews.di

import android.content.Context
import androidx.room.RoomDatabase
import com.linli.authentication.platformAuthProvidersModule
import com.linli.authentication.presentation.platformLoginModule
import com.linli.blackcatnews.data.local.database.NewsDatabase
import com.linli.blackcatnews.data.local.database.getDatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

val androidPlatformModule = module {
    includes(
        platformAuthProvidersModule,
        platformLoginModule
    )
    single<RoomDatabase.Builder<NewsDatabase>> {
        val context: Context = androidContext()
        getDatabaseBuilder(context)
    }
    single { androidContext().resources }
}

fun initKoin() {
    stopKoin()
    startKoin {
        modules(listOf(appModule, androidPlatformModule))
    }
}
