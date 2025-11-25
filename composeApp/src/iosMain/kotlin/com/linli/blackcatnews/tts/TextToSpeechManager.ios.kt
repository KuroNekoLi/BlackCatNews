package com.linli.blackcatnews.tts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.ObjCSignatureOverride
import platform.AVFAudio.AVSpeechBoundary
import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechSynthesizerDelegateProtocol
import platform.AVFAudio.AVSpeechUtterance
import platform.AVFAudio.AVSpeechUtteranceDefaultSpeechRate
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.darwin.NSObject

private class IOSTextToSpeechManager : TextToSpeechManager {
    private val synthesizer = AVSpeechSynthesizer()
    private val delegate = TtsDelegate()
    private var currentCompletion: (() -> Unit)? = null

    init {
        synthesizer.delegate = delegate
        delegate.onFinish = {
            currentCompletion?.invoke()
            currentCompletion = null
        }
        delegate.onCancel = {
            currentCompletion = null
        }
    }

    override fun speak(
        text: String,
        languageTag: String?,
        pitch: Float,
        rate: Float,
        onComplete: (() -> Unit)?
    ) {
        stop()
        currentCompletion = onComplete
        val utterance = AVSpeechUtterance.speechUtteranceWithString(text)
        val voice = languageTag?.let { AVSpeechSynthesisVoice.voiceWithLanguage(it) }
            ?: AVSpeechSynthesisVoice.voiceWithLanguage(NSLocale.currentLocale.languageCode)
            ?: AVSpeechSynthesisVoice.voiceWithLanguage(DEFAULT_LANGUAGE_TAG)
        utterance.voice = voice
        utterance.pitchMultiplier = pitch
        utterance.rate = rate * AVSpeechUtteranceDefaultSpeechRate
        synthesizer.speakUtterance(utterance)
    }

    override fun stop() {
        synthesizer.stopSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
        currentCompletion = null
    }

    override fun release() {
        stop()
        synthesizer.delegate = null
    }
}

private class TtsDelegate : NSObject(), AVSpeechSynthesizerDelegateProtocol {
    var onFinish: (() -> Unit)? = null
    var onCancel: (() -> Unit)? = null

    @ObjCSignatureOverride
    override fun speechSynthesizer(
        synthesizer: AVSpeechSynthesizer,
        didFinishSpeechUtterance: AVSpeechUtterance
    ) {
        onFinish?.invoke()
    }

    @ObjCSignatureOverride
    override fun speechSynthesizer(
        synthesizer: AVSpeechSynthesizer,
        didCancelSpeechUtterance: AVSpeechUtterance
    ) {
        onCancel?.invoke()
    }
}

/**
 * 取得 iOS 端的 TTS 管理器，隨 Compose 生命週期釋放資源。
 */
@Composable
actual fun rememberTextToSpeechManager(): TextToSpeechManager? {
    val manager = remember { IOSTextToSpeechManager() }
    DisposableEffect(manager) {
        onDispose { manager.release() }
    }
    return manager
}
