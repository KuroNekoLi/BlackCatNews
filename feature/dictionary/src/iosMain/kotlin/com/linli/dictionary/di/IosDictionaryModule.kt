package com.linli.dictionary.di

import com.linli.dictionary.data.local.DictionaryDataStore
import com.linli.dictionary.data.local.IosDictionaryDataStore
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * iOS-specific dictionary module.
 */
val iosDictionaryModule = module {
    // iOS-specific implementations
    singleOf(::IosDictionaryDataStore) { bind<DictionaryDataStore>() }
}