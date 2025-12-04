package com.linli.blackcatnews.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import com.linli.authentication.platformAuthProvidersModule
import com.linli.authentication.presentation.platformLoginModule
import com.linli.blackcatnews.data.local.database.NewsDatabase
import com.linli.blackcatnews.data.local.database.getDatabaseBuilder
import com.linli.blackcatnews.tts.IOSTextToSpeechManager
import com.linli.blackcatnews.tts.TextToSpeechManager
import com.linli.dictionary.di.iosDictionaryModule
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
val iosPlatformModule = module {
    // 認證模組
    includes(platformAuthProvidersModule, platformLoginModule)

    // 資料庫
    single<RoomDatabase.Builder<NewsDatabase>>(named("newsDatabaseBuilder")) {
        getDatabaseBuilder()
    }
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )
                val docDir = requireNotNull(documentDirectory)
                val path = requireNotNull(docDir.path) { "Document directory path is null" }
                (path + "/user_preferences.preferences_pb").toPath()
            }
        )
    }

    factory<TextToSpeechManager> {
        IOSTextToSpeechManager()
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
