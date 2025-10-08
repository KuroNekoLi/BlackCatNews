package com.linli.blackcatnews.utils

import kotlinx.datetime.Clock

/**
 * 跨平台時間工具類
 *
 * 提供跨平台的時間戳獲取功能
 */
object TimeUtils {
    /**
     * 獲取當前時間戳（毫秒）
     *
     * 使用 kotlinx-datetime 提供跨平台支持
     *
     * @return 當前時間戳（毫秒）
     */
    fun currentTimeMillis(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
}
