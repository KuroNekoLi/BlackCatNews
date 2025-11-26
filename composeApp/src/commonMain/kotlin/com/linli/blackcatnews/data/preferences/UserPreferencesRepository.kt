@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.linli.blackcatnews.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * 使用者偏好儲存庫
 * 用於保存使用者設定與學習連續天數 (Streak)
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val PREFERS_CHINESE = booleanPreferencesKey("prefers_chinese")
        private val DAILY_STREAK = intPreferencesKey("daily_streak")
        private val LAST_VISIT_TIME = longPreferencesKey("last_visit_time")
    }

    val prefersChinese: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PREFERS_CHINESE] ?: false
        }

    val dailyStreak: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[DAILY_STREAK] ?: 0
        }

    suspend fun setPrefersChinese(useChinese: Boolean) {
        dataStore.edit { preferences ->
            preferences[PREFERS_CHINESE] = useChinese
        }
    }

    /**
     * 更新每日連勝邏輯
     * 1. 若最後訪問時間與今天相同 -> 不變
     * 2. 若最後訪問時間是昨天 -> 連勝 + 1
     * 3. 若最後訪問時間早於昨天 -> 連勝歸 1
     */
    suspend fun updateStreak() {
        // Use kotlin.time.Clock as workaround for kotlinx.datetime.Clock.System resolution issue
        val currentEpoch = Clock.System.now().toEpochMilliseconds()
        val currentInstant = Instant.fromEpochMilliseconds(currentEpoch)
        val todayDate = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date

        dataStore.edit { preferences ->
            val lastVisitEpoch = preferences[LAST_VISIT_TIME] ?: 0L
            val streak = preferences[DAILY_STREAK] ?: 0

            if (lastVisitEpoch == 0L) {
                // 第一次使用
                preferences[DAILY_STREAK] = 1
                preferences[LAST_VISIT_TIME] = currentEpoch
            } else {
                val lastVisitInstant = Instant.fromEpochMilliseconds(lastVisitEpoch)
                val lastVisitDate =
                    lastVisitInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date

                val yesterday = todayDate.minus(DatePeriod(days = 1))

                when (lastVisitDate) {
                    todayDate -> {
                        // 今天已經登入過，保持現狀
                    }

                    yesterday -> {
                        // 昨天登入過，連勝 +1
                        preferences[DAILY_STREAK] = streak + 1
                        preferences[LAST_VISIT_TIME] = currentEpoch
                    }

                    else -> {
                        if (lastVisitDate < yesterday) {
                            // 斷開了（超過一天沒登入），重置為 1
                            preferences[DAILY_STREAK] = 1
                            preferences[LAST_VISIT_TIME] = currentEpoch
                        }
                    }
                }
            }
        }
    }
}
