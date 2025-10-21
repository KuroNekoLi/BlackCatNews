# Google Play AD_ID 權限修復

## ❌ 問題

Google Play Console 上傳失敗，錯誤訊息：

```
This release includes the com.google.android.gms.permission.AD_ID permission 
but your declaration on Play Console says your app doesn't use advertising ID. 
You must update your advertising ID declaration.
```

---

## 🔍 原因分析

### 為什麼會有 AD_ID 權限？

**Firebase Analytics** 和某些 Google Play Services 依賴會自動在 `AndroidManifest.xml` 中添加 `AD_ID`
權限。

```
你的依賴鏈：
├── firebase-analytics
│   └── play-services-measurement
│       └── 自動添加 AD_ID 權限
```

### Google Play 的要求

如果你的應用：

- ✅ **不使用廣告**
- ✅ **不追蹤使用者用於廣告目的**
- ✅ 在 Play Console 中聲明「不使用廣告 ID」

那麼你**必須從 APK/AAB 中移除** `AD_ID` 權限。

---

## ✅ 解決方案

### 方案 1：明確移除 AD_ID 權限（已實施）

在 `composeApp/src/androidMain/AndroidManifest.xml` 中添加：

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

   <!-- 明確移除廣告 ID 權限（Firebase Analytics 預設會添加） -->
    <uses-permission 
        android:name="com.google.android.gms.permission.AD_ID"
        tools:node="remove" />

</manifest>
```

**效果**：

- ✅ Firebase Analytics 仍然正常運作（使用匿名統計）
- ✅ 移除了廣告 ID 權限
- ✅ 符合 Google Play 政策

---

### 方案 2：在 Play Console 中更新聲明（如果你需要使用廣告）

如果你**確實需要使用廣告 ID**（比如接入 AdMob 或其他廣告 SDK），則需要：

#### 步驟 1：移除 AndroidManifest.xml 中的限制

在 `composeApp/src/androidMain/AndroidManifest.xml` 中：

```xml
<!-- 移除或註解掉這段 -->
<!--
<uses-permission 
    android:name="com.google.android.gms.permission.AD_ID"
    tools:node="remove" />
-->
```

#### 步驟 2：在 Play Console 中聲明廣告 ID 使用

1. **登入 Play Console**
    - 前往 [Google Play Console](https://play.google.com/console/)
    - 選擇你的應用

2. **進入「應用程式內容」設定**
   ```
   左側選單 → 政策 → 應用程式內容
   ```

3. **找到「廣告 ID」區塊**
    - 向下捲動找到「廣告 ID」或「Advertising ID」
    - 點擊「開始」或「管理」

4. **回答問題**

   **問題 1：「這個應用程式是否會收集、使用或分享廣告 ID？」**
    - ✅ 選擇：**是**

   **問題 2：「這個應用程式使用廣告 ID 的目的為何？」**（可多選）

   請根據實際情況勾選：

    - ✅ **廣告或行銷**
        - 在應用程式中顯示廣告
        - 廣告個人化

    - ✅ **分析**
        - 分析使用者行為
        - 衡量廣告成效

    - ✅ **詐欺預防、安全性和法規遵循**
        - 偵測詐欺行為
        - 防止濫用

    - ✅ **應用程式功能**
        - 提供應用程式核心功能

   **範例選擇（如果使用 AdMob）：**
   ```
   ✓ 廣告或行銷
   ✓ 分析
   ```

5. **確認聲明**
    - 確認所有資訊正確
    - 點擊「儲存」

6. **提交審查**
    - Play Console 會審查你的聲明
    - 通常幾分鐘到幾小時內完成

#### 步驟 3：更新隱私權政策

你的隱私權政策必須說明：

```markdown
## 廣告與追蹤

本應用程式使用 Google AdMob 顯示廣告。

### 廣告 ID 的使用

- 我們收集裝置的廣告識別碼（Advertising ID）
- 用途：提供個人化廣告、衡量廣告成效

### 第三方廣告合作夥伴

- Google AdMob
- 可能收集：裝置資訊、廣告互動資料

### 選擇退出

使用者可以在裝置設定中：

- Android：設定 → Google → 廣告 → 選擇退出廣告個人化
- iOS：設定 → 隱私 → 追蹤 → 關閉
```

---

## 🔒 隱私政策最佳實踐

### 你的應用程式目前狀態

| 功能                       | 是否使用 | AD_ID 需求       |
|--------------------------|------|----------------|
| **Firebase Analytics**   | ✅ 是  | ❌ 不需要（匿名統計）    |
| **Firebase Crashlytics** | ✅ 是  | ❌ 不需要          |
| **Firebase Auth**        | ✅ 是  | ❌ 不需要          |
| **廣告 SDK（AdMob）**        | ❌ 否  | ⚠️ 如需要則要 AD_ID |
| **廣告追蹤**                 | ❌ 否  | ❌ 不需要          |

**結論**：你的應用程式**目前不需要** AD_ID 權限 ✅

---

## 📝 Firebase Analytics 說明

### 移除 AD_ID 後 Analytics 仍然可以：

| 功能                 | 狀態      |
|--------------------|---------|
| **使用者數量統計**        | ✅ 正常運作  |
| **事件追蹤**           | ✅ 正常運作  |
| **當機報告**           | ✅ 正常運作  |
| **畫面瀏覽**           | ✅ 正常運作  |
| **使用者屬性**          | ✅ 正常運作  |
| **匿名 Instance ID** | ✅ 使用此替代 |

### 移除 AD_ID 後無法使用：

| 功能            | 狀態    |
|---------------|-------|
| **跨應用程式廣告追蹤** | ❌ 不可用 |
| **個人化廣告**     | ❌ 不可用 |
| **廣告歸因**      | ❌ 不可用 |

---

## 📱 常見廣告 SDK 設定範例

### 如果未來要接入 AdMob

#### 1. 添加依賴

在 `gradle/libs.versions.toml` 中：

```toml
[versions]
play-services-ads = "23.7.0"

