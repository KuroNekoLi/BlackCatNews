package com.linli.dictionary.di

import com.linli.dictionary.data.local.DictionaryDataStore
import com.linli.dictionary.data.local.IosDictionaryDataStore
import org.koin.core.scope.Scope

/**
 * iOS
 */
actual fun getDataStoreForPlatform(scope: Scope): DictionaryDataStore {
    return IosDictionaryDataStore()
}