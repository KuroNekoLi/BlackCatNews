# Firebase 與部署完整指南

> **BlackCatNews 專案** - Kotlin Multiplatform 跨平台應用程式  
> **最後更新**：2025-01-21

---

## 📚 目錄

1. [專案概述](#專案概述)
2. [Firebase 設定](#firebase-設定)
    - [雙平台配置](#雙平台配置)
    - [Android 設定](#android-設定)
    - [iOS 設定](#ios-設定)
3. [Firebase 功能測試](#firebase-功能測試)
4. [常見問題與修復](#常見問題與修復)
5. [GitHub Actions CI/CD](#github-actions-cicd)
6. [Google Play 部署](#google-play-部署)
7. [參考資源](#參考資源)

---

## 專案概述

### 技術架構

- **平台**：Kotlin Multiplatform (KMP)
- **UI 框架**：Compose Multiplatform
- **支援平台**：Android、iOS
- **Firebase SDK**：GitLive Firebase Kotlin SDK（跨平台）

### 目前使用的 Firebase 功能

| 功能                          | 用途      | 狀態    |
|-----------------------------|---------|-------|
| **Firebase Analytics**      | 使用者行為分析 | ✅ 已啟用 |
| **Firebase Crashlytics**    | 當機報告    | ✅ 已啟用 |
| **Firebase Authentication** | 使用者認證   | ✅ 已啟用 |

---

## Firebase 設定

### 雙平台配置

在 Firebase Console 中，你需要為每個平台和環境創建獨立的應用程式：

| 平台      | 環境      | Package/Bundle ID              | Firebase 昵稱                   |
|---------|---------|--------------------------------|-------------------------------|
| Android | Debug   | `com.linli.blackcatnews.debug` | BlackCatNews (Debug)          |
| Android | Release | `com.linli.blackcatnews`       | BlackCatNews (Production)     |
| iOS     | Debug   | `com.linli.blackcatnews.debug` | BlackCatNews iOS (Debug)      |
| iOS     | Release | `com.linli.blackcatnews`       | BlackCatNews iOS (Production) |

### Android 設定

#### 1. 在 Firebase Console 添加 Android 應用程式

**Debug 版本：**

- **套件名稱**：`com.linli.blackcatnews.debug`
- **應用程式昵稱**：BlackCatNews (Debug)
- **SHA-1 簽名**：
  ```bash
  ./gradlew :composeApp:signingReport
  # 從輸出中複製 Debug 的 SHA-1
  ```

**Release 版本：**

- **套件名稱**：`com.linli.blackcatnews`
- **應用程式昵稱**：BlackCatNews (Production)
- **SHA-1 簽名**：
  ```bash
  keytool -list -v -keystore /path/to/upload-keystore.jks -alias upload
  # 輸入密碼後複製 SHA-1
  ```

#### 2. 下載並放置配置檔案

```
composeApp/
├── src/
│   ├── debug/
│   │   └── google-services.json          # Debug 配置
│   └── release/
│       └── google-services.json          # Release 配置
```

**重要**：Google Services 插件會在以下路徑尋找：

- `src/debug/google-services.json`（Debug）
- `src/release/google-services.json`（Release）

#### 3. Gradle 配置

在 `gradle/libs.versions.toml` 中：

```toml
[versions]
firebase-gitlive = "2.1.0"
google-services = "4.4.0"
firebase-crashlytics = "3.0.6"

[libraries]
gitlive-firebase-app = { module = "dev.gitlive:firebase-app", version.ref = "firebase-gitlive" }
gitlive-firebase-auth = { module = "dev.gitlive:firebase-auth", version.ref = "firebase-gitlive" }
gitlive-firebase-analytics = { module = "dev.gitlive:firebase-analytics", version.ref = "firebase-gitlive" }
gitlive-firebase-crashlytics = { module = "dev.gitlive:firebase-crashlytics", version.ref = "firebase-gitlive" }

[plugins]
googleServices = { id = "com.google.gms.google-services", version.ref = "google-services" }
firebaseCrashlytics = { id = "com.google.firebase.crashlytics", version.ref = "firebase-crashlytics" }
```

在 `composeApp/build.gradle.kts` 中：

```kotlin
plugins {
    // ... 其他 plugins
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // GitLive Firebase Kotlin SDK - 跨平台
            implementation(libs.gitlive.firebase.app)        // ✅ 核心依賴，必須包含
            implementation(libs.gitlive.firebase.auth)       // 使用者認證
            implementation(libs.gitlive.firebase.analytics)  // 分析統計
            implementation(libs.gitlive.firebase.crashlytics) // 當機報告
        }
    }
}
```

**重要提醒**：

- ✅ `firebase-app` 是核心依賴，**絕對不能註解掉**
- ✅ 必須同時添加 Google Services 和 Crashlytics 兩個插件

### iOS 設定

#### 1. 在 Firebase Console 添加 iOS 應用程式

**Debug 版本：**

- **軟體包 ID**：`com.linli.blackcatnews.debug`
- **應用程式昵稱**：BlackCatNews iOS (Debug)
- **App Store ID**：留空

**Release 版本：**

- **軟體包 ID**：`com.linli.blackcatnews`
- **應用程式昵稱**：BlackCatNews iOS (Production)
- **App Store ID**：上架後填寫

#### 2. 下載並添加配置檔案

```
iosApp/
└── iosApp/
    ├── GoogleService-Info-Debug.plist    # Debug 配置
    └── GoogleService-Info-Release.plist  # Release 配置
```

#### 3. 在 Xcode 中配置動態載入

在 `iOSApp.swift` 中：

```swift
import SwiftUI
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        configureFirebase()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }

    private func configureFirebase() {
        #if DEBUG
        // Debug build: 使用 debug 配置
        if let filePath = Bundle.main.path(forResource: "GoogleService-Info-Debug", ofType: "plist"),
           let options = FirebaseOptions(contentsOfFile: filePath) {
            FirebaseApp.configure(options: options)
        }
        #else
        // Release build: 使用 production 配置
        if let filePath = Bundle.main.path(forResource: "GoogleService-Info-Release", ofType: "plist"),
           let options = FirebaseOptions(contentsOfFile: filePath) {
            FirebaseApp.configure(options: options)
        }
        #endif
    }
}
```

#### 4. 使用 Swift Package Manager 添加 Firebase iOS SDK

本專案**不使用 CocoaPods**，改用 SPM（Swift Package Manager）：

1. 在 Xcode 中選擇 `File` → `Add Package Dependencies...`
2. 輸入網址：
   ```
   https://github.com/firebase/firebase-ios-sdk
   ```
3. 選擇需要的產品：
    - ✅ `FirebaseAuth`
    - ✅ `FirebaseAnalytics`
    - ✅ `FirebaseCrashlytics`
4. Target 選擇 `iosApp`

#### 5. 打開 iOS 專案的正確方式

```bash
# ✅ 正確方式（使用 .xcodeproj）
open iosApp/iosApp.xcodeproj

# ❌ 錯誤方式（.xcworkspace 已刪除）
# open iosApp/iosApp.xcworkspace
```

**原因**：專案已移除 CocoaPods，不再使用 `.xcworkspace`

---

## Firebase 功能測試

### 內建測試功能

專案中已經內建 Firebase 測試界面，可以測試所有功能：

**進入測試界面：**

```
應用程式 → 設定（底部導航欄）→ 開發者選項 → "🔥 Firebase 功能測試"
```

### 測試項目

#### 1. Analytics 測試

| 測試按鈕     | 功能          |
|----------|-------------|
| 記錄簡單事件   | 測試基本事件追蹤    |
| 記錄帶參數的事件 | 測試帶有自訂參數的事件 |
| 設定使用者屬性  | 測試使用者屬性設定   |

**查看結果：**

- 實時：Firebase Console → Analytics → DebugView（需啟用 Debug 模式）
- 一般：Firebase Console → Analytics → Events（2-4 小時後）

#### 2. Authentication 測試

| 測試按鈕    | 功能        |
|---------|-----------|
| 匿名登入    | 測試匿名使用者創建 |
| 檢查目前使用者 | 查看登入狀態    |
| 登出      | 測試登出功能    |

**查看結果：**

- Firebase Console → Authentication → Users（立即顯示）

#### 3. Crashlytics 測試

| 測試按鈕    | 功能                 |
|---------|--------------------|
| 記錄非致命錯誤 | 測試異常報告             |
| 設定自訂鍵值  | 測試自訂元資料            |
| 記錄自訂日誌  | 測試日誌記錄             |
| ⚠️ 強制當機 | 測試當機報告（會導致 App 當機） |

**查看結果：**

- Firebase Console → Crashlytics（5-10 分鐘後）

### 啟用 Debug 模式

**Android：**

```bash
# 啟用 Analytics Debug 模式
adb shell setprop debug.firebase.analytics.app com.linli.blackcatnews.debug

# 停用
adb shell setprop debug.firebase.analytics.app .none.
```

**iOS：**
在 Xcode Scheme 中添加啟動參數：

```
-FIRAnalyticsDebugEnabled
```

---

## 常見問題與修復

### 問題 1：Android Crashlytics 無法使用

**錯誤訊息：**

```
FirebaseApp is not initialized in this process
```

**原因：**
缺少 Google Services 插件或 Crashlytics 插件。

**解決方案：**
確認 `composeApp/build.gradle.kts` 中已添加：

```kotlin
plugins {
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
}
```

### 問題 2：GitHub Actions 找不到 podInstall 任務

**錯誤訊息：**

```
Cannot locate tasks that match ':composeApp:podInstall'
```

**原因：**
專案已移除 CocoaPods，但 CI/CD 配置未更新。

**解決方案：**
修改 `.github/workflows/ios.yml`：

```yaml
# ❌ 錯誤
- name: 設定 iOS Framework
  run: ./gradlew :composeApp:podInstall

# ✅ 正確
- name: 構建 Kotlin Multiplatform Framework
  run: ./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
```

### 問題 3：Google Play 上傳失敗（AD_ID 權限）

**錯誤訊息：**

```
This release includes the com.google.android.gms.permission.AD_ID permission 
but your declaration on Play Console says your app doesn't use advertising ID.
```

**原因：**
Firebase Analytics 會自動添加 AD_ID 權限，但 Play Console 中聲明不使用廣告。

**解決方案 A：移除 AD_ID 權限（不使用廣告）**

在 `composeApp/src/androidMain/AndroidManifest.xml` 中：

```xml

<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- 明確移除廣告 ID 權限 -->
    <uses-permission android:name="com.google.android.gms.permission.AD_ID" tools:node="remove" />

</manifest>
```

**解決方案 B：在 Play Console 中聲明使用廣告（使用廣告）**

1. 前往 [Google Play Console](https://play.google.com/console/)
2. 選擇應用程式 → **政策** → **應用程式內容**
3. 找到 **廣告 ID** 區塊 → 點擊「管理」
4. 回答問題：
    - **是否使用廣告 ID**：選擇「是」
    - **使用目的**：勾選「廣告或行銷」、「分析」
5. 儲存並提交

同時需要更新隱私權政策說明廣告 ID 的使用。

---

## GitHub Actions CI/CD

### Android 部署

**檔案位置**：`.github/workflows/android-gpp.yml`

**主要步驟：**

1. 檢出程式碼
2. 設定 Java 環境
3. 設定環境變數（Keystore、Play Console 憑證）
4. 建立 Release AAB
5. 上傳至 Google Play（Internal Testing）

**環境變數需求：**

| 變數名稱                       | 說明                | 來源                    |
|----------------------------|-------------------|-----------------------|
| `UPLOAD_KEYSTORE`          | Keystore 檔案路徑     | `keystore.properties` |
| `UPLOAD_KEYSTORE_PASSWORD` | Keystore 密碼       | GitHub Secrets        |
| `UPLOAD_KEY_ALIAS`         | Key 別名            | `keystore.properties` |
| `UPLOAD_KEY_PASSWORD`      | Key 密碼            | GitHub Secrets        |
| `PLAY_CREDENTIALS_JSON`    | Play Console 服務帳號 | GitHub Secrets        |

### iOS 部署

**檔案位置**：`.github/workflows/ios.yml`

**主要步驟：**

1. 檢出程式碼
2. 設定 Xcode 環境
3. 構建 Kotlin Framework（使用 `embedAndSignAppleFrameworkForXcode`）
4. 建立 iOS Archive
5. 上傳至 App Store Connect

**重要**：

- ✅ 使用 `embedAndSignAppleFrameworkForXcode` 而非 `podInstall`
- ✅ 直接打開 `.xcodeproj` 而非 `.xcworkspace`

---

## Google Play 部署

### 本地建立與測試

#### Debug 版本

```bash
# 建立 Debug APK
./gradlew :composeApp:assembleDebug

# 安裝到裝置
./gradlew :composeApp:installDebug
```

#### Release 版本

```bash
# 建立 Release AAB（推薦）
./gradlew :composeApp:bundleRelease

# 建立 Release APK
./gradlew :composeApp:assembleRelease

# 重新安裝 Release（解決簽名衝突）
./gradlew reinstallRelease

# 安裝並啟動 Release
./gradlew installAndRunRelease
```

### 上傳至 Play Console

#### 方式 1：使用 Gradle Plugin（自動化）

```bash
# 上傳至 Internal Testing
./gradlew publishReleaseBundle --track=internal

# 上傳至 Alpha
./gradlew publishReleaseBundle --track=alpha

# 上傳至 Beta
./gradlew publishReleaseBundle --track=beta

# 上傳至 Production
./gradlew publishReleaseBundle --track=production
```

**配置**（`composeApp/build.gradle.kts`）：

```kotlin
play {
    serviceAccountCredentials.set(file("${rootDir}/play-credentials.json"))
    defaultToAppBundles.set(true)
    track.set(
        project.findProperty("play.track")?.toString()
            ?: System.getenv("PLAY_TRACK")
            ?: "internal"
    )
}
```

#### 方式 2：手動上傳

1. 前往 [Google Play Console](https://play.google.com/console/)
2. 選擇應用程式 → **測試** → **內部測試**
3. 創建新版本 → 上傳 AAB
4. 填寫版本資訊 → 儲存 → 審查 → 推出

### 版本管理

**版本號配置**（透過環境變數或預設值）：

```kotlin
defaultConfig {
    versionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 2
    versionName = System.getenv("VERSION_NAME") ?: "1.0"
}
```

**在 CI/CD 中設定版本：**

```yaml
env:
  VERSION_CODE: ${{ github.run_number }}
  VERSION_NAME: "1.0.${{ github.run_number }}"
```

---

## 參考資源

### 官方文件

- [Firebase 文件](https://firebase.google.com/docs)
- [GitLive Firebase Kotlin SDK](https://github.com/GitLiveApp/firebase-kotlin-sdk)
- [Google Play Console](https://play.google.com/console/)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)

### 政策與規範

- [Google Play 政策中心](https://support.google.com/googleplay/android-developer/topic/9858052)
- [廣告 ID 政策](https://support.google.com/googleplay/android-developer/answer/6048248)
- [使用者資料政策](https://support.google.com/googleplay/android-developer/answer/10787469)

### 相關工具

- [bundletool](https://github.com/google/bundletool) - AAB 檢查工具
- [Firebase CLI](https://firebase.google.com/docs/cli) - Firebase 命令列工具
- [Fastlane](https://fastlane.tools/) - iOS/Android 自動化工具

---

## 附錄

### A. CocoaPods 移除說明

本專案**已完全移除 CocoaPods**，改用以下方式：

| 舊方式（CocoaPods） | 新方式（直接整合）                            |
|----------------|--------------------------------------|
| Podfile        | ❌ 已刪除                                |
| Pods/          | ❌ 已刪除                                |
| .xcworkspace   | ❌ 已刪除                                |
| podInstall 任務  | ✅ embedAndSignAppleFrameworkForXcode |
| .xcodeproj     | ✅ 直接使用                               |

**優勢：**

- ✅ 更簡單，減少依賴管理工具
- ✅ 建立速度更快
- ✅ 避免版本衝突
- ✅ 與 Kotlin Multiplatform 整合更好

### B. Firebase 配置檔案是否需要加入 .gitignore？

**建議**：視專案性質而定

| 專案類型     | 建議    | 原因                              |
|----------|-------|---------------------------------|
| 個人/小團隊專案 | 可以不加入 | 方便開發，Firebase 有內建保護             |
| 企業/敏感專案  | 建議加入  | 額外的安全層                          |
| 開源專案     | 可以不加入 | Firebase 安全性由 Security Rules 控制 |

**Firebase 的安全機制：**

- ✅ Security Rules（Firestore、Storage）
- ✅ SHA-1 指紋驗證
- ✅ 套件名稱/Bundle ID 驗證
- ✅ API Key 限制

**真正必須加入 .gitignore 的：**

- ✅ `keystore.properties`（簽名密鑰配置）
- ✅ `*.jks`（Android 簽名密鑰）
- ✅ `play-credentials.json`（Play Console 服務帳號）
- ✅ 服務器端的 Service Account JSON

### C. 目錄結構

```
BlackCatNews/
├── .github/
│   └── workflows/
│       ├── android-gpp.yml           # Android CI/CD
│       └── ios.yml                   # iOS CI/CD
├── composeApp/
│   ├── src/
│   │   ├── androidMain/
│   │   │   ├── AndroidManifest.xml
│   │   │   └── kotlin/...
│   │   ├── debug/
│   │   │   └── google-services.json  # Android Debug 配置
│   │   ├── release/
│   │   │   └── google-services.json  # Android Release 配置
│   │   ├── iosMain/
│   │   │   └── kotlin/...
│   │   └── commonMain/
│   │       └── kotlin/...
│   ├── build.gradle.kts
│   └── keystore.properties           # 不提交到 Git
├── iosApp/
│   └── iosApp/
│       ├── GoogleService-Info-Debug.plist    # iOS Debug 配置
│       ├── GoogleService-Info-Release.plist  # iOS Release 配置
│       └── iOSApp.swift
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── play-credentials.json             # 不提交到 Git
└── Firebase_與_部署完整指南.md        # 本文件
```

---

**文件維護者**：BlackCatNews 開發團隊  
**最後更新日期**：2025-01-21  
**版本**：1.0

如有問題或建議，請在專案中提出 Issue。
