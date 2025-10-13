This is a Kotlin Multiplatform project targeting Android, iOS, Web, Server.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/server](./server/src/main/kotlin) is for the Ktor server application.

* [/shared](./shared/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./shared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Server

To build and run the development version of the server, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :server:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :server:run
  ```

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:
- for the Wasm target (faster, modern browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
    ```
- for the JS target (slower, supports older browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:jsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:jsBrowserDevelopmentRun
    ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

## 本次變更摘要（iOS 上架/自動化）

以下為此次協助你完成的工作與驗證結果，方便團隊後續追蹤：

- 新增 Fastlane 設定（位於 `iosApp/fastlane/`）
    - `Gemfile`、`Fastfile`、`README.md`
    - 提供 `build`（產生 ipa）、`beta`（上傳 TestFlight）、`release`（上傳 App Store Connect） lanes
    - 支援以環境變數讀取 App Store Connect API Key（`ASC_KEY_ID`、`ASC_ISSUER_ID`、`ASC_PRIVATE_KEY` 或
      `ASC_PRIVATE_KEY_PATH`）

- 建立 GitHub Actions workflow（`.github/workflows/ios.yml`）
    - macOS runner、Java 17、Bundler/Fastlane、CocoaPods 快取
    - `main` 預設執行 `beta`，推送 `ios-v*` 標籤時執行 `release`

- iOS 專案調整
    - 新增 `iosApp/Podfile` 並執行 `pod install`，產生 `iosApp.xcworkspace`
    - 更新 `iosApp/iosApp.xcodeproj/project.pbxproj` 的 `FRAMEWORK_SEARCH_PATHS`，加入 `$(inherited)`
      以修正 Pods 警告
    - 更新 `.gitignore` 排除 `iosApp/Pods/` 與 `iosApp/Podfile.lock`
    - 固定 `iosApp/Configuration/Config.xcconfig` 的 `PRODUCT_BUNDLE_IDENTIFIER` 為
      `com.linli.blackcatnews`

- 驗證腳本與排錯
    - 新增 `iosApp/verify_api.rb`：用以產生 JWT 並直接呼叫 Apple `/v1/apps` 檢查 API 金鑰是否有效
    - 以多組 Team/Individual 金鑰測試，皆回 `HTTP 401 NOT_AUTHORIZED`
    - Fastlane `gym` 能成功產生 `BlackCatNews.ipa`（簽章/打包無誤），`pilot` 上傳階段因 API 驗證被拒（401）

- 初步結論與下一步
    - 401 來自 Apple 端對 App Store Connect API 的授權驗證；可能原因：
        - Integrations（整合）「Request Access」尚未完全部署/審核通過
        - 角色/權限伺服器端未就緒（即使 UI 顯示 App 管理/管理員）
        - 帳號有待同意的合約或帳務條款
    - 建議：由帳號持有人重新確認 Integrations 開通狀態與合約；若超過 24 小時仍 401，攜 Team ID、Issuer
      ID、Key ID 與錯誤範例聯絡 Apple Developer Support
    - 在 API 問題解決前，可用 Xcode Organizer/Transporter（Apple ID + App 專用密碼）暫時上傳 TestFlight

### 工作流程範例

**日常開發**：

```bash
# 在 main 或 feature 分支開發，不會觸發發布
git add .
git commit -m "feat: 新增某功能"
git push origin main  # ← 不會觸發發布，可以安心開發
```

**內部測試**：

```bash
# 需要測試時才打 tag
git tag android-internal-v1.0.0
git push --tags
# → GitHub Actions 自動上傳到 internal 軌道
```

**準備公測**：

```bash
# 1. 確保功能完整且經過內部測試
# 2. 打 beta tag
git tag android-beta-v1.0.1
git push --tags
# → GitHub Actions 自動上傳到 beta 軌道
```

**正式發布**：

```bash
# 1. 確保經過 beta 測試無誤
# 2. 打正式版 tag
git tag android-v1.0.1
git push --tags
# → GitHub Actions 自動上傳到 production 軌道
# 
# 或手動觸發（備用方式）：
# 前往 GitHub → Actions → Android Play Deploy (GPP)
# → Run workflow → 選擇 'production' 軌道
```

### Android 自動化發佈（Google Play Console）

### 概述

- 使用 **Gradle Play Publisher (GPP)** 外掛自動上傳 AAB 到 Google Play Console
- 支援多軌道發布：`internal`（內測）、`alpha`、`beta`（公測）、`production`（正式版）
- GitHub Actions 自動化：推送 `main` → `internal`、打 tag → `beta`，正式發布時推送 tag 並自動上傳到
  `production` 軌道

### 專案設定

1. **Gradle Play Publisher 外掛**（已配置於 `composeApp/build.gradle.kts`）
   ```kotlin
   plugins {
       id("com.github.triplet.play") version "3.12.1"
   }
   
   play {
       serviceAccountCredentials.set(file("${project.rootDir}/play-credentials.json"))
       defaultToAppBundles.set(true)
       track.set(project.findProperty("play.track")?.toString() ?: System.getenv("PLAY_TRACK") ?: "internal")
   }
   ```

2. **Release 簽章設定**（從環境變數讀取）
   ```kotlin
   android {
       signingConfigs {
           create("release") {
               storeFile = file(System.getenv("UPLOAD_KEYSTORE"))
               storePassword = System.getenv("UPLOAD_KEYSTORE_PASSWORD")
               keyAlias = System.getenv("UPLOAD_KEY_ALIAS")
               keyPassword = System.getenv("UPLOAD_KEY_PASSWORD")
           }
       }
       buildTypes {
           getByName("release") {
               signingConfig = signingConfigs.getByName("release")
           }
       }
   }
   ```

3. **版本管理**
    - `versionCode`：CI 自動遞增（使用 `github.run_number + 100`），本機預設為 2
    - `versionName`：CI 自動產生（如 `1.0.123`），本機預設為 `1.0`
    - 本機測試時使用預設值，推送到 GitHub 後自動遞增，無需手動修改

### GitHub Actions 觸發策略

| 觸發方式                                                 | 目標軌道         | 使用場景         |
|------------------------------------------------------|--------------|--------------|
| `git tag android-internal-v1.0.0 && git push --tags` | `internal`   | 內部測試、QA 驗證   |
| `git tag android-alpha-v1.0.0 && git push --tags`    | `alpha`      | 封閉測試（特定測試人員） |
| `git tag android-beta-v1.0.0 && git push --tags`     | `beta`       | 公開測試（大規模驗證）  |
| `git tag android-v1.0.0 && git push --tags`          | `production` | 正式發布         |
| 手動觸發（Actions UI）                                     | 自選           | 緊急修復、特殊發布    |

**注意**：為避免每次 push 都觸發發布，本專案僅在**打 tag 時**才會自動發布到 Play Console。

### 必要的 GitHub Secrets

在 **Settings → Secrets and variables → Actions** 新增：

| Secret Name                 | 說明                           | 取得方式                                           |
|-----------------------------|------------------------------|------------------------------------------------|
| `PLAY_CREDENTIALS_JSON_B64` | Service Account JSON（base64） | `base64 -i service-account.json \| tr -d '\n'` |
| `UPLOAD_KEYSTORE_BASE64`    | Upload keystore（base64）      | `base64 -i my_keystore.jks \| tr -d '\n'`      |
| `UPLOAD_KEYSTORE_PASSWORD`  | Keystore 密碼                  | 純文字                                            |
| `UPLOAD_KEY_ALIAS`          | Key alias                    | 純文字                                            |
| `UPLOAD_KEY_PASSWORD`       | Key 密碼                       | 純文字                                            |

### 本地測試

1. **設定環境變數**
   ```bash
   export UPLOAD_KEYSTORE=/path/to/my_keystore.jks
   export UPLOAD_KEYSTORE_PASSWORD='your_password'
   export UPLOAD_KEY_ALIAS='your_alias'
   export UPLOAD_KEY_PASSWORD='your_password'
   ```

2. **建置並上傳到 internal 軌道**
   ```bash
   ./gradlew :composeApp:bundleRelease
   ./gradlew :composeApp:publishReleaseBundle --track internal
   ```

3. **上傳到其他軌道**
   ```bash
   ./gradlew :composeApp:publishReleaseBundle --track beta
   ./gradlew :composeApp:publishReleaseBundle --track production
   ```

### 注意事項
- **首次上傳**：必須先在 Play Console 手動建立 App 並完成一次手動上傳
- **簽章一致性**：Upload keystore 必須與 Play Console 註冊的 Upload key SHA1 一致
- **Service Account 權限**：在 Play Console → Users and permissions 授予 **Release manager** 角色

### 參考資源

- [Gradle Play Publisher 官方文件](https://github.com/Triple-T/gradle-play-publisher)
- [Google Play Console 發布流程](https://support.google.com/googleplay/android-developer/answer/9859152)

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).