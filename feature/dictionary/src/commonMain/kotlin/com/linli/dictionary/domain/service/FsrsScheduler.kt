package com.linli.dictionary.domain.service

import com.linli.dictionary.domain.model.ReviewMetadata
import com.linli.dictionary.domain.model.ReviewRating
import com.linli.dictionary.domain.model.ReviewState
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Instant as KotlinxInstant

/**
 * FSRS 排程器，根據使用者的給分計算下一次複習時間與卡片狀態。
 * 以預設參數模擬 FSRS 的間隔調整，並保留可注入的 clock 以利測試。
 *
 * @property config 排程調整用參數
 * @property nowProvider 取得當下時間的函式，預設使用系統時間
 */
@OptIn(ExperimentalTime::class)
class FsrsScheduler(
    private val config: FsrsSchedulerConfig = FsrsSchedulerConfig(),
    private val nowProvider: () -> KotlinxInstant = {
        KotlinxInstant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
    }
) {

    /**
     * 根據 FSRS 規則更新卡片狀態
     *
     * @param metadata 目前的排程狀態
     * @param rating 使用者對卡片的評分
     * @param now 當前時間，預設為系統時間
     * @return 更新後的排程狀態
     */
    fun schedule(
        metadata: ReviewMetadata,
        rating: ReviewRating,
        now: KotlinxInstant = nowProvider()
    ): ReviewMetadata {
        val lastReviewInstant = metadata.lastReviewedAt ?: now
        val elapsedDays = daysBetween(lastReviewInstant, now)
        val currentDifficulty = metadata.difficulty

        val nextDifficulty = (currentDifficulty + config.difficultyDelta(rating))
            .coerceIn(MIN_DIFFICULTY, MAX_DIFFICULTY)

        val nextStability = when (rating) {
            ReviewRating.AGAIN -> max(config.minStability, metadata.stability * config.lapsePenalty)
            ReviewRating.HARD -> max(
                config.minStability,
                metadata.stability * config.hardIntervalFactor
            )

            ReviewRating.GOOD -> max(
                config.minStability,
                metadata.stability * config.goodIntervalFactor
            )

            ReviewRating.EASY -> max(
                config.minStability,
                metadata.stability * config.easyIntervalFactor
            )
        }.coerceAtLeast(config.minStability)

        val scheduledDays = max(config.minIntervalDays, nextStability.roundToInt())
        val dueAt = now.plus(scheduledDays.toLong(), DateTimeUnit.DAY, TimeZone.UTC)
        val nextState = when {
            metadata.reps == 0 -> ReviewState.LEARNING
            rating == ReviewRating.AGAIN -> ReviewState.LEARNING
            else -> ReviewState.REVIEW
        }

        return metadata.copy(
            dueAt = dueAt,
            lastReviewedAt = now,
            stability = nextStability,
            difficulty = nextDifficulty,
            reps = metadata.reps + 1,
            lapses = if (rating == ReviewRating.AGAIN) metadata.lapses + 1 else metadata.lapses,
            state = nextState,
            scheduledDays = scheduledDays,
            elapsedDays = elapsedDays
        )
    }

    private fun daysBetween(from: KotlinxInstant, to: KotlinxInstant): Int {
        val fromDate = from.toLocalDateTime(TimeZone.UTC).date
        val toDate = to.toLocalDateTime(TimeZone.UTC).date
        return max(0, fromDate.daysUntil(toDate))
    }

    private fun FsrsSchedulerConfig.difficultyDelta(rating: ReviewRating): Double {
        return when (rating) {
            ReviewRating.AGAIN -> lapseDifficultyDelta
            ReviewRating.HARD -> hardDifficultyDelta
            ReviewRating.GOOD -> goodDifficultyDelta
            ReviewRating.EASY -> easyDifficultyDelta
        }
    }

    private companion object {
        const val MIN_DIFFICULTY = 1.0
        const val MAX_DIFFICULTY = 10.0
    }
}

/**
 * FSRS 間隔調整設定，可依需求微調各評分的影響係數。
 *
 * @property minIntervalDays 最小間隔天數
 * @property minStability 穩定度下限，避免乘積為零
 * @property hardIntervalFactor Hard 評分的間隔倍率
 * @property goodIntervalFactor Good 評分的間隔倍率
 * @property easyIntervalFactor Easy 評分的間隔倍率
 * @property lapsePenalty Again 評分的間隔倍率（通常小於 1）
 * @property hardDifficultyDelta Hard 評分對難度的調整
 * @property goodDifficultyDelta Good 評分對難度的調整
 * @property easyDifficultyDelta Easy 評分對難度的調整
 * @property lapseDifficultyDelta Again 評分對難度的調整
 */
data class FsrsSchedulerConfig(
    val minIntervalDays: Int = 1,
    val minStability: Double = 0.3,
    val hardIntervalFactor: Double = 1.4,
    val goodIntervalFactor: Double = 2.0,
    val easyIntervalFactor: Double = 2.8,
    val lapsePenalty: Double = 0.6,
    val hardDifficultyDelta: Double = 0.2,
    val goodDifficultyDelta: Double = -0.1,
    val easyDifficultyDelta: Double = -0.4,
    val lapseDifficultyDelta: Double = 0.6
)
