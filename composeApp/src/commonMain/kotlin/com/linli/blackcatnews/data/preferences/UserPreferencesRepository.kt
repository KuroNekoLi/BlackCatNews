package com.linli.blackcatnews.data.preferences

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 使用者偏好儲存庫
 * 目前僅用於保存首頁內容語言偏好
 */
class UserPreferencesRepository {

    private val prefersChineseFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun prefersChinese(): Flow<Boolean> = prefersChineseFlow.asStateFlow()

    suspend fun setPrefersChinese(useChinese: Boolean) {
        prefersChineseFlow.emit(useChinese)
    }
}
