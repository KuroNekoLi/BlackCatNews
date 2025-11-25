package com.linli.blackcatnews.tts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * 跨平台語音朗讀管理介面，封裝平台端 TTS 實作並提供統一 API。
 */
interface TextToSpeechManager {
    /**
     * 朗讀指定文字。
     *
     * @param text 要朗讀的內容
     * @param languageTag IETF 語言代碼，例如 "en-US"
     * @param pitch 音調倍率，1f 為預設值
     * @param rate 語速倍率，1f 為預設值
     * @param onComplete 播放完成或被中斷時的回呼
     */
    fun speak(
        text: String,
        languageTag: String? = DEFAULT_LANGUAGE_TAG,
        pitch: Float = 1f,
        rate: Float = 1f,
        onComplete: (() -> Unit)? = null
    )

    /**
     * 停止當前朗讀，若有播放中的音訊會立即終止。
     */
    fun stop()

    /**
     * 釋放平台資源，Composable 銷毀時必須呼叫以避免資源洩漏。
     */
    fun release()
}

/**
 * 提供與 Compose 生命週期綁定的 TTS 管理器，若平台不支援則回傳 null。
 *
 * @return 可用的 TTS 管理器或 null（不支援平台時）
 */
@Composable
expect fun rememberTextToSpeechManager(): TextToSpeechManager?

/**
 * 控制播放中的項目 ID 並橋接至 TextToSpeechManager，避免重疊播放。
 */
class TtsPlaybackController(
    private val textToSpeechManager: TextToSpeechManager?
) {
    private val playingItemIdState: MutableState<String?> = mutableStateOf(null)

    /**
     * 目前正在播放的項目識別碼，null 代表沒有播放。
     */
    val playingItemId: String?
        get() = playingItemIdState.value

    /**
     * 播放指定文字，會先停止任何現有播放以避免重疊。
     *
     * @param text 要朗讀的文字
     * @param id 對應 UI 項目的唯一識別碼
     * @param languageTag 語言標籤，預設英文
     */
    fun play(text: String, id: String, languageTag: String? = DEFAULT_LANGUAGE_TAG) {
        val manager = textToSpeechManager ?: run {
            playingItemIdState.value = null
            return
        }
        manager.stop()
        playingItemIdState.value = id
        manager.speak(
            text = text,
            languageTag = languageTag,
            onComplete = {
                if (playingItemIdState.value == id) {
                    playingItemIdState.value = null
                }
            }
        )
    }

    /**
     * 停止播放並清空目前的播放狀態。
     */
    fun stop() {
        textToSpeechManager?.stop()
        playingItemIdState.value = null
    }
}

/**
 * 在 Compose 中記住一個 TtsPlaybackController，會隨 TTS 管理器的生命週期釋放。
 *
 * @param ttsManager 平台 TTS 管理器，為 null 時僅更新狀態不執行播放
 * @return 與組合生命週期綁定的播放控制器
 */
@Composable
fun rememberTtsPlaybackController(
    ttsManager: TextToSpeechManager?
): TtsPlaybackController {
    val controller = remember(ttsManager) { TtsPlaybackController(ttsManager) }
    DisposableEffect(controller) {
        onDispose { controller.stop() }
    }
    return controller
}

/**
 * 預設語音朗讀語言標籤（美式英文）。
 */
const val DEFAULT_LANGUAGE_TAG = "en-US"
