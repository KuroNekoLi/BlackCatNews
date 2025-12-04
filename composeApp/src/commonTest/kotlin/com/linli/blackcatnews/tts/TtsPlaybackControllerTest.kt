package com.linli.blackcatnews.tts

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private class FakeTextToSpeechManager : TextToSpeechManager {
    var speakCount = 0
    var lastText: String? = null
    var stopCount = 0
    var lastLanguageTag: String? = null
    private var completion: (() -> Unit)? = null

    override val highlightState: StateFlow<TextHighlightRange?> =
        MutableStateFlow(null).asStateFlow()

    override fun speak(
        text: String,
        languageTag: String?,
        pitch: Float,
        rate: Float,
        onComplete: (() -> Unit)?
    ) {
        speakCount++
        lastText = text
        lastLanguageTag = languageTag
        completion = onComplete
    }

    override fun stop() {
        stopCount++
    }

    override fun release() = Unit

    fun completePlayback() {
        completion?.invoke()
    }
}

class TtsPlaybackControllerTest {

    @Test
    fun playShouldCallSpeakAndUpdateState() {
        val manager = FakeTextToSpeechManager()
        val controller = TtsPlaybackController(manager)

        controller.play("hello", "word:hello", "en-US")

        assertEquals("word:hello", controller.playingItemId)
        assertEquals(1, manager.stopCount) // play 之前會先 stop 以避免重疊
        assertEquals(1, manager.speakCount)
        assertEquals("hello", manager.lastText)
        assertEquals("en-US", manager.lastLanguageTag)

        manager.completePlayback()
        assertNull(controller.playingItemId)
    }

    @Test
    fun stopShouldClearState() {
        val manager = FakeTextToSpeechManager()
        val controller = TtsPlaybackController(manager)

        controller.play("world", "word:world")
        controller.stop()

        assertNull(controller.playingItemId)
        assertEquals(2, manager.stopCount) // play 時一次、手動停止再一次
    }
}
