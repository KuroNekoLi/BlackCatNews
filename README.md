# BlackCatNews - AI 雙語新聞學習應用程式

> **Kotlin Multiplatform 跨平台專案**  
> 支援 Android 與 iOS 雙平台

---

## 📱 專案簡介

BlackCatNews 是一個創新的雙語新聞學習應用程式，幫助使用者透過閱讀新聞提升語言能力。

### 核心功能

- 📰 雙語新聞瀏覽（繁體中文 / English）
- 🔍 新聞搜尋與分類
- 💾 離線收藏功能
- 📊 學習進度追蹤
- 🔐 使用者帳號系統

### 技術架構

| 技術           | 版本 / 說明                           |
|--------------|-----------------------------------|
| **開發語言**     | Kotlin 2.2.20                     |
| **UI 框架**    | Compose Multiplatform 1.9.0       |
| **平台支援**     | Android (API 24+)、iOS (14.0+)     |
| **網路請求**     | Ktor 3.3.0                        |
| **資料庫**      | Room 2.8.1 + SQLite               |
| **依賴注入**     | Koin 4.1.1                        |
| **Firebase** | GitLive Firebase Kotlin SDK 2.1.0 |

---

## 🚀 快速開始

### 環境需求

- **JDK**: 11 或以上
- **Android Studio**: Koala (2024.1.1) 或以上
- **Xcode**: 15.0 或以上（僅 iOS 開發需要）
- **macOS**: 13.0 或以上（僅 iOS 開發需要）

### 專案結構

```
BlackCatNews/
├── composeApp/          # 主應用程式模組
│   ├── src/
│   │   ├── androidMain/       # Android 特定程式碼
│   │   ├── iosMain/           # iOS 特定程式碼
│   │   ├── commonMain/        # 共用程式碼
│   │   ├── debug/             # Debug 配置檔案
│   │   └── release/           # Release 配置檔案
│   └── build.gradle.kts
├── iosApp/              # iOS 應用程式（SwiftUI）
│   └── iosApp/
│       └── iosApp.xcodeproj
├── shared/              # 共用邏輯模組
├── docs/                # 文檔目錄
│   ├── features/        # 功能說明文檔
│   └── iOS_CICD問題排除.md
├── Firebase_與_部署完整指南.md    # Firebase 設定與 CI/CD
├── 開發者指南.md                  # 開發者技術文檔
└── GOOGLE_PLAY_AD_ID_FIX.md      # Google Play 問題修復
```

### 建立與執行

#### Android

```bash
# Debug 版本
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug

# Release 版本
./gradlew :composeApp:assembleRelease

# 重新安裝 Release（解決簽名衝突）
./gradlew reinstallRelease

# 安裝並啟動
./gradlew installAndRunRelease
```

#### iOS

```bash
# 1. 建立 Kotlin Framework
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode

# 2. 打開 Xcode 專案
open iosApp/iosApp.xcodeproj

# 3. 在 Xcode 中選擇 scheme 並執行
```

**重要**：

- ✅ 使用 `iosApp.xcodeproj` 而非 `.xcworkspace`
- ✅ 專案已移除 CocoaPods，改用 SPM
- ❌ 不要執行 `pod install`

---

## 🔥 Firebase 設定

詳細的 Firebase 設定步驟請參考：**[Firebase_與_部署完整指南.md](./Firebase_與_部署完整指南.md)**

### 快速摘要

#### 1. Firebase Console 設定

在 Firebase Console 建立 **4 個應用程式**：

| 平台      | 環境      | Package/Bundle ID              |
|---------|---------|--------------------------------|
| Android | Debug   | `com.linli.blackcatnews.debug` |
| Android | Release | `com.linli.blackcatnews`       |
| iOS     | Debug   | `com.linli.blackcatnews.debug` |
| iOS     | Release | `com.linli.blackcatnews`       |

#### 2. 配置檔案位置

**Android：**

```
composeApp/src/debug/google-services.json
composeApp/src/release/google-services.json
```

