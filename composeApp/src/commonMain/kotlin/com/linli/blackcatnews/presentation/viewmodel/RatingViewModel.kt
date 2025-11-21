package com.linli.blackcatnews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linli.blackcatnews.rating.RatingReason
import com.linli.blackcatnews.rating.RatingRequester
import kotlinx.coroutines.launch

/**
 * 全域評分 ViewModel
 * 管理應用內評分請求邏輯
 */
class RatingViewModel(
    private val ratingRequester: RatingRequester
) : ViewModel() {

    /**
     * 當使用者閱讀完文章後調用
     */
    fun onArticleRead() {
        viewModelScope.launch {
            try {
                ratingRequester.maybeRequestReview(RatingReason.ARTICLE_READ)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
