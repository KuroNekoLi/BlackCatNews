package com.linli.blackcatnews

import android.app.Application
import com.linli.blackcatnews.di.androidPlatformModule
import com.linli.blackcatnews.di.appModule
import com.linli.dictionary.di.androidDictionaryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BlackCatNewsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BlackCatNewsApplication)
            modules(
                appModule,
                androidPlatformModule,
                androidDictionaryModule
            )
        }
    }
}
