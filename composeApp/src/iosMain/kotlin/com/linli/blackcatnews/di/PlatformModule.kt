package com.linli.blackcatnews.di

import androidx.room.RoomDatabase
import com.linli.authentication.platformAuthProvidersModule
import com.linli.authentication.presentation.platformLoginModule
import com.linli.blackcatnews.data.local.database.NewsDatabase
import com.linli.blackcatnews.data.local.database.getDatabaseBuilder
import com.linli.dictionary.di.iosDictionaryModule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

val iosPlatformModule = module {
    // 認證模組
    includes(platformAuthProvidersModule, platformLoginModule)

    // 資料庫
    single<RoomDatabase.Builder<NewsDatabase>>(named("newsDatabaseBuilder")) {
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
 * 無參數版本的 initKoin，方便 Swift 調用
 * Swift 調用 Kotlin 的 vararg 很複雜，這個函數簡化了調用
 */
fun initKoin() {
    stopKoin()
    startKoin {
        modules(listOf(appModule, iosPlatformModule, iosDictionaryModule))
    }
}
