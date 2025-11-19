package com.linli.blackcatnews.rating

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RatingEligibilityDeciderTest {

    private val decider = RatingEligibilityDecider(minimumArticleReads = 3)

    @Test
    fun `should not request review when flag already set`() {
        assertFalse(decider.shouldRequest(articleReadCount = 99, hasAskedForReview = true))
    }

    @Test
    fun `should not request review before threshold`() {
        assertFalse(decider.shouldRequest(articleReadCount = 2, hasAskedForReview = false))
    }

    @Test
    fun `should request review after threshold`() {
        assertTrue(decider.shouldRequest(articleReadCount = 3, hasAskedForReview = false))
    }
}
