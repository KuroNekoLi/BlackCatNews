package com.linli.dictionary.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.linli.dictionary.data.local.database.DictionaryDatabase
import com.linli.dictionary.data.remote.DefaultDictionaryApi
import com.linli.dictionary.data.remote.DictionaryApi
import com.linli.dictionary.data.repository.DictionaryRepositoryImpl
import com.linli.dictionary.data.repository.WordBankRepositoryImpl
import com.linli.dictionary.domain.repository.DictionaryRepository
import com.linli.dictionary.domain.repository.WordBankRepository
import com.linli.dictionary.domain.service.FsrsScheduler
import com.linli.dictionary.domain.usecase.AddWordToWordBankUseCase
import com.linli.dictionary.domain.usecase.GetDueReviewCardsUseCase
import com.linli.dictionary.domain.usecase.GetRecentSearchesUseCase
import com.linli.dictionary.domain.usecase.GetSavedWordsUseCase
import com.linli.dictionary.domain.usecase.GetWordBankCountUseCase
import com.linli.dictionary.domain.usecase.IsWordInWordBankUseCase
import com.linli.dictionary.domain.usecase.LookupWordUseCase
import com.linli.dictionary.domain.usecase.RemoveWordFromWordBankUseCase
import com.linli.dictionary.domain.usecase.ResetWordProgressUseCase
import com.linli.dictionary.domain.usecase.ReviewWordUseCase
import com.linli.dictionary.presentation.DictionaryViewModel
import com.linli.dictionary.presentation.wordbank.WordBankViewModel
import com.linli.dictionary.presentation.wordbank.WordReviewViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

/**
 * Koin module for the dictionary feature.
 */
@OptIn(ExperimentalTime::class)
val dictionaryModule = module {
    // Database
    single<DictionaryDatabase> {
        val builder: RoomDatabase.Builder<DictionaryDatabase> =
            get(named("dictionaryDatabaseBuilder"))
        builder
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(true)
            .build()
    }
    single { get<DictionaryDatabase>().dictionaryDao() }

    // API
    single { createHttpClient() }
    singleOf(::DefaultDictionaryApi) { bind<DictionaryApi>() }
//    singleOf(::MockDictionaryApi) { bind<DictionaryApi>() }

    // Repository
    single<DictionaryRepository> { DictionaryRepositoryImpl(get(), get()) }

    // WordBank 相關
    single<WordBankRepository> { WordBankRepositoryImpl(get(), get()) }
    single { FsrsScheduler() }

    // Use cases
    factoryOf(::LookupWordUseCase)
    factoryOf(::GetRecentSearchesUseCase)

    // WordBank Use cases
    factoryOf(::GetSavedWordsUseCase)
    factoryOf(::AddWordToWordBankUseCase)
    factoryOf(::RemoveWordFromWordBankUseCase)
    factoryOf(::IsWordInWordBankUseCase)
    factoryOf(::GetWordBankCountUseCase)
    factoryOf(::GetDueReviewCardsUseCase)
    factoryOf(::ReviewWordUseCase)
    factoryOf(::ResetWordProgressUseCase)

    // ViewModel
    factory { DictionaryViewModel(get(), get()) }
    factoryOf(::WordBankViewModel)
    factoryOf(::WordReviewViewModel)
}

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
