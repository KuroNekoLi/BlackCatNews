package com.linli.blackcatnews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.linli.blackcatnews.parser.parseHtmlToArticle
import com.linli.blackcatnews.ui.ArticleView
import com.linli.blackcatnews.utils.fromJsonHtml
import com.linli.blackcatnews.utils.wrapHtml
import okio.FileSystem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    // 初始化 Coil ImageLoader（必須在最頂層調用）
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .crossfade(true)
            .logger(coil3.util.DebugLogger())  // 啟用調試日誌
            .components {
                add(KtorNetworkFetcherFactory())  // 添加 Ktor 網絡引擎
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
                    .maxSizeBytes(512L * 1024 * 1024) // 512MB
                    .build()
            }
            .build()
    }

    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }

        // 示例：JSON 格式的 HTML（带有转义字符）
        val jsonHtml = """
<article>\n  <h1>'African tribe' evicted from Scottish Borders camp</h1>\n  <figure>\n    <img src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/e325/live/044bf1b0-9f63-11f0-ab23-87e36f512d0a.jpg.webp\" alt=\"BBC Two police vans and officers standing in the road.\">\n    <figcaption>BBC</figcaption>\n  </figure>\n  <p>Police have evicted members of a self-styled African tribe who had set up camp on council-owned land in the Scottish Borders.</p>\n  <p>The three people, who call themselves the Kingdom of Kubala, have been staying in a wooded area near Jedburgh.</p>\n  <p>The tribe streamed the eviction live on TikTok on Thursday morning.</p>\n  <p>The group had previously said they were reclaiming land that was stolen from their ancestors 400 years ago and refused to recognise the powers of the courts to evict them.</p>\n  <p>Police and immigration enforcement officials moved into the camp at about 08:00 and detained the members of the group.</p>\n  <p>Ghanaian Kofi Offeh, 36, and Jean Gasho, 43, who is originally from Zimbabwe, first arrived in the area in the spring.</p>\n  <p>Describing themselves as King Atehehe and Queen Nandi, they set up camp on a hillside above the town in the Scottish Borders.</p>\n  <p>They were joined by \"handmaiden\" Kaura Taylor, from Texas, who calls herself Asnat.</p>\n  <p>On Wednesday, they were banned from moving back to private land they had already been evicted from.</p>\n  <p>They were not at the hearing at Selkirk Sheriff Court when Sheriff Peter Paterson awarded expenses against them.</p>\n  <p>Last week, the court granted an order banning them from any council land in the area, including where they had been staying.</p>\n</article>
""".trimIndent()
        // 使用 extension function 转换为标准 HTML
        val htmlContent = remember { jsonHtml.fromJsonHtml().wrapHtml() }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val articleData = parseHtmlToArticle(
                html = htmlContent,
                baseUri = "https://www.bbc.com/"
            )
            ArticleView(article = articleData)
        }
    }
}