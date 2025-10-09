package com.linli.blackcatnews.di

import com.linli.blackcatnews.data.remote.api.HttpClientFactory
import com.linli.blackcatnews.data.remote.api.NewsApiService
import org.koin.core.module.Module
import org.koin.dsl.module

val networkModule: Module = module {
    single { HttpClientFactory.create() }

    single { NewsApiService(get()) }
}
