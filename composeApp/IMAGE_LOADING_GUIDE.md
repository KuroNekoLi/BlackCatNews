# 圖片加載問題解決方案

## 問題描述

在 iOS 平台上使用 Coil 加載網絡圖片時遇到錯誤：
```
kotlin.native.internal.FileFailedToInitializeException: 
There was an error during file or class initialization
```

## 原因分析

Coil 在 Kotlin Multiplatform 中需要特定平台的 HTTP 客戶端引擎：
- **Android**: 使用 OkHttp 引擎
- **iOS**: 需要使用 Darwin 引擎（基於 NSURLSession）

## 解決方案

### 1. 添加 iOS 平台的 Ktor 客戶端依賴

#### gradle/libs.versions.toml
```toml
[libraries]
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }
```

#### composeApp/build.gradle.kts
```kotlin
sourceSets {
    androidMain.dependencies {
        implementation(libs.ktor.client.okhttp)  // Android 使用 OkHttp
    }
    
    iosMain.dependencies {
        implementation(libs.ktor.client.darwin)  // iOS 使用 Darwin
    }
    
    commonMain.dependencies {
        implementation(libs.ktor.client.core)    // 通用核心
        implementation(libs.coil.compose)
        implementation(libs.coil.network)
    }
}
```

### 2. 添加錯誤處理和佔位符

在 `NewsCard.kt` 中：

```kotlin
AsyncImage(
    model = newsItem.imageUrl,
    contentDescription = newsItem.title,
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .clip(MaterialTheme.shapes.medium),
    contentScale = ContentScale.Crop,
    onError = { error ->
        // 圖片加載失敗時的處理
        println("圖片加載失敗: ${newsItem.imageUrl}")
    }
)
```

### 3. 使用佔位符（當圖片不可用時）

```kotlin
if (newsItem.imageUrl != null) {
    AsyncImage(...)
} else {
    // 顯示佔位符
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("📰", style = MaterialTheme.typography.displayLarge)
    }
}
```

## 測試策略

### 階段 1：使用佔位符（當前）
- 暫時將所有 `imageUrl` 設為 `null`
- 驗證 UI 佈局正常
- 確保佔位符正確顯示

```kotlin
NewsItem(
    id = "1",
    title = "新聞標題",
    imageUrl = null,  // 暫時使用佔位符
    // ...
)
```

### 階段 2：使用本地圖片資源
```kotlin
// 未來可以使用本地資源
imageUrl = "compose.resource://drawable/news_placeholder.png"
```

### 階段 3：啟用網絡圖片
當 Coil + Ktor 配置正確後：
```kotlin
imageUrl = "https://example.com/image.jpg"
```

## Coil 配置驗證

確保在 `App.kt` 中正確初始化：

```kotlin
@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .crossfade(true)
            .logger(coil3.util.DebugLogger())  // 啟用調試日誌
            .components {
                add(KtorNetworkFetcherFactory())  // 使用 Ktor 網絡引擎
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
                    .maxSizeBytes(512L * 1024 * 1024)
                    .build()
            }
            .build()
    }
    
    AppTheme {
        AppNavigation()
    }
}
```

## 常見問題

### Q: 為什麼 iOS 需要 Darwin 引擎？
**A**: iOS 不支持 OkHttp（JVM 庫）。Darwin 引擎使用 Apple 原生的 NSURLSession API。

### Q: 如何調試圖片加載問題？
**A**: 
1. 啟用 Coil 的 DebugLogger
2. 檢查 Logcat/Console 輸出
3. 使用 `onError` 回調捕獲錯誤

### Q: 可以使用其他圖片庫嗎？
**A**: 可以考慮：
- Kamel（專為 KMP 設計）
- 自定義實現（使用平台特定 API）

## 依賴版本

確保使用兼容的版本：
```toml
[versions]
ktor = "3.3.0"
coil = "3.0.4"
```

## 下一步

1. ✅ 添加 Darwin 引擎依賴
2. ✅ 實現錯誤處理和佔位符
3. ✅ 暫時使用 null 圖片 URL
4. ⏳ 測試 iOS 平台運行
5. ⏳ 逐步啟用網絡圖片
6. ⏳ 添加加載動畫和進度指示器

## 參考資料

- [Coil KMP Documentation](https://coil-kt.github.io/coil/upgrading_to_coil3/#multiplatform)
- [Ktor Client Engines](https://ktor.io/docs/http-client-engines.html)
- [iOS NSURLSession](https://developer.apple.com/documentation/foundation/nsurlsession)
