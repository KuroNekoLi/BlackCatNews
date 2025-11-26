package com.linli.blackcatnews.tts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.cinterop.useContents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.AVFAudio.AVSpeechBoundary
import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechSynthesizerDelegateProtocol
import platform.AVFAudio.AVSpeechUtterance
import platform.AVFAudio.AVSpeechUtteranceDefaultSpeechRate
import platform.Foundation.NSLocale
import platform.Foundation.NSRange
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.darwin.NSObject

private class IOSTextToSpeechManager : TextToSpeechManager {
    private val synthesizer = AVSpeechSynthesizer()
    private val delegate = TtsDelegate()
    private var currentCompletion: (() -> Unit)? = null

    private val _highlightState = MutableStateFlow<TextHighlightRange?>(null)
    override val highlightState: StateFlow<TextHighlightRange?> = _highlightState.asStateFlow()

    init {
        synthesizer.delegate = delegate
        delegate.onFinish = {
            currentCompletion?.invoke()
            currentCompletion = null
            _highlightState.value = null
        }
        delegate.onCancel = {
            currentCompletion = null
            _highlightState.value = null
        }
        delegate.onRangeStart = { start, end ->
            _highlightState.value = TextHighlightRange(start, end)
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
        _highlightState.value = null
    }

    override fun release() {
        stop()
        synthesizer.delegate = null
        _highlightState.value = null
    }
}

private class TtsDelegate : NSObject(), AVSpeechSynthesizerDelegateProtocol {
    var onFinish: (() -> Unit)? = null
    var onCancel: (() -> Unit)? = null
    var onRangeStart: ((Int, Int) -> Unit)? = null

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

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    @ObjCSignatureOverride
    override fun speechSynthesizer(
        synthesizer: AVSpeechSynthesizer,
        willSpeakRangeOfSpeechString: CValue<NSRange>,
        utterance: AVSpeechUtterance
    ) {
        willSpeakRangeOfSpeechString.useContents {
            val start = location.toInt()
            val end = (location + length).toInt()
            onRangeStart?.invoke(start, end)
        }
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
