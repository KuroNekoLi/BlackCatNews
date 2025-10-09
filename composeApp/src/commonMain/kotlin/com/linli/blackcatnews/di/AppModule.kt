package com.linli.blackcatnews.di

import org.koin.core.module.Module
import org.koin.dsl.module

val appModule: Module = module {
    includes(
        dispatcherModule,
        databaseModule,
        networkModule,
        repositoryModule,
        useCaseModule,
        viewModelModule
    )
}
