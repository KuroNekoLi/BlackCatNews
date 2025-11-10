package com.linli.blackcatnews.di

import com.linli.authentication.domain.usecase.SignOutUseCase
import com.linli.blackcatnews.data.preferences.UserPreferencesRepository
import com.linli.blackcatnews.presentation.viewmodel.ArticleDetailViewModel
import com.linli.blackcatnews.presentation.viewmodel.FavoritesViewModel
import com.linli.blackcatnews.presentation.viewmodel.HomeViewModel
import com.linli.blackcatnews.presentation.viewmodel.SearchViewModel
import com.linli.blackcatnews.presentation.viewmodel.SettingsViewModel
import com.linli.dictionary.presentation.wordbank.WordBankViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { UserPreferencesRepository() }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { (articleId: String) -> ArticleDetailViewModel(articleId, get()) }
    viewModel { SettingsViewModel(get(), get<SignOutUseCase>()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { SearchViewModel(get()) }

    // --- 生字本（WordBank）DI ---
    // ViewModel
    viewModel {
        WordBankViewModel(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}
