package com.linli.blackcatnews

import android.app.Application
import com.linli.blackcatnews.di.androidPlatformModule
import com.linli.blackcatnews.di.appModule
import com.linli.blackcatnews.rating.CurrentActivityProvider
import com.linli.dictionary.di.androidDictionaryModule
import org.koin.android.ext.android.inject
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

        // 強制初始化 CurrentActivityProvider，以便儘早開始監聽 Activity 生命週期
        // 避免懶加載導致錯過第一個 Activity 的 Resumed 事件
    }
}