**iOS：**

```
iosApp/iosApp/GoogleService-Info-Debug.plist
iosApp/iosApp/GoogleService-Info-Release.plist
```

#### 3. 使用的 Firebase 功能

- ✅ **Analytics** - 使用者行為分析
- ✅ **Crashlytics** - 當機報告
- ✅ **Authentication** - 使用者認證（匿名登入）

### Firebase 測試

應用程式內建測試界面：

```
應用程式 → 設定 → 開發者選項 → 🔥 Firebase 功能測試
```

可測試：

- Analytics 事件記錄
- Authentication 匿名登入
- Crashlytics 當機報告

---

## 🔐 簽名設定

### 本地開發

建立 `composeApp/keystore.properties`：

```properties
keystore.path=/path/to/your/upload-keystore.jks
keystore.password=your_keystore_password
key.alias=upload
key.password=your_key_password
```

**重要**：此檔案已加入 `.gitignore`，不會提交到 Git

### CI/CD 環境變數

在 GitHub Secrets 中設定：

| 變數名稱                       | 說明                     |
|----------------------------|------------------------|
| `UPLOAD_KEYSTORE`          | Keystore 檔案（Base64 編碼） |
| `UPLOAD_KEYSTORE_PASSWORD` | Keystore 密碼            |
| `UPLOAD_KEY_ALIAS`         | Key 別名                 |
| `UPLOAD_KEY_PASSWORD`      | Key 密碼                 |
| `PLAY_CREDENTIALS_JSON`    | Play Console 服務帳號 JSON |

---

## 🤖 GitHub Actions CI/CD

### Android 自動部署

**檔案**：`.github/workflows/android-gpp.yml`

**觸發條件**：

- Push 到 `main` 分支
- 手動觸發

**流程**：

1. 檢出程式碼
2. 設定 Java 環境
3. 建立 Release AAB
4. 上傳至 Google Play Internal Testing

### iOS 自動部署

**檔案**：`.github/workflows/ios.yml`

**流程**：

1. 檢出程式碼
2. 設定 Xcode 環境
3. 建立 Kotlin Framework（**不使用** `podInstall`）
4. 建立 iOS Archive
5. 上傳至 App Store Connect

**重要更新**：

- ✅ 使用 `embedAndSignAppleFrameworkForXcode`
- ❌ 已移除 `podInstall` 任務

---

## 📦 Google Play 部署

### 手動上傳

```bash
# 建立 AAB
./gradlew :composeApp:bundleRelease

# 檔案位置
composeApp/build/outputs/bundle/release/composeApp-release.aab
```

### 自動上傳（使用 Gradle Plugin）

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

---

## ⚠️ 常見問題

### 1. Android Crashlytics 無法運作

**症狀**：`FirebaseApp is not initialized`

**解決方案**：
確認已添加兩個必要插件：

```kotlin
plugins {
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)  // 必須！
}
```

### 2. GitHub Actions 找不到 podInstall

**症狀**：`Cannot locate tasks that match ':composeApp:podInstall'`

**解決方案**：
專案已移除 CocoaPods，更新 `.github/workflows/ios.yml`：

```yaml
# ✅ 正確
run: ./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
```

### 3. Google Play 上傳失敗（AD_ID 權限）

**症狀**：應用程式包含 AD_ID 權限但未在 Play Console 聲明

**解決方案**：
在 `AndroidManifest.xml` 中明確移除：

```xml
<uses-permission 
    android:name="com.google.android.gms.permission.AD_ID"
    tools:node="remove" />
```

詳細說明請參考：**[GOOGLE_PLAY_AD_ID_FIX.md](./GOOGLE_PLAY_AD_ID_FIX.md)**

### 4. iOS 無法開啟 .xcworkspace

**症狀**：找不到 `.xcworkspace` 檔案

**解決方案**：
專案已移除 CocoaPods，改用 `.xcodeproj`：

