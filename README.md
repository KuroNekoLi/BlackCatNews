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

### 本地執行範例

1) 使用 Fastlane 上傳（需先成功 Archive 一次）

```bash
cd iosApp
export ASC_KEY_ID=<你的 Key ID>
export ASC_ISSUER_ID=<你的 Issuer ID>
export ASC_PRIVATE_KEY_PATH=<你的 .p8 絕對路徑>
BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane beta
```

2) 驗證 API 金鑰（直接呼叫 Apple API）

```bash
cd iosApp
export ASC_KEY_ID=<你的 Key ID>
export ASC_ISSUER_ID=<你的 Issuer ID>
export ASC_PRIVATE_KEY_PATH=<你的 .p8 絕對路徑>
ruby verify_api.rb
```

若看到 `HTTP 401`，表示 API 權限/開通仍有問題；請依「初步結論與下一步」處理。

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).