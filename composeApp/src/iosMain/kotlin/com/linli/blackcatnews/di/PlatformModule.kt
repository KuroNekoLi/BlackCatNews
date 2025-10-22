package com.linli.blackcatnews.di

import androidx.room.RoomDatabase
import com.linli.authentication.platformAuthProvidersModule
import com.linli.blackcatnews.data.local.database.NewsDatabase
import com.linli.blackcatnews.data.local.database.getDatabaseBuilder
import com.linli.authentication.presentation.platformLoginModule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module

val iosPlatformModule = module {
    // 认证模块
    includes(platformAuthProvidersModule, platformLoginModule)

    // 数据库
    single<RoomDatabase.Builder<NewsDatabase>> {
        getDatabaseBuilder()
    }
}

//fun initKoin(vararg extraModules: Module) {
//    stopKoin()
//    startKoin {
//        modules(listOf(appModule, iosPlatformModule) + extraModules)
//    }
//}

/**
 * 无参数版本的 initKoin，方便 Swift 调用
 * Swift 调用 Kotlin 的 vararg 很复杂，这个函数简化了调用
 */
fun initKoin() {
    stopKoin()
    startKoin {
        modules(listOf(appModule, iosPlatformModule))
    }
}
