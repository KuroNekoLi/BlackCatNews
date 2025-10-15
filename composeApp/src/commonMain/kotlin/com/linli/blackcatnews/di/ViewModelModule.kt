package com.linli.blackcatnews.di

import com.linli.blackcatnews.presentation.viewmodel.ArticleDetailViewModel
import com.linli.blackcatnews.presentation.viewmodel.FavoritesViewModel
import com.linli.blackcatnews.presentation.viewmodel.HomeViewModel
import com.linli.blackcatnews.presentation.viewmodel.SearchViewModel
import com.linli.blackcatnews.presentation.viewmodel.SettingsViewModel
import com.linli.blackcatnews.data.preferences.UserPreferencesRepository
import org.koin.dsl.module

val viewModelModule = module {
    single { UserPreferencesRepository() }
    single { HomeViewModel(get(), get(), get()) }
    factory { (articleId: String) -> ArticleDetailViewModel(articleId, get()) }
    single { SettingsViewModel(get()) }
    single { FavoritesViewModel(get()) }
    single { SearchViewModel(get()) }
}
