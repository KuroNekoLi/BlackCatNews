package com.linli.blackcatnews.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import java.util.UUID

private class AndroidTextToSpeechManager(
    context: Context
) : TextToSpeechManager {
    private var textToSpeech: TextToSpeech? = null
    private var initialized = false
    private var currentCompletion: (() -> Unit)? = null

    init {
        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            initialized = status == TextToSpeech.SUCCESS
        }
    }

    override fun speak(
        text: String,
        languageTag: String?,
        pitch: Float,
        rate: Float,
        onComplete: (() -> Unit)?
    ) {
        val engine = textToSpeech ?: return
        if (!initialized) return
        engine.stop()
        currentCompletion = onComplete
        val locale = languageTag?.let { Locale.forLanguageTag(it) } ?: engine.language ?: Locale.US
        engine.language = locale
        engine.setPitch(pitch)
        engine.setSpeechRate(rate)
        val utteranceId = UUID.randomUUID().toString()
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit

            override fun onDone(doneUtteranceId: String?) {
                if (utteranceId == doneUtteranceId) {
                    currentCompletion?.invoke()
                    currentCompletion = null
                }
            }

            override fun onError(errorUtteranceId: String?) {
                if (utteranceId == errorUtteranceId) {
                    currentCompletion = null
                }
            }
        })
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    override fun stop() {
        textToSpeech?.stop()
        currentCompletion = null
    }

    override fun release() {
        textToSpeech?.shutdown()
        textToSpeech = null
        currentCompletion = null
    }
}

/**
 * 取得 Android 端的 TTS 管理器，跟隨 Compose 生命週期釋放資源。
 */
@Composable
actual fun rememberTextToSpeechManager(): TextToSpeechManager? {
    val context = LocalContext.current
    val manager = remember(context) { AndroidTextToSpeechManager(context) }
    DisposableEffect(manager) {
        onDispose { manager.release() }
    }
    return manager
}
