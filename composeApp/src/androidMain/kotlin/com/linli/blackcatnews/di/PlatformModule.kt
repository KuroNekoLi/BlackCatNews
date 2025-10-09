package com.linli.blackcatnews.di

import android.content.Context
import androidx.room.RoomDatabase
import com.linli.blackcatnews.data.local.database.NewsDatabase
import com.linli.blackcatnews.data.local.database.getDatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidPlatformModule = module {
    single<RoomDatabase.Builder<NewsDatabase>> {
        val context: Context = androidContext()
        getDatabaseBuilder(context)
    }
    single { androidContext().resources }
}
