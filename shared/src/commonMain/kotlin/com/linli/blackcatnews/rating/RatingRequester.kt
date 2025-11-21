package com.linli.blackcatnews.rating

/**
 * 平台無關的評分請求抽象，讓 UI 或 ViewModel 不需要了解 Play Core 的細節。
 */
interface RatingRequester {
    /**
     * 嘗試觸發評分流程，若不符合觸發條件可直接返回。
     */
    suspend fun maybeRequestReview(reason: RatingReason)
}

/** 評分觸發的原因，可依需求擴充。 */
enum class RatingReason {
    ARTICLE_READ
}

/**
 * 本地邏輯：判斷是否應該向使用者請求評分。
 */
class RatingEligibilityDecider(private val minimumArticleReads: Int) {
    fun shouldRequest(articleReadCount: Int, hasAskedForReview: Boolean): Boolean {
        if (hasAskedForReview) return false
        return articleReadCount >= minimumArticleReads
    }
}

/**
 * 其他平台的預設實作：什麼都不做，避免打擾使用者。
 */
object NoOpRatingRequester : RatingRequester {
    override suspend fun maybeRequestReview(reason: RatingReason) = Unit
}