```bash
# ✅ 正確
open iosApp/iosApp.xcodeproj

# ❌ 錯誤
open iosApp/iosApp.xcworkspace  # 此檔案已刪除
```

---

## 📚 文檔索引

### 核心文檔

| 文件                                                         | 說明                       | 適用對象      |
|------------------------------------------------------------|--------------------------|-----------|
| **[README.md](./README.md)**                               | 專案總覽（本文件）                | 所有人       |
| **[Firebase_與_部署完整指南.md](./Firebase_與_部署完整指南.md)**         | Firebase 設定、CI/CD、部署完整指南 | DevOps、後端 |
| **[開發者指南.md](./開發者指南.md)**                                 | 架構、導航、UI、資料層開發文檔         | 前端開發者     |
| **[GOOGLE_PLAY_AD_ID_FIX.md](./GOOGLE_PLAY_AD_ID_FIX.md)** | Google Play AD_ID 權限問題修復 | 發布管理員     |

### 功能文檔

| 文件                                                       | 說明          |
|----------------------------------------------------------|-------------|
| **[docs/features/雙語文章功能.md](./docs/features/雙語文章功能.md)** | 雙語學習新聞詳細頁功能 |
| **[docs/features/測驗功能.md](./docs/features/測驗功能.md)**     | 閱讀測驗功能說明    |

### 問題排除

| 文件                                                 | 說明                          |
|----------------------------------------------------|-----------------------------|
| **[docs/iOS_CICD問題排除.md](./docs/iOS_CICD問題排除.md)** | iOS CI/CD 警告與 Keychain 錯誤排除 |

---

## 🛠️ 開發工具

### 推薦 IDE 設定

**Android Studio / IntelliJ IDEA：**

- Kotlin Plugin
- Compose Multiplatform IDE Support
- Kotlin Multiplatform Mobile

**Xcode：**

- Swift 5.9+
- iOS Deployment Target: 14.0+

### 實用指令

```bash
# 清理建立
./gradlew clean

# 檢查依賴
./gradlew :composeApp:dependencies

# 檢查簽名資訊
./gradlew :composeApp:signingReport

# 同步 Gradle
./gradlew --refresh-dependencies
```

---

## 🔄 版本管理

### 版本號規則

- **versionCode**：每次發布自動遞增
- **versionName**：語意化版本 (Semantic Versioning)
  - 格式：`主版本.次版本.修訂版本`
  - 範例：`1.0.0`、`1.1.0`、`1.1.1`

### 在 CI/CD 中設定版本

```yaml
env:
  VERSION_CODE: ${{ github.run_number }}
  VERSION_NAME: "1.0.${{ github.run_number }}"
```

---

## 🤝 貢獻指南

### 分支策略

- `main` - 穩定版本分支
- `develop` - 開發分支
- `feature/*` - 功能分支
- `hotfix/*` - 緊急修復分支

### Pull Request 流程

1. Fork 專案
2. 建立功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交變更 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 開啟 Pull Request

### 程式碼規範

- 使用 Kotlin 官方程式碼風格
- 所有公開 API 必須有註解
- 提交訊息使用英文，格式：`[類型] 簡短說明`
  - 類型：`feat`, `fix`, `docs`, `refactor`, `test`, `chore`
  - 範例：`feat: 新增雙語切換功能`

---

## 📄 授權條款

本專案採用 MIT 授權條款 - 詳見 [LICENSE](LICENSE) 檔案

---

## 📞 聯絡資訊

- **專案維護者**：BlackCatNews 開發團隊
- **問題回報**：請在 GitHub Issues 中提出
- **功能建議**：歡迎在 Discussions 中討論

---

## 🙏 致謝

感謝以下開源專案：

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [GitLive Firebase Kotlin SDK](https://github.com/GitLiveApp/firebase-kotlin-sdk)
- [Ktor](https://ktor.io/)
- [Koin](https://insert-koin.io/)
- [Coil](https://coil-kt.github.io/coil/)

---

**最後更新**：2025-01-21  
**版本**：1.0.0
