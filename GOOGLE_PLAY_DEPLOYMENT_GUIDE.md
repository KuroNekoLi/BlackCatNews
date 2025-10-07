# BlackCatNews Google Play Store 上架完整指南

本指南將協助您將 BlackCatNews 應用程式上架到 Google Play Store。

## 📋 目錄

- [前置準備](#前置準備)
- [配置專案](#配置專案)
- [簽名設定](#簽名設定)
- [建置 Release APK/AAB](#建置-release-apkaab)
- [Google Play Console 設定](#google-play-console-設定)
- [上傳和發布](#上傳和發布)
- [測試軌道](#測試軌道)
- [常見問題](#常見問題)
- [檢查清單](#檢查清單)

---

## 🎯 前置準備

### 1. Google Play 開發者帳號

#### 註冊步驟

1. 前往 [Google Play Console](https://play.google.com/console/)
2. 使用您的 Google 帳號登入
3. 點擊「建立開發人員帳戶」
4. 選擇帳戶類型：
    - **個人開發者**：使用個人名義
    - **機構開發者**：使用公司名義（需要公司資訊）
5. 填寫開發者資訊：
    - 開發者名稱（會顯示在 Play Store）
    - Email 地址
    - 電話號碼
    - 網站（選填）
6. 閱讀並接受開發者發布協議
7. 完成付款（**一次性費用 $25 美金**）
8. 等待帳號審核（通常幾小時到 24 小時）

#### 確認註冊完成

- 收到 Google 的確認郵件
- 可以存取 Google Play Console
- 帳號狀態顯示「已驗證」

### 2. 準備必要資料

在開始前，請準備以下資料：

#### 圖像資源

- **應用程式圖示**：512x512 像素，PNG 格式（32 位元）
- **功能圖片**：1024x500 像素，JPG 或 PNG
- **螢幕截圖**：
    - 手機：至少 2 張（最多 8 張）
    - 7 吋平板：至少 1 張（選填）
    - 10 吋平板：至少 1 張（選填）

#### 文字內容

- 應用程式名稱（最多 50 字元）
- 簡短說明（最多 80 字元）
- 完整說明（最多 4000 字元）
- 隱私權政策 URL

#### 分類資訊

- 應用程式類別
- 內容分級問卷答案
- 目標年齡層

---

## ⚙️ 配置專案

### 3. 更新 build.gradle.kts

讓我們為您的專案新增必要的 Release 配置：

```kotlin:composeApp/build.gradle.kts
android {
    namespace = "com.linli.blackcatnews"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.linli.blackcatnews"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1  // 每次更新時遞增
        versionName = "1.0"  // 顯示給使用者的版本號
    }
    
    // 簽名配置
    signingConfigs {
        create("release") {
            // 這些值會從 keystore.properties 讀取
            storeFile = file(project.properties["RELEASE_STORE_FILE"] as String? ?: "release.keystore")
            storePassword = project.properties["RELEASE_STORE_PASSWORD"] as String? ?: ""
            keyAlias = project.properties["RELEASE_KEY_ALIAS"] as String? ?: ""
            keyPassword = project.properties["RELEASE_KEY_PASSWORD"] as String? ?: ""
        }
    }
    
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        
        getByName("release") {
            isMinifyEnabled = true  // 啟用程式碼壓縮
            isShrinkResources = true  // 移除未使用的資源
            
            // 設定混淆規則
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // 使用 release 簽名
            signingConfig = signingConfigs.getByName("release")
        }
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
```

### 4. 建立 ProGuard 規則

建立 `composeApp/proguard-rules.pro` 檔案：

```proguard
# Keep all Kotlin Metadata
-keep class kotlin.Metadata { *; }

# Ktor
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Serializable classes
-keep,includedescriptorclasses class com.linli.blackcatnews.**$$serializer { *; }
-keepclassmembers class com.linli.blackcatnews.** {
    *** Companion;
}
-keepclasseswithmembers class com.linli.blackcatnews.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Compose
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }

# Coil
-keep class coil3.** { *; }

# KSoup
-keep class com.fleeksoft.ksoup.** { *; }
```

### 5. 更新 AndroidManifest.xml

確保 `composeApp/src/androidMain/AndroidManifest.xml` 已正確配置：

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- 網路權限 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@android:style/Theme.Material.Light.NoActionBar"
        android:usesCleartextTraffic="false">  <!-- 安全性：禁止明文 HTTP -->
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:configChanges="orientation|screenSize|keyboardHidden"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

**重要**：如果您的 API 使用 HTTP（非 HTTPS），需要設定 `android:usesCleartextTraffic="true"`，但 Google
Play 可能會要求說明。

---

## 🔐 簽名設定

### 6. 產生上傳金鑰（Upload Key）

Android 應用程式必須使用數位簽名才能上架。

#### 使用指令產生金鑰

```bash
# 在專案根目錄執行
keytool -genkey -v -keystore release.keystore \
  -alias blackcatnews \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass YourStorePassword \
  -keypass YourKeyPassword \
  -dname "CN=Lin Li, OU=Development, O=BlackCatNews, L=Taipei, ST=Taiwan, C=TW"
```

**參數說明**：

- `release.keystore`：金鑰檔案名稱
- `blackcatnews`：金鑰別名（alias）
- `YourStorePassword`：金鑰庫密碼（請改成您的密碼）
- `YourKeyPassword`：金鑰密碼（請改成您的密碼）
- `validity 10000`：有效期限（天數），約 27 年

#### 使用 Android Studio 產生（圖形化介面）

1. 在 Android Studio 中開啟專案
2. 選擇 **Build** > **Generate Signed Bundle / APK**
3. 選擇 **Android App Bundle**
4. 點擊 **Create new...**
5. 填寫金鑰資訊：
    - Key store path：選擇儲存位置
    - Password：設定金鑰庫密碼
    - Key alias：輸入別名（例如 `blackcatnews`）
    - Key password：設定金鑰密碼
    - Validity：25 年
    - Certificate 資訊（姓名、組織等）
6. 點擊 **OK**

### 7. 設定金鑰屬性

建立 `keystore.properties` 檔案（在專案根目錄）：

```properties
RELEASE_STORE_FILE=../release.keystore
RELEASE_STORE_PASSWORD=YourStorePassword
RELEASE_KEY_ALIAS=blackcatnews
RELEASE_KEY_PASSWORD=YourKeyPassword
```

**⚠️ 重要安全提示**：

- 將 `keystore.properties` 加入 `.gitignore`
- **絕對不要**將金鑰檔案或密碼提交到 Git
- 安全備份 `release.keystore` 檔案（遺失將無法更新應用程式）

### 8. 更新 .gitignore

確保 `.gitignore` 包含：

```gitignore
# Keystore files
*.jks
*.keystore
keystore.properties

# Local configuration
local.properties
```

### 9. 讀取金鑰屬性

更新專案根目錄的 `build.gradle.kts`，在 `composeApp/build.gradle.kts` 中加入：

```kotlin
// 在 android {} 區塊之前加入
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = java.util.Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(java.io.FileInputStream(keystorePropertiesFile))
}

android {
    // ... 其他配置
    
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["RELEASE_STORE_FILE"] as String? ?: "release.keystore")
            storePassword = keystoreProperties["RELEASE_STORE_PASSWORD"] as String? ?: ""
            keyAlias = keystoreProperties["RELEASE_KEY_ALIAS"] as String? ?: ""
            keyPassword = keystoreProperties["RELEASE_KEY_PASSWORD"] as String? ?: ""
        }
    }
    
    // ... 其他配置
}
```

---

## 📦 建置 Release APK/AAB

### 10. 什麼是 AAB vs APK？

| 格式 | 說明 | 用途 |
|------|------|------|
| **AAB** (Android App Bundle) | Google 推薦的發布格式 | **上傳到 Play Store**（必須） |
| **APK** (Android Package) | 傳統安裝檔 | 直接安裝到裝置 |

**Google Play 要求**：2021 年 8 月起，新應用程式必須使用 AAB 格式。

### 11. 建置 AAB（推薦）

#### 方法 1：使用 Gradle 指令

```bash
# 在專案根目錄執行
./gradlew :composeApp:bundleRelease
```

建置完成後，AAB 檔案位於：

```
composeApp/build/outputs/bundle/release/composeApp-release.aab
```

#### 方法 2：使用 Android Studio

1. 選擇 **Build** > **Generate Signed Bundle / APK**
2. 選擇 **Android App Bundle**
3. 選擇或建立金鑰
4. 選擇 **release** build type
5. 點擊 **Finish**

### 12. 建置 APK（測試用）

如果您想要測試 Release 版本：

```bash
./gradlew :composeApp:assembleRelease
```

APK 檔案位於：

```
composeApp/build/outputs/apk/release/composeApp-release.apk
```

### 13. 測試 Release 版本

在上傳前，務必測試 Release 版本：

```bash
# 安裝到連接的裝置
adb install composeApp/build/outputs/apk/release/composeApp-release.apk

# 或使用 Gradle
./gradlew :composeApp:installRelease
```

**測試項目**：

- ✅ 所有功能正常運作
- ✅ 網路請求正常
- ✅ 圖片載入正常
- ✅ 無崩潰或錯誤
- ✅ 效能流暢

---

## 🏪 Google Play Console 設定

### 14. 建立應用程式

1. 登入 [Google Play Console](https://play.google.com/console/)
2. 點擊「建立應用程式」或「Create app」
3. 填寫基本資訊：

#### 應用程式詳細資料

- **應用程式名稱**：BlackCatNews
- **預設語言**：繁體中文（台灣）
- **應用程式類型**：應用程式
- **免費或付費**：免費

#### 宣告

- ✅ 這是應用程式嗎？是
- ✅ 遵守 Google Play 政策
- ✅ 遵守美國出口法律

4. 點擊「建立應用程式」

### 15. 設定商店資訊

#### 主要商店資訊頁面

前往「商店資訊」>「主要商店資訊」

##### 應用程式名稱

```
BlackCatNews
```

（最多 50 字元）

##### 簡短說明

```
每日精選新聞，雙語學習，提升您的閱讀與語言能力
```

（最多 80 字元）

##### 完整說明

```
BlackCatNews 是您的每日新聞閱讀夥伴，提供精選的新聞內容和獨特的雙語學習功能。

【主要功能】
📰 精選新聞
• 每日更新最新資訊
• 多元化新聞來源
• 智慧推薦演算法

🌍 雙語閱讀
• 中英文對照顯示
• 提升語言能力
• ���習專業詞彙

📝 互動測驗
• 新聞理解測驗
• 加深閱讀印象
• 趣味學習體驗

🎨 優質體驗
• 簡潔現代介面
• 流暢閱讀體驗
• 護眼閱讀模式

🔖 個人化功能
• 收藏喜愛文章
• 閱讀歷史記錄
• 自訂閱讀偏好

【適合對象】
✓ 關注時事的讀者
✓ 英語學習者
✓ 雙語教育使用者
✓ 新聞從業人員

BlackCatNews 讓您在忙碌的生活中，輕鬆掌握世界脈動，同時提升語言能力。

立即下載，開始您的新聞閱讀之旅！
```

（最多 4000 字元）

### 16. 上傳圖像資源

#### 應用程式圖示

- **尺寸**：512 x 512 像素
- **格式**：32 位元 PNG（含 Alpha 通道）
- **檔案大小**：最大 1024 KB

#### 功能圖片

- **尺寸**：1024 x 500 像素
- **格式**：JPG 或 24 位元 PNG（不含 Alpha 通道）
- **內容**：展示應用程式主要功能的橫幅圖片

#### 螢幕截圖

**手機截圖**（必要）：

- **尺寸**：至少 320 像素，最多 3840 像素
- **建議尺寸**：1080 x 2340 像素（適用於現代手機）
- **數量**：最少 2 張，最多 8 張
- **格式**：JPG 或 PNG

**平板截圖**（選填）：

- 7 吋平板：最少 1 張
- 10 吋平板：最少 1 張

#### 製作截圖建議

**使用 Android Emulator**：

```bash
# 啟動模擬器
emulator -avd Pixel_7_Pro_API_34

# 或在 Android Studio 中啟動 AVD Manager
```

**截圖內容建議**：

1. 首頁/新聞列表
2. 新聞詳細頁面
3. 雙語閱讀模式
4. 測驗互動畫面
5. 文章收藏功能

**美化工具**：

- [Screener](https://play.google.com/store/apps/details?id=de.toastcode.screener) - 自動加入裝置外框
- Figma / Photoshop - 專業設計工具
- [AppMockUp](https://app-mockup.com/) - 線上工具

### 17. 分類和標記

#### 應用程式類別

- **類別**：新聞與雜誌
- **標記**：新聞、學習、閱讀、雙語、英文

#### 聯絡資訊

- **Email**：您的聯絡 Email
- **電話**：選填
- **網站**：您的網站 URL（選填）

#### 隱私權政策

- **URL**：**必填項目**
- 必須是可公開存取的網頁

**隱私權政策範本**：

```markdown
# BlackCatNews 隱私權政策

最後更新日期：2025年1月

## 1. 資料收集
BlackCatNews 不會收集使用者的個人資料。

## 2. 網路使用
本應用程式需要網路連線以載入新聞內容。

## 3. 第三方服務
我們可能使用第三方服務來提供新聞內容。

## 4. 資料安全
我們重視使用者隱私，不會儲存或分享個人資訊。

## 5. 聯絡我們
如有任何問題，請聯絡：your-email@example.com
```

您可以將此政策放在：

- GitHub Pages（免費）
- 您的網站
- Google Sites（免費）

---

## 📤 上傳和發布

### 18. 建立版本

#### 前往「製作版本」

1. 在 Google Play Console 中，選擇您的應用程式
2. 前往「製作版本」>「測試」>「內部測試」（或直接選擇「正式版」）

#### 建立新版本

1. 點擊「建立新版本」
2. 上傳 AAB 檔案：
    - 拖放 `composeApp-release.aab`
    - 或點擊上傳按鈕選擇檔案
3. 等待上傳完成（Google Play 會自動驗證）

#### 填寫版本資訊

**版本名稱**：1.0

**版本資訊**（這個版本的新功能）：

```
首次發布！

【新功能】
• 精選新聞閱讀
• 雙語對照功能
• 互動式測驗
• 文章收藏功能
• 簡潔現代介面

感謝您的下載，歡迎提供寶貴意見！
```

### 19. 內容分級

完成內容分級問卷（必要）：

1. 前往「政策」>「應用程式內容」>「內容分級」
2. 選擇應用程式類別：**新聞**
3. 回答問卷：
    - 是否包含暴力內容？
    - 是否包含性或裸露內容？
    - 是否包含仇恨言論？
    - 等等...

對於新聞應用程式，根據您的內容誠實回答。

### 20. 目標受眾和內容

#### 目標年齡層

1. 前往「政策」>「應用程式內容」>「目標受眾和內容」
2. 選擇目標年齡層：
    - 建議選擇「16 歲以上」或「18 歲以上」（取決於新聞內容）

#### 應用程式內容

回答以下問題：

- 是否為兒童導向？**否**
- 是否包含廣告？根據實際情況選擇
- 是否包含應用程式內購買？根據實際情況選擇

### 21. 資料安全性

1. 前往「政策」>「應用程式內容」>「資料安全性」
2. 回答資料收集問卷：

#### 資料收集和安全性

- **您的應用程式是否會收集或分享任何必要的使用者資料類型？**
    - 如果不收集：選擇「否」
    - 如果收集：說明收集哪些資料及用途

#### 範例（如果不收集資料）：

```
本應用程式不會收集、儲存或分享使用者的個人資料。
應用程式僅透過網路連線載入公開的新聞內容。
```

### 22. 選擇發布軌道

Google Play 提供三種測試軌道：

#### 內部測試（Internal Testing）

- **對象**：最多 100 位測試者
- **審核**：無需審核，幾分鐘內即可使用
- **用途**：快速測試，內部團隊使用

#### 封閉測試（Closed Testing）

- **對象**：最多數千位測試者（需建立測試者清單或群組）
- **審核**：需要審核，但較快
- **用途**：Beta 測試，邀請特定使用者

#### 開放測試（Open Testing）

- **對象**：任何人都能加入
- **審核**：需要完整審核
- **用途**：公開 Beta，收集更多回饋

#### 正式版（Production）

- **對象**：所有使用者
- **審核**：完整審核，通常 1-7 天
- **用途**：正式上架

**建議流程**：

1. **內部測試** → 確保基本功能正常
2. **封閉測試** → 邀請朋友、家人測試
3. **正式版** → 正式發布

### 23. 提交審核

完成所有設定後：

1. 檢查「發布總覽」頁面，確認所有項目都已完成
2. 點擊「提交審核」或「開始發布」
3. 確認提交

#### 審核檢查清單

- ✅ AAB 已上傳
- ✅ 商店資訊已填寫完整
- ✅ 圖像資源已上傳
- ✅ 內容分級已完成
- ✅ 目標受眾已設定
- ✅ 資料安全性已說明
- ✅ 隱私權政策 URL 已提供
- ✅ 聯絡資訊已填寫

---

## 🔍 審核和發布

### 24. 審核狀態追蹤

#### 審核狀態說明

| 狀態 | 說明 |
|------|------|
| 審核中（In review） | Google 團隊正在審核 |
| 已核准（Approved） | 審核通過，準備發布 |
| 已發布（Published） | 已在 Play Store 上架 |
| 遭拒（Rejected） | 審核未通過，需要修正 |

#### 審核時間

- **內部/封閉測試**：通常幾小時
- **正式版首次發布**：1-7 天（通常 1-3 天）
- **更新版本**：較快，通常 1-2 天

### 25. 處理審核被拒

如果審核被拒：

1. **查看拒絕原因**
    - 在 Play Console 中查看詳細訊息
    - Google 會說明具體問題

2. **常見被拒原因**
    - 隱私權政策缺失或無法存取
    - 應用程式崩潰或無法運作
    - 違反 Google Play 政策
    - 元數據（描述、截圖）不準確
    - 權限使用不當

3. **修正問題**
    - 根據回饋修正應用程式
    - 增加 versionCode（例如從 1 改為 2）
    - 重新建置並上傳新的 AAB

4. **申訴**
    - 如果認為拒絕不合理，可以申訴
    - 提供詳細說明

### 26. 發布後

#### 監控應用程式

1. **查看統計資料**
    - 安裝次數
    - 使用者評分
    - 崩潰報告

2. **回應評論**
    - 及時回應使用者評論
    - 展現良好的開發者形象

3. **監控崩潰報告**
    - 在 Play Console 中查看「Android Vitals」
    - 修正崩潰和 ANR（應用程式無回應）

#### 更新應用程式

當需要更新時：

1. 修改程式碼
2. **增加 versionCode 和 versionName**：
   ```kotlin
   defaultConfig {
       versionCode = 2  // 從 1 增加到 2
       versionName = "1.1"  // 從 1.0 升到 1.1
   }
   ```
3. 重新建置 AAB
4. 在 Play Console 建立新版本
5. 上傳新的 AAB
6. 填寫更新資訊
7. 提交審核

---

## 🧪 測試軌道

### 27. 設定內部測試

建議先使用內部測試：

1. 前往「製作版本」>「內部測試」
2. 建立新版本並上傳 AAB
3. 建立測試者清單：
    - 點擊「測試者」分頁
    - 建立新清單
    - 新增測試者 Email
4. 儲存並發布

測試者會收到邀請 Email，可以透過連結下載測試版。

### 28. 使用 Firebase App Distribution（補充）

如果您想要更靈活的測試方式，可以搭配使用 Firebase：

#### 優點

- 更快的發布速度（不需 Google 審核）
- 更靈活的測試者管理
- 詳細的測試回饋收集
- 支援 iOS 和 Android

#### 設定步驟（簡要）

1. 在 Firebase Console 建立專案
2. 新增 Android 應用程式
3. 下載 `google-services.json`
4. 整合 Firebase SDK
5. 使用 Firebase CLI 上傳 APK

**詳細步驟請參考**：`FIREBASE_DEPLOYMENT_GUIDE.md`（我可以為您建立）

---

## ❓ 常見問題

### Q1: AAB 和 APK 有什麼差別？

**答**：

- **AAB**：Android App Bundle，是 Google 推薦的發布格式。Play Store 會根據使用者裝置自動產生最佳化的
  APK。
- **APK**：傳統安裝檔，包含所有資源，檔案較大。
- **Play Store 要求**：新應用程式必須使用 AAB。

### Q2: 如果遺失了金鑰檔案怎麼辦？

**答**：

- **使用 Play App Signing**（推薦）：Google 會幫您保管金鑰，即使遺失上傳金鑰也能重新產生。
- **沒使用 Play App Signing**：遺失金鑰將無法更新應用程式，只能建立新的應用程式。
- **建議**：備份金鑰檔案到安全的地方（例如密碼管理器、加密雲端硬碟）。

### Q3: 審核要多久？

**答**：

- **首次發布**：通常 1-7 天，平均 2-3 天
- **更新版本**：通常 1-2 天
- **內部測試**：無需審核，幾分鐘即可使用

### Q4: 可以在審核期間撤回嗎？

**答**：可以。在審核完成前，您可以在 Play Console 中暫停發布。

### Q5: 什麼是 Play App Signing？

**答**：

- Google 的金鑰管理服務
- Google 會用自己的金鑰簽署發布到使用者的 APK
- 您只需保管「上傳金鑰」
- **強烈建議**使用此功能

啟用方式：

1. 在第一次發布時，Google 會詢問是否使用
2. 選擇「是」
3. 完成設定

### Q6: 如何處理多語言？

**答**：

1. 在 Play Console 中，點擊「新增語言」
2. 選擇語言（例如英文、日文）
3. 填寫該語言的商店資訊和截圖
4. 在應用程式中使用 Android 的本地化資源

### Q7: 需要隱私權政策嗎？

**答**：

- **是的，必須提供**
- 即使不收集使用者資料，仍需說明
- 必須是可公開存取的網頁 URL

### Q8: Google Play 註冊費是一次性的嗎？

**答**：是的，$25 美金是一次性費用，不需要年費。

### Q9: 可以撤下已發布的應用程式嗎？

**答**：

- 可以從 Play Store 下架
- 但已安裝的使用者仍可繼續使用
- 可以選擇「取消發布」或「永久刪除」

### Q10: versionCode 和 versionName 的差別？

**答**：

- **versionCode**：整數，每次更新必須遞增（1, 2, 3...），Play Store 用來辨識版本
- **versionName**：字串，顯示給使用者的版本號（1.0, 1.1, 2.0...）

---

## ✅ 檢查清單

### 發布前檢查清單

#### 開發者帳號

- [ ] 已註冊 Google Play 開發者帳號
- [ ] 已完成付款（$25）
- [ ] 帳號已驗證

#### 專案配置

- [ ] versionCode 已設定
- [ ] versionName 已設定
- [ ] applicationId 正確
- [ ] 已建立 ProGuard 規則
- [ ] AndroidManifest 已正確配置
- [ ] 權限宣告適當

#### 簽名設定

- [ ] 已產生 release.keystore
- [ ] 已建立 keystore.properties
- [ ] 金鑰已安全備份
- [ ] .gitignore 已更新（排除金鑰檔案）
- [ ] signingConfigs 已設定

#### 建置

- [ ] AAB 建置成功
- [ ] Release APK 已測試
- [ ] 無崩潰或嚴重錯誤
- [ ] 所有功能正常運作
- [ ] 網路請求正常
- [ ] 圖片載入正常

#### Play Console

- [ ] 應用程式已建立
- [ ] 商店資訊已填寫
- [ ] 簡短說明已填寫
- [ ] 完整說明已填寫
- [ ] 應用程式圖示已上傳（512x512）
- [ ] 功能圖片已上傳（1024x500）
- [ ] 手機截圖已上傳（至少 2 張）
- [ ] 聯絡 Email 已填寫
- [ ] 隱私權政策 URL 已提供且可存取

#### 政策和內容

- [ ] 內容分級問卷已完成
- [ ] 目標受眾已設定
- [ ] 資料安全性已說明
- [ ] 應用程式類別已選擇

#### 測試

- [ ] 內部測試已完成（建議）
- [ ] 封閉測試已完成（建議）
- [ ] 已收集測試者回饋
- [ ] 主要問題已修正

#### 發布

- [ ] AAB 已上傳
- [ ] 版本資訊已填寫
- [ ] 所有必要項目已完成
- [ ] 已提交審核

---

## 📚 相關資源

### Google 官方文件

- [Google Play Console 說明](https://support.google.com/googleplay/android-developer/)
- [Android Developers 指南](https://developer.android.com/distribute)
- [Play Store 政策中心](https://support.google.com/googleplay/android-developer/topic/9858052)
- [Play App Signing](https://support.google.com/googleplay/android-developer/answer/9842756)

### 工具

- [Google Play Console](https://play.google.com/console/)
- [Android Studio](https://developer.android.com/studio)
- [Firebase Console](https://console.firebase.google.com/)

### 社群資源

- [Android Developers - Reddit](https://www.reddit.com/r/androiddev/)
- [Stack Overflow - Android](https://stackoverflow.com/questions/tagged/android)

---

## 🎉 完成！

恭喜您完成所有步驟！您的 BlackCatNews Android 應用程式即將與全球使用者見面。

祝您上架順利！🚀

---

**最後更新日期**：2025年1月

**文件版本**：1.0

**下一步**：考慮整合 Firebase App Distribution 進行更靈活的測試，請參考
`FIREBASE_DEPLOYMENT_GUIDE.md`。

如有任何問題或需要協助，歡迎隨時提出。
