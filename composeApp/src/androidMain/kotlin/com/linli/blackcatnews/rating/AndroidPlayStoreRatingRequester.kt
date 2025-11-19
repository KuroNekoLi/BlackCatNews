package com.linli.blackcatnews.rating

import android.app.Activity
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.linli.blackcatnews.rating.RatingReason.ARTICLE_READ
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

private val Context.reviewPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "in_app_review"
)

/**
 * Android 端透過 Google Play In-App Review API 的實作。
 *
 * 測試方式：
 * 1. 將 APK 發佈到 Play Console 的 Internal testing 或 Internal App Sharing。
 * 2. 確認測試帳號已在測試名單中並安裝該版本。
 * 3. 滿足本地門檻後（預設閱讀 3 篇文章離開詳情頁），即可在滿足 API 配額時看到官方的評分 dialog。
 */
class AndroidPlayStoreRatingRequester(
    private val context: Context,
    private val reviewManager: ReviewManager,
    private val activityProvider: CurrentActivityProvider,
    private val eligibilityDecider: RatingEligibilityDecider = RatingEligibilityDecider(DEFAULT_READ_THRESHOLD),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RatingRequester {

    override suspend fun maybeRequestReview(reason: RatingReason) {
        if (reason != ARTICLE_READ) return

        val gatingState = withContext(ioDispatcher) {
            val updatedPrefs = context.reviewPreferencesDataStore.edit { prefs ->
                val updatedCount = (prefs[ARTICLE_READ_COUNT_KEY] ?: 0) + 1
                prefs[ARTICLE_READ_COUNT_KEY] = updatedCount
            }
            val hasAsked = updatedPrefs[HAS_ASKED_FOR_REVIEW_KEY] ?: false
            val readCount = updatedPrefs[ARTICLE_READ_COUNT_KEY] ?: 0
            readCount to hasAsked
        }

        val readCount = gatingState.first
        val hasAsked = gatingState.second
        if (!eligibilityDecider.shouldRequest(readCount, hasAsked)) return

        val activity = activityProvider.getCurrentActivity() ?: return
        val reviewInfo = requestReviewInfo() ?: return
        launchReviewDialog(activity, reviewInfo)
        markHasAskedForReview()
    }

    private suspend fun requestReviewInfo(): ReviewInfo? = suspendCancellableCoroutine { cont ->
        reviewManager.requestReviewFlow()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume(task.result)
                } else {
                    cont.resume(null)
                }
            }
    }

    private suspend fun launchReviewDialog(activity: Activity, reviewInfo: ReviewInfo) = suspendCancellableCoroutine { cont ->
        reviewManager.launchReviewFlow(activity, reviewInfo)
            .addOnCompleteListener {
                cont.resume(Unit)
            }
    }

    private suspend fun markHasAskedForReview() {
        withContext(ioDispatcher) {
            context.reviewPreferencesDataStore.edit { prefs ->
                prefs[HAS_ASKED_FOR_REVIEW_KEY] = true
            }
        }
    }

    companion object {
        private const val DEFAULT_READ_THRESHOLD = 3
        private val HAS_ASKED_FOR_REVIEW_KEY = booleanPreferencesKey("has_asked_for_review")
        private val ARTICLE_READ_COUNT_KEY = intPreferencesKey("article_read_count")
    }
}
