package com.linli.dictionary.domain.model

import kotlin.time.ExperimentalTime
import kotlinx.datetime.Instant as KotlinxInstant

/**
 * 單字複習用的卡片，包含顯示內容與當前排程狀態。
 *
 * @property word 要複習的單字內容
 * @property metadata FSRS 排程的當前狀態資料
 */
data class ReviewCard(
    val word: Word,
    val metadata: ReviewMetadata
)

/**
 * FSRS 排程狀態
 *
 * @property dueAt 下次應複習的時間
 * @property lastReviewedAt 上一次複習時間，未複習過則為 null
 * @property stability 穩定度（天數）
 * @property difficulty 難度分數，範圍 1~10
 * @property reps 已複習次數
 * @property lapses 遺忘次數
 * @property state 當前卡片狀態（新卡、學習中、複習中）
 * @property scheduledDays 上一次排程的間隔天數
 * @property elapsedDays 這次複習距離上次的天數
 */
@OptIn(ExperimentalTime::class)
data class ReviewMetadata(
    val dueAt: KotlinxInstant,
    val lastReviewedAt: KotlinxInstant?,
    val stability: Double,
    val difficulty: Double,
    val reps: Int,
    val lapses: Int,
    val state: ReviewState,
    val scheduledDays: Int,
    val elapsedDays: Int
)

/**
 * FSRS 卡片狀態
 */
enum class ReviewState {
    NEW,
    LEARNING,
    REVIEW
}

/**
 * 使用者複習給分
 */
enum class ReviewRating {
    AGAIN,
    HARD,
    GOOD,
    EASY
}
