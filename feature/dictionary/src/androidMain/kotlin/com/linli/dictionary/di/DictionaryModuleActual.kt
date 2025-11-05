package com.linli.dictionary.di

import android.content.Context
import com.linli.dictionary.data.local.AndroidDictionaryDataStore
import com.linli.dictionary.data.local.DictionaryDataStore
import org.koin.core.scope.Scope

/**
 * Android
 */
actual fun getDataStoreForPlatform(scope: Scope): DictionaryDataStore {
    val context = scope.get<Context>()
    return AndroidDictionaryDataStore(context)
}