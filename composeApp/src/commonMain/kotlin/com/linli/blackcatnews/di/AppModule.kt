package com.linli.blackcatnews.di

import com.linli.authentication.authModule
import com.linli.dictionary.di.dictionaryModule
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule: Module = module {
    includes(
        // 認證模組（已包含登入 UI）
        authModule,
        // 應用核心模組
        dispatcherModule,
        databaseModule,
        networkModule,
        repositoryModule,
        useCaseModule,
        viewModelModule,
        // 字典模組
        dictionaryModule
    )
}
