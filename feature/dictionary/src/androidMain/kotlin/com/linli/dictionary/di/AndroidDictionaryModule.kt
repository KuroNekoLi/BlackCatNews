package com.linli.dictionary.di

import com.linli.dictionary.data.local.AndroidDictionaryDataStore
import com.linli.dictionary.data.local.DictionaryDataStore
import org.koin.dsl.module

/**
 * Android-specific dictionary module.
 */
val androidDictionaryModule = module {
    // Android-specific implementations
    single<DictionaryDataStore> { AndroidDictionaryDataStore(get()) }
}