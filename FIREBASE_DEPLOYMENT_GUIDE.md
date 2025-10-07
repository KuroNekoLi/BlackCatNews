# BlackCatNews Firebase App Distribution 測試指南

本指南將協助您使用 Firebase App Distribution 快速分發測試版本給測試者。

## 📋 目錄

- [什麼是 Firebase App Distribution](#什麼是-firebase-app-distribution)
- [優勢比較](#優勢比較)
- [前置準備](#前置準備)
- [設定 Firebase 專案](#設定-firebase-專案)
- [整合 Firebase SDK](#整合-firebase-sdk)
- [使用 Firebase CLI 上傳](#使用-firebase-cli-上傳)
- [使用 Gradle 插件上傳](#使用-gradle-插件上傳)
- [邀請測試者](#邀請測試者)
- [收集回饋](#收集回饋)
- [常見問題](#常見問題)

---

## 🎯 什麼是 Firebase App Distribution

Firebase App Distribution 是 Google 提供的應用程式測試分發平台，讓您能夠快速將 App 的測試版本分發給測試者。

### 主要特點

- ⚡ **快速發布**：無需審核，上傳後立即可用
- 👥 **靈活管理**：輕鬆新增/移除測試者
- 📱 **跨平台**：支援 Android 和 iOS
- 📊 **回饋收集**：內建回饋機制
- 🔔 **自動通知**：測試者會收到新版本通知
- 🆓 **免費使用**：Firebase Spark 方案即可使用

---

## ⚖️ 優勢比較

### Firebase App Distribution vs Google Play 內部測試

| 功能 | Firebase App Distribution | Play Console 內部測試 |
|------|---------------------------|---------------------|
| **審核時間** | 無需審核，立即可用 | 無需審核，但需上傳處理 |
| **測試者數量** | 無限制 | 最多 100 位 |
| **設定難度** | 簡單 | 簡單 |
| **回饋收集** | ✅ 內建 | ❌ 需透過其他方式 |
| **iOS 支援** | ✅ 支援 | ❌ 不支援 |
| **發布速度** | ⚡ 最快（幾秒鐘） | 🐢 較慢（需處理 AAB） |
| **適用階段** | 開發、Alpha 測試 | Beta 測試、預發布 |

### 建議使用情境

#### 使用 Firebase App Distribution 當：

- 🔨 **開發階段**：快速迭代，頻繁更新
- 👨‍💻 **內部測試**：團隊成員測試
- 🧪 **Alpha 測試**：少量外部測試者
- 🚀 **快速驗證**：需要立即測試新功能

#### 使用 Google Play 測試軌道當：

- 📦 **Beta 測試**：準備上架前的大規模測試
- 🌍 **公開測試**：開放給更多使用者
- 🎯 **預發布**：接近正式版本

---

## 🎯 前置準備

### 1. 需要的工具

- ✅ Google 帳號（免費）
- ✅ Firebase 專案（免費）
- ✅ Firebase CLI（命令列工具）
- ✅ 已建置的 APK 檔案

### 2. 費用

- 💰 **完全免費**（使用 Firebase Spark 方案）

---

## 🔧 設定 Firebase 專案

### 3. 建立 Firebase 專案

1. 前往 [Firebase Console](https://console.firebase.google.com/)
2. 點擊「新增專案」或「Add project」
3. 輸入專案名稱：`BlackCatNews`
4. （選填）啟用 Google Analytics
5. 選擇 Analytics 帳戶或建立新帳戶
6. 點擊「建立專案」
7. 等待專案建立完成

### 4. 新增 Android 應用程式

1. 在 Firebase 專案中，點擊「新增應用程式」圖示（Android）
2. 填寫應用程式資訊：
    - **Android 套件名稱**：`com.linli.blackcatnews`（必須與您的 applicationId 相同）
    - **應用程式暱稱**（選填）：BlackCatNews
    - **Debug 簽署憑證 SHA-1**（選填）：可暫時跳過
3. 點擊「註冊應用程式」

### 5. 下載設定檔

1. 下載 `google-services.json` 檔案
2. 將檔案放置到：
   ```
   composeApp/
   ```
   也就是與 `build.gradle.kts` 同一層目錄

### 6. 啟用 App Distribution

1. 在 Firebase Console 左側選單中，展開「發布與監控」
2. 點擊「App Distribution」
3. 點擊「開始使用」

---

## 📱 整合 Firebase SDK

### 7. 更新專案依賴

#### 方法 A：使用 Google Services Plugin（推薦）

**更新 `gradle/libs.versions.toml`**：

```toml
[versions]
# ... 現有版本
google-services = "4.4.1"
firebase-bom = "33.7.0"

[libraries]
# ... 現有 libraries
firebase-bom = { module = "com.google.firebase:firebase-bom", version.ref = "firebase-bom" }
firebase-app-distribution = { module = "com.google.firebase:firebase-appdistribution" }

[plugins]
# ... 現有 plugins
google-services = { id = "com.google.gms.google-services", version.ref = "google-services" }
firebase-app-distribution = { id = "com.google.firebase.appdistribution", version = "5.0.0" }
```

**更新 `composeApp/build.gradle.kts`**：

```kotlin:composeApp/build.gradle.kts
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.google.services)  // 新增
    alias(libs.plugins.firebase.app.distribution)  // 新增
}

// ... 現有 kotlin {} 配置

android {
    // ... 現有配置
}

dependencies {
    debugImplementation(compose.uiTooling)
    
    // Firebase（僅 Android）
    add("androidMainImplementation", platform(libs.firebase.bom))
    add("androidMainImplementation", libs.firebase.app.distribution)
}
```

#### 方法 B：僅使用 CLI（無需整合 SDK）

如果您只想使用 Firebase CLI 上傳，可以跳過 SDK 整合，直接使用命令列工具。

### 8. 同步專案

在 Android Studio 中：

1. 點擊「Sync Now」同步 Gradle
2. 等待同步完成
3. 確認沒有錯誤

或使用命令列：

```bash
./gradlew sync
```

---

## 🚀 使用 Firebase CLI 上傳

### 9. 安裝 Firebase CLI

#### macOS / Linux

```bash
# 使用 npm（需要先安裝 Node.js）
npm install -g firebase-tools

# 或使用 Homebrew（僅 macOS）
brew install firebase-cli
```

#### 驗證安裝

```bash
firebase --version
```

### 10. 登入 Firebase

```bash
firebase login
```

這會開啟瀏覽器視窗，使用您的 Google 帳號登入。

### 11. 建置 APK

```bash
# 建置 Release APK
./gradlew :composeApp:assembleRelease

# 或建置 Debug APK（測試用）
./gradlew :composeApp:assembleDebug
```

### 12. 上傳到 Firebase

#### 基本上傳

```bash
firebase appdistribution:distribute \
  composeApp/build/outputs/apk/release/composeApp-release.apk \
  --app YOUR_FIREBASE_APP_ID \
  --groups testers
```

**參數說明**：

- `YOUR_FIREBASE_APP_ID`：在 Firebase Console 中找到（格式：`1:123456789:android:abcdef`）
- `--groups testers`：測試者群組名稱

#### 完整上傳（含版本資訊）

```bash
firebase appdistribution:distribute \
  composeApp/build/outputs/apk/release/composeApp-release.apk \
  --app YOUR_FIREBASE_APP_ID \
  --groups testers \
  --release-notes "版本 1.0.1 更新內容：
  - 修正新聞載入問題
  - 優化雙語切換效能
  - 新增夜間模式" \
  --testers "tester1@example.com,tester2@example.com"
```

#### 使用版本資訊檔案

建立 `release-notes.txt`：

```
版本 1.0.1 更新內容

【修正問題】
- 修正新聞列表載入失敗的問題
- 解決測驗頁面閃退問題

【新功能】
- 新增夜間模式
- 優化雙語切換動畫

【改進】
- 提升載入速度 30%
- 優化記憶體使用

歡迎回報任何問題！
```

然後上傳：

```bash
firebase appdistribution:distribute \
  composeApp/build/outputs/apk/release/composeApp-release.apk \
  --app YOUR_FIREBASE_APP_ID \
  --groups testers \
  --release-notes-file release-notes.txt
```

### 13. 取得 Firebase App ID

#### 方法 1：從 Firebase Console

1. 前往 [Firebase Console](https://console.firebase.google.com/)
2. 選擇您的專案
3. 點擊齒輪圖示 ⚙️ > 「專案設定」
4. 滾動到「您的應用程式」區段
5. 找到 Android 應用程式
6. 複製「應用程式 ID」（格式：`1:123456789:android:abcdef123456`）

#### 方法 2：從 google-services.json

開啟 `composeApp/google-services.json`，找到：

```json
{
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "1:123456789:android:abcdef123456",
        ...
      }
    }
  ]
}
```

---

## 🔌 使用 Gradle 插件上傳

### 14. 設定 Gradle 任務

如果您已整合 Firebase SDK，可以直接使用 Gradle 任務。

#### 配置 `composeApp/build.gradle.kts`

```kotlin:composeApp/build.gradle.kts
plugins {
    // ... 現有 plugins
    alias(libs.plugins.firebase.app.distribution)
}

android {
    // ... 現有配置
}

// Firebase App Distribution 配置
firebaseAppDistribution {
    // 您的 Firebase App ID
    appId = "1:123456789:android:abcdef123456"
    
    // 測試者群組
    groups = "testers"
    
    // 或指定個別測試者
    // testers = "tester1@example.com,tester2@example.com"
    
    // 版本資訊
    releaseNotes = """
        版本 1.0.1 更新內容
        - 修正新聞載入問題
        - 優化雙語切換效能
    """.trimIndent()
    
    // 或使用檔案
    // releaseNotesFile = "release-notes.txt"
}
```

#### 使用環境變數（更安全）

建立 `firebase.properties`（記得加入 `.gitignore`）：

```properties
FIREBASE_APP_ID=1:123456789:android:abcdef123456
```

更新 `composeApp/build.gradle.kts`：

```kotlin
// 讀取 Firebase 配置
val firebasePropertiesFile = rootProject.file("firebase.properties")
val firebaseProperties = java.util.Properties()
if (firebasePropertiesFile.exists()) {
    firebaseProperties.load(java.io.FileInputStream(firebasePropertiesFile))
}

firebaseAppDistribution {
    appId = firebaseProperties["FIREBASE_APP_ID"] as String? ?: 
            "1:123456789:android:abcdef123456"
    groups = "testers"
    releaseNotesFile = "release-notes.txt"
}
```

### 15. 執行 Gradle 任務

#### 上傳 Debug 版本

```bash
./gradlew :composeApp:appDistributionUploadDebug
```

#### 上傳 Release 版本

```bash
./gradlew :composeApp:appDistributionUploadRelease
```

#### 組合指令（建置 + 上傳）

```bash
# Debug
./gradlew :composeApp:assembleDebug :composeApp:appDistributionUploadDebug

# Release
./gradlew :composeApp:assembleRelease :composeApp:appDistributionUploadRelease
```

---

## 👥 邀請測試者

### 16. 建立測試者群組

1. 在 Firebase Console 中，進入「App Distribution」
2. 點擊「測試者和群組」分頁
3. 點擊「新增群組」
4. 輸入群組名稱：`testers`（或其他名稱）
5. 新增測試者 Email：
    - 輸入 Email 地址
    - 按 Enter 或點擊新增
    - 可以一次新增多位測試者
6. 點擊「儲存」

### 17. 邀請個別測試者

#### 方法 1：透過 Firebase Console

1. 前往「App Distribution」>「測試者和群組」
2. 點擊測試者群組（例如 `testers`）
3. 點擊「新增測試者」
4. 輸入 Email 地址
5. 點擊「傳送邀請」

#### 方法 2：透過 CLI

```bash
firebase appdistribution:testers:add \
  --project YOUR_PROJECT_ID \
  --emails "tester1@example.com,tester2@example.com" \
  --groups "testers"
```

### 18. 測試者如何安裝

測試者會收到邀請 Email，包含以下步驟：

1. **接受邀請**
    - 點擊 Email 中的邀請連結
    - 使用 Google 帳號登入

2. **安裝 Firebase App Tester**
    -
    Android：從 [Google Play](https://play.google.com/store/apps/details?id=com.google.firebase.apptester)
    安裝
    - iOS：從 App Store 安裝

3. **啟用未知來源**（僅 Android）
    - 前往「設定」>「安全性」
    - 啟用「允許安裝未知來源的應用程式」
    - 或在安裝時按照提示允許

4. **下載並安裝測試版**
    - 在 Firebase App Tester 中找到 BlackCatNews
    - 點擊「下載」
    - 安裝應用程式

5. **接收更新通知**
    - 有新版本時會自動收到通知
    - 可以在 App Tester 中查看所有版本

---

## 📊 收集回饋

### 19. 內建回饋功能

Firebase App Distribution 提供內建的回饋機制：

#### 整合回饋 SDK（選填）

如果您想要測試者能直接在 App 中提交回饋：

**更新 `composeApp/build.gradle.kts`**：

```kotlin
dependencies {
    // ... 現有依賴
    
    // Firebase App Distribution 回饋功能
    add("androidMainImplementation", "com.google.firebase:firebase-appdistribution-api-ktx")
}
```

**在 App 中觸發回饋**（Android）：

建立 `composeApp/src/androidMain/kotlin/com/linli/blackcatnews/FirebaseFeedback.kt`：

```kotlin
package com.linli.blackcatnews

import android.content.Context
import com.google.firebase.appdistribution.FirebaseAppDistribution

object FirebaseFeedback {
    fun showFeedbackDialog(context: Context) {
        val appDistribution = FirebaseAppDistribution.getInstance()
        
        // 顯示回饋對話框
        appDistribution.showFeedbackNotification(
            "有任何問題或建議嗎？",
            com.google.firebase.appdistribution.InterruptionLevel.DEFAULT
        )
    }
    
    fun enableSignInOnStart() {
        val appDistribution = FirebaseAppDistribution.getInstance()
        
        // 啟動時檢查是否為測試者
        appDistribution.signInTester()
            .addOnSuccessListener {
                // 測試者已登入
            }
            .addOnFailureListener {
                // 不是測試者或登入失敗
            }
    }
}
```

### 20. 查看回饋

1. 在 Firebase Console 中，進入「App Distribution」
2. 點擊特定版本
3. 查看「回饋」分頁
4. 可以看到測試者的評論和截圖

---

## 🔄 最佳實踐

### 21. 自動化發布工作流程

#### 建立發布腳本

建立 `scripts/distribute.sh`：

```bash
#!/bin/bash

# 顏色定義
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 開始建置和發布測試版本...${NC}"

# 1. 清理舊建置
echo -e "${BLUE}📦 清理舊建置...${NC}"
./gradlew clean

# 2. 建置 Release APK
echo -e "${BLUE}🔨 建置 Release APK...${NC}"
./gradlew :composeApp:assembleRelease

# 3. 檢查建置是否成功
if [ ! -f "composeApp/build/outputs/apk/release/composeApp-release.apk" ]; then
    echo "❌ 建置失敗！"
    exit 1
fi

# 4. 讀取版本號
VERSION_NAME=$(grep "versionName" composeApp/build.gradle.kts | sed 's/.*"\(.*\)".*/\1/')
echo -e "${GREEN}✅ 建置完成！版本：$VERSION_NAME${NC}"

# 5. 上傳到 Firebase
echo -e "${BLUE}📤 上傳到 Firebase App Distribution...${NC}"
firebase appdistribution:distribute \
  composeApp/build/outputs/apk/release/composeApp-release.apk \
  --app "YOUR_FIREBASE_APP_ID" \
  --groups "testers" \
  --release-notes "版本 $VERSION_NAME 測試版本"

echo -e "${GREEN}🎉 發布完成！${NC}"
```

賦予執行權限：

```bash
chmod +x scripts/distribute.sh
```

執行：

```bash
./scripts/distribute.sh
```

### 22. 使用 GitHub Actions 自動發布

建立 `.github/workflows/firebase-distribution.yml`：

```yaml
name: Firebase App Distribution

on:
  push:
    branches:
      - develop  # 推送到 develop 分支時自動發布

jobs:
  distribute:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Setup Gradle
        uses: gradle/gradle-build-action@v3
      
      - name: Build Release APK
        run: ./gradlew :composeApp:assembleRelease
      
      - name: Upload to Firebase App Distribution
        uses: wzieba/Firebase-Distribution-Github-Action@v1
        with:
          appId: ${{ secrets.FIREBASE_APP_ID }}
          serviceCredentialsFileContent: ${{ secrets.FIREBASE_SERVICE_ACCOUNT }}
          groups: testers
          file: composeApp/build/outputs/apk/release/composeApp-release.apk
          releaseNotes: "自動建置版本 - Commit: ${{ github.sha }}"
```

設定 GitHub Secrets：

1. 前往 GitHub 專案 > Settings > Secrets and variables > Actions
2. 新增 `FIREBASE_APP_ID`
3. 新增 `FIREBASE_SERVICE_ACCOUNT`（從 Firebase Console 下載 Service Account JSON）

---

## ❓ 常見問題

### Q1: Firebase App Distribution 需要付費嗎？

**答**：不需要！Firebase Spark（免費方案）就可以使用 App Distribution 功能。

### Q2: 測試者需要 Firebase 帳號嗎？

**答**：測試者需要 Google 帳號來接受邀請和登入 Firebase App Tester，但不需要 Firebase 專案帳號。

### Q3: 可以同時使用 Firebase 和 Google Play 測試嗎？

**答**：可以！建議的使用方式：

- **Firebase**：開發階段、內部測試、快速迭代
- **Google Play**：Beta 測試、預發布、正式上架前測試

### Q4: 上傳的 APK 有大小限制嗎？

**答**：

- **APK**：最大 200 MB
- 如果超過，考慮使用 ProGuard 壓縮或 App Bundle

### Q5: 測試者安裝時遇到「無法安裝」錯誤？

**答**：常見原因：

1. **未啟用未知來源**：需要在設定中允許安裝未知來源的 App
2. **簽名衝突**：如果之前安裝過其他版本，需要先解除安裝
3. **Android 版本不符**：檢查 minSdk 設定

### Q6: 如何撤銷測試者存取權限？

**答**：

1. 前往 Firebase Console > App Distribution > 測試者和群組
2. 找到該測試者
3. 點擊「移除」

### Q7: 可以看到測試者的安裝數據嗎？

**答**：可以！在 Firebase Console 的 App Distribution 頁面可以看到：

- 各版本的下載次數
- 測試者安裝狀態
- 回饋和崩潰報告

### Q8: Firebase CLI 登入失敗怎麼辦？

**答**：

1. 確認已安裝最新版本：`firebase --version`
2. 嘗試登出後重新登入：
   ```bash
   firebase logout
   firebase login
   ```
3. 使用瀏覽器手動登入

### Q9: 上傳失敗：Permission denied？

**答**：確認：

1. 已執行 `firebase login`
2. 您的 Google 帳號有該 Firebase 專案的權限
3. Firebase App ID 正確

### Q10: 如何發布 iOS 版本？

**答**：Firebase App Distribution 也支援 iOS！流程類似：

1. 建置 iOS IPA 檔案
2. 使用 Firebase CLI 上傳
3. 邀請測試者（使用 TestFlight 或直接分發）

詳細步驟請參考 Firebase 官方文件。

---

## ✅ 檢查清單

### 設定檢查清單

- [ ] Firebase 專案已建立
- [ ] Android 應用程式已新增到 Firebase
- [ ] `google-services.json` 已下載並放置正確位置
- [ ] Firebase CLI 已安裝
- [ ] 已執行 `firebase login`
- [ ] Firebase App ID 已取得
- [ ] 測試者群組已建立
- [ ] 測試者已邀請

### 發布檢查清單

- [ ] APK 已成功建置
- [ ] Release notes 已準備
- [ ] 版本號已更新
- [ ] 上傳命令已測試
- [ ] 測試者已收到通知
- [ ] 測試者能成功安裝

### 測試檢查清單

- [ ] 測試者已安裝 Firebase App Tester
- [ ] 測試者能看到新版本
- [ ] 測試者能成功下載和安裝
- [ ] App 功能正常運作
- [ ] 回饋機制正常
- [ ] 已收集測試者意見

---

## 📚 相關資源

### Firebase 官方文件

- [Firebase App Distribution 文件](https://firebase.google.com/docs/app-distribution)
- [Firebase CLI 參考](https://firebase.google.com/docs/cli)
- [Android 整合指南](https://firebase.google.com/docs/app-distribution/android/distribute-gradle)

### 工具

- [Firebase Console](https://console.firebase.google.com/)
- [Firebase CLI](https://firebase.google.com/docs/cli#install_the_firebase_cli)
- [Firebase App Tester（Android）](https://play.google.com/store/apps/details?id=com.google.firebase.apptester)

### 社群

- [Firebase Support](https://firebase.google.com/support)
- [Stack Overflow - Firebase](https://stackoverflow.com/questions/tagged/firebase)

---

## 🎉 完成！

您現在已經學會如何使用 Firebase App Distribution 快速分發測試版本！

### 建議工作流程

1. **開發中** → 使用 Firebase App Distribution（快速迭代）
2. **Alpha 測試** → Firebase App Distribution（小規模測試）
3. **Beta 測試** → Google Play 封閉測試（大規模測試）
4. **正式發布** → Google Play 正式版

這樣可以充分利用兩個平台的優勢！🚀

---

**最後更新日期**：2025年1月

**文件版本**：1.0

如有任何問題或需要協助，歡迎隨時提出。
