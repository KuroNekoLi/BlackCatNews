package com.linli.blackcatnews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linli.blackcatnews.domain.model.ArticleDetail
import com.linli.blackcatnews.domain.model.BilingualParagraphType
import com.linli.blackcatnews.tts.TextToSpeechManager
import com.linli.blackcatnews.tts.TtsPlaybackController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class TtsViewModel(
    private val ttsManager: TextToSpeechManager
) : ViewModel() {

    val playbackController = TtsPlaybackController(ttsManager)
    val highlightState = ttsManager.highlightState

    private val _playingParagraphIndex = MutableStateFlow(-1)
    val playingParagraphIndex: StateFlow<Int> = _playingParagraphIndex.asStateFlow()

    private val _isFullTextPlaying = MutableStateFlow(false)
    val isFullTextPlaying: StateFlow<Boolean> = _isFullTextPlaying.asStateFlow()

    fun startFullTextPlayback(article: ArticleDetail) {
        if (_isFullTextPlaying.value) return
        _isFullTextPlaying.value = true
        viewModelScope.launch {
            val paragraphs = article.content.paragraphs
            for (i in paragraphs.indices) {
                if (!_isFullTextPlaying.value) break

                val p = paragraphs[i]
                val englishText = p.english
                if (p.type == BilingualParagraphType.TEXT && !englishText.isNullOrEmpty()) {
                    _playingParagraphIndex.value = i
                    playParagraphAndWait(englishText)
                } else {
                    _playingParagraphIndex.value = i
                }
            }
            _isFullTextPlaying.value = false
            _playingParagraphIndex.value = -1
        }
    }

    private suspend fun playParagraphAndWait(text: String) =
        suspendCancellableCoroutine { continuation ->
            ttsManager.speak(
                text = text,
                onComplete = {
                    if (continuation.isActive) {
                        continuation.resume(Unit)
                    }
                }
            )
            continuation.invokeOnCancellation {
                ttsManager.stop()
            }
        }

    fun stopFullTextPlayback() {
        _isFullTextPlaying.value = false
        _playingParagraphIndex.value = -1
        ttsManager.stop()
    }

    fun playSingle(text: String, id: String, languageTag: String = "en-US") {
        stopFullTextPlayback() // Stop full text if playing
        playbackController.play(text, id, languageTag)
    }

    fun stop() {
        stopFullTextPlayback()
        playbackController.stop()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.release()
    }
}
