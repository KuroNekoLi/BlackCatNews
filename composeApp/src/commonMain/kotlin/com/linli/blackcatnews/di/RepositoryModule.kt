package com.linli.blackcatnews.di

import com.linli.blackcatnews.data.repository.ArticleRepositoryImpl
import com.linli.blackcatnews.domain.repository.ArticleRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<ArticleRepository> {
        ArticleRepositoryImpl(
            newsApiService = get(),
            articleDao = get()
        )
    }
}
