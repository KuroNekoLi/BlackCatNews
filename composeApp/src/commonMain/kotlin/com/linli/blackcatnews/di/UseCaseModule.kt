package com.linli.blackcatnews.di

import com.linli.blackcatnews.domain.usecase.GetAllArticlesUseCase
import com.linli.blackcatnews.domain.usecase.GetArticleDetailUseCase
import com.linli.blackcatnews.domain.usecase.GetArticlesBySectionUseCase
import com.linli.blackcatnews.domain.usecase.ManageFavoritesUseCase
import com.linli.blackcatnews.domain.usecase.RefreshArticlesUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetArticlesBySectionUseCase(repository = get()) }
    factory { RefreshArticlesUseCase(repository = get()) }
    factory { GetArticleDetailUseCase(repository = get()) }
    factory { ManageFavoritesUseCase(repository = get()) }
    factory { GetAllArticlesUseCase(getArticlesBySectionUseCase = get()) }
}
