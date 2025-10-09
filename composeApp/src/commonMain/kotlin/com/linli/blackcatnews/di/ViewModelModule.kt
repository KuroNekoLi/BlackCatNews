package com.linli.blackcatnews.di

import com.linli.blackcatnews.presentation.viewmodel.ArticleDetailViewModel
import com.linli.blackcatnews.presentation.viewmodel.HomeViewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { HomeViewModel(get(), get()) }
    factory { (articleId: String) -> ArticleDetailViewModel(articleId, get()) }
}
