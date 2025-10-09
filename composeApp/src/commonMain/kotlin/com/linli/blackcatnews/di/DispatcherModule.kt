package com.linli.blackcatnews.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val DISPATCHER_IO = "dispatcher_io"

val dispatcherModule = module {
    single<CoroutineDispatcher>(named(DISPATCHER_IO)) { Dispatchers.Default }
}
