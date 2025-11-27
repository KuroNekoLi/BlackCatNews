package com.linli.blackcatnews.di

import com.linli.blackcatnews.data.remote.api.HttpClientFactory
import com.linli.blackcatnews.data.remote.api.INewsApiService
import com.linli.blackcatnews.data.remote.api.MockNewsApiService
import com.linli.blackcatnews.data.remote.api.NewsApiService
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * 網路模組配置
 *
 * 控制是否使用 Mock 數據：
 * - useMockData = true: 使用本地 mock 數據（適合開發和測試）
 * - useMockData = false: 使用真實 API（適合生產環境）
 */
private const val USE_MOCK_DATA = false

val networkModule: Module = module {
    // 只在使用真實 API 時創建 HttpClient
    if (!USE_MOCK_DATA) {
        single { HttpClientFactory.create() }
    }

    // 根據配置選擇使用 Mock Service 或真實 Service
    single<INewsApiService> {
        if (USE_MOCK_DATA) {
            // 使用 Mock Service（開發/測試環境）
            MockNewsApiService()
        } else {
            // 使用真實 Service（生產環境）
            NewsApiService(get())
        }
    }
}
