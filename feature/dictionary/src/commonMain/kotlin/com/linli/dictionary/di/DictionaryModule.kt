package com.linli.dictionary.di

import com.linli.dictionary.data.local.DictionaryDataStore
import com.linli.dictionary.data.remote.DefaultDictionaryApi
import com.linli.dictionary.data.remote.DictionaryApi
import com.linli.dictionary.data.repository.DictionaryRepositoryImpl
import com.linli.dictionary.domain.repository.DictionaryRepository
import com.linli.dictionary.domain.usecase.GetRecentSearchesUseCase
import com.linli.dictionary.domain.usecase.LookupWordUseCase
import com.linli.dictionary.presentation.DictionaryViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Koin module for the dictionary feature.
 */
val dictionaryModule = module {
    // Platform-specific DataStore implementation
    single<DictionaryDataStore> {
        // 使用平台特定的實現
        getDataStoreForPlatform(this)
    }

    // API
    single { createHttpClient() }
    singleOf(::DefaultDictionaryApi) { bind<DictionaryApi>() }

    // Repository
    singleOf(::DictionaryRepositoryImpl) { bind<DictionaryRepository>() }

    // Use cases
    factoryOf(::LookupWordUseCase)
    factoryOf(::GetRecentSearchesUseCase)

    // ViewModel
    factory { DictionaryViewModel(get(), get()) }
}

/**
 * 根據當前平台返回對應的 DictionaryDataStore 實現
 */
expect fun getDataStoreForPlatform(scope: org.koin.core.scope.Scope): DictionaryDataStore

/**
 * Creates an HTTP client with JSON serialization and logging.
 */
private fun createHttpClient(): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }
}