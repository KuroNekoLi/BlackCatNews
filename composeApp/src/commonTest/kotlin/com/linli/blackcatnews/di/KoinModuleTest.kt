package com.linli.blackcatnews.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify
import kotlin.test.Test

class KoinModuleTest {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun verifyKoinModules() {
        appModule.verify(
            extraTypes = listOf(
                RoomDatabase.Builder::class,
                DataStore::class,
                Preferences::class,
                com.linli.authentication.domain.SignInUIClient::class,
                com.linli.authentication.domain.usecase.GetCurrentUserUseCase::class,
                com.linli.authentication.domain.usecase.SignOutUseCase::class,
                Set::class,
                List::class,
                Map::class,
                com.linli.authentication.data.AuthProvider::class,
                io.ktor.client.engine.HttpClientEngine::class,
                io.ktor.client.HttpClientConfig::class,
                com.linli.blackcatnews.tts.TextToSpeechManager::class
            )
        )
    }
}