[libraries]
play-services-ads = { module = "com.google.android.gms:play-services-ads", version.ref = "play-services-ads" }
```

在 `composeApp/build.gradle.kts` 中：

```kotlin
androidMain.dependencies {
    implementation(libs.play.services.ads)
}
```

#### 2. 移除 AD_ID 限制

在 `AndroidManifest.xml` 中移除：

```xml
<!-- 移除這段 -->
<!--
<uses-permission 
    android:name="com.google.android.gms.permission.AD_ID"
    tools:node="remove" />
-->
```

#### 3. 添加 AdMob App ID

在 `AndroidManifest.xml` 的 `<application>` 中：

```xml

<application>
   <!-- AdMob App ID -->
   <meta-data android:name="com.google.android.gms.ads.APPLICATION_ID"
           android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy" />

   <!-- 其他配置 -->
</application>
```

#### 4. 初始化 AdMob

在 `Application` 類別中：

```kotlin
import com.google.android.gms.ads.MobileAds

class BlackCatNewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化 AdMob
        MobileAds.initialize(this) {}
    }
}
```

#### 5. 在 Play Console 更新聲明

按照上面「方案 2」的步驟設定。

---

## ✅ 驗證修復

### 1. 本機驗證

```bash
# 建立 Release AAB
./gradlew :composeApp:bundleRelease

# 檢查生成的 AAB 中是否還有 AD_ID 權限
cd composeApp/build/outputs/bundle/release
unzip -l composeApp-release.aab | grep -i "androidmanifest"

# 解壓並查看 Manifest（需要 bundletool）
bundletool dump manifest --bundle=composeApp-release.aab | grep -i "AD_ID"
```

**預期結果**：

- 如果不使用廣告：不應該出現 `AD_ID` 權限
- 如果使用廣告：應該出現 `AD_ID` 權限

### 2. GitHub Actions 驗證

推送更改後，GitHub Actions 會自動建立並上傳：

```bash
git add composeApp/src/androidMain/AndroidManifest.xml
git commit -m "移除 AD_ID 權限以符合 Play Console 聲明"
git push
```

---

## 📚 相關連結

- [Google Play 政策：廣告 ID](https://support.google.com/googleplay/android-developer/answer/6048248)
- [Android 文件：廣告 ID](https://developer.android.com/training/articles/ad-id)
- [Firebase Analytics 隱私](https://firebase.google.com/support/privacy)
- [AdMob 政策指南](https://support.google.com/admob/answer/6128543)
- [使用者資料政策](https://support.google.com/googleplay/android-developer/answer/10787469)

---

## 🎯 總結

### 目前狀態（不使用廣告）

| 問題                     | 解決方案             | 狀態     |
|------------------------|------------------|--------|
| **APK 包含 AD_ID 權限**    | 在 Manifest 中明確移除 | ✅ 已修復  |
| **Play Console 聲明不符**  | 保持聲明為「不使用廣告 ID」  | ✅ 已匹配  |
| **Firebase Analytics** | 繼續使用匿名統計         | ✅ 正常運作 |

### 如果未來要使用廣告

| 步驟                  | 動作                            |
|---------------------|-------------------------------|
| **1. Manifest**     | 移除 `tools:node="remove"` 聲明   |
| **2. Play Console** | 更新廣告 ID 聲明（勾選使用目的）            |
| **3. 隱私權政策**        | 說明廣告 ID 的收集與使用                |
| **4. 添加廣告 SDK**     | 如 AdMob、Meta Audience Network |

---

## ⚠️ 重要提醒

### 關於 Play Console 審查

- ⏱️ **審查時間**：通常幾小時到 1-2 天
- 📝 **聲明一致性**：Manifest 權限必須與 Play Console 聲明一致
- 🔄 **更新頻率**：每次應用程式更新時都會檢查

### 關於隱私權政策

- 📄 **必須詳細說明**：收集哪些資料、如何使用、與誰分享
- 🔗 **必須可存取**：在 Play Store 清單和應用程式中都要有連結
- 🌍 **語言要求**：至少要有英文版本

### 違反政策的後果

- ⚠️ 應用程式可能被拒絕或下架
- ⚠️ 開發者帳號可能被停權
- ⚠️ 所有應用程式可能受影響

---

**最後更新**：2025-01-21
