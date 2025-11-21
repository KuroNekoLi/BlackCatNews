package com.linli.blackcatnews.di

import android.app.Application
import android.content.Context
import androidx.room.RoomDatabase
import com.linli.authentication.platformAuthProvidersModule
import com.linli.authentication.presentation.platformLoginModule
import com.linli.blackcatnews.data.local.database.NewsDatabase
import com.linli.blackcatnews.data.local.database.getDatabaseBuilder
import com.linli.blackcatnews.rating.AndroidPlayStoreRatingRequester
import com.linli.blackcatnews.rating.CurrentActivityProvider
import com.linli.blackcatnews.rating.LifecycleAwareActivityProvider
import com.linli.blackcatnews.rating.RatingRequester
import com.google.android.play.core.review.ReviewManagerFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

val androidPlatformModule = module {
    includes(
        platformAuthProvidersModule,
        platformLoginModule
    )
    single<RoomDatabase.Builder<NewsDatabase>>(named("newsDatabaseBuilder")) {
        val context: Context = androidContext()
        getDatabaseBuilder(context)
    }
    single { androidContext().resources }
    single<CurrentActivityProvider> {
        val application = androidContext().applicationContext as Application
        LifecycleAwareActivityProvider(application)
    }
    single<RatingRequester> {
        AndroidPlayStoreRatingRequester(
            context = androidContext(),
            reviewManager = ReviewManagerFactory.create(androidContext()),
            activityProvider = get()
        )
    }
}

fun initKoin() {
    stopKoin()
    startKoin {
        allowOverride(true)
        modules(listOf(appModule, androidPlatformModule))
    }
}
