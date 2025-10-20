# Firebase 双平台设置指南

本指南说明如何在 Firebase Console 中为 BlackCatNews 项目设置 Android 和 iOS 的 Debug 和 Release 环境。

---

## 📱 一、Android 平台设置

### 1.1 Debug 版本设置

#### **步骤 1：在 Firebase Console 添加 Android 应用**

1. 进入 [Firebase Console](https://console.firebase.google.com/)
2. 选择你的项目（或创建新项目）
3. 点击 "添加应用" → 选择 Android 图标
4. 填写以下信息：

**Android 套件名称（必填）**

```
com.linli.blackcatnews.debug
```

> **说明**：Debug 版本使用 `.debug` 后缀，对应你的 `build.gradle.kts` 中的设置：
> ```kotlin
> debug {
>     applicationIdSuffix = ".debug"
> }
> ```

**应用昵称（选填，建议填写）**

```
BlackCatNews (Debug)
```

> **说明**：这个昵称只在 Firebase Console 中显示，方便你区分不同版本

**Debug 签名凭证 SHA-1（选填，建议添加）**

- 获取方式：
  ```bash
  cd ~/AndroidStudioProjects/BlackCatNews
  ./gradlew :composeApp:signingReport
  ```
- 在输出中找到 `Variant: debug` 区块下的 `SHA1` 值
- 示例：`A1:B2:C3:D4:E5:F6:...`

> **注意**：SHA-1 对于使用 Google Sign-In、Dynamic Links 等功能是必需的

#### **步骤 2：下载 google-services.json (Debug)**

1. 点击 "注册应用程序"
2. 下载 `google-services.json` 文件
3. 将文件放置到：
   ```
   composeApp/src/androidDebug/google-services.json
   ```

#### **步骤 3：确认目录结构**

创建目录（如果不存在）：

```bash
mkdir -p composeApp/src/androidDebug
```

---

### 1.2 Release 版本设置

#### **步骤 1：在同一个 Firebase 项目添加另一个 Android 应用**

1. 在 Firebase Console 项目页面
2. 点击 "添加应用" → 选择 Android
3. 填写以下信息：

**Android 套件名称（必填）**

```
com.linli.blackcatnews
```

> **说明**：Release 版本使用完整的 applicationId，没有后缀

**应用昵称（选填，建议填写）**

```
BlackCatNews (Production)
```

**Release 签名凭证 SHA-1（必填）**

- 获取方式：
  ```bash
  cd ~/AndroidStudioProjects/BlackCatNews
  keytool -list -v -keystore /path/to/your/upload-keystore.jks -alias upload
  ```
- 输入 keystore 密码后，复制 `SHA1` 值

> **重要**：Release 版本的 SHA-1 必须使用你的上传密钥（upload key）生成，而不是 debug keystore

#### **步骤 2：下载 google-services.json (Release)**

1. 点击 "注册应用程序"
2. 下载 `google-services.json` 文件
3. 将文件放置到：
   ```
   composeApp/src/androidMain/google-services.json
   ```

或者使用 flavor 方式：

   ```
   composeApp/src/androidRelease/google-services.json
   ```

---

## 🍎 二、iOS 平台设置

### 2.1 Debug 版本设置

#### **步骤 1：在 Firebase Console 添加 iOS 应用**

1. 在 Firebase Console 项目页面
2. 点击 "添加应用" → 选择 iOS 图标
3. 填写以下信息：

**iOS 软件包 ID（必填）**

```
com.linli.blackcatnews.debug
```

> **说明**：iOS Debug 版本也使用 `.debug` 后缀

**应用昵称（选填，建议填写）**

```
BlackCatNews iOS (Debug)
```

**App Store ID（选填）**

- 暂时留空，上架后再填写

#### **步骤 2：下载 GoogleService-Info.plist (Debug)**

1. 点击 "注册应用"
2. 下载 `GoogleService-Info.plist` 文件
3. 将文件添加到 Xcode 项目：
    - 打开 `iosApp/iosApp.xcodeproj`
    - 拖拽 `GoogleService-Info.plist` 到 `iosApp` 文件夹
    - 确保 "Add to targets" 勾选了 `iosApp`
    - 或者直接放置到：
      ```
      iosApp/iosApp/GoogleService-Info-Debug.plist
      ```

#### **步骤 3：在 Xcode 中配置 Debug Bundle ID**

1. 打开 `iosApp/iosApp.xcodeproj`
2. 选择 `iosApp` target
3. 在 `Build Settings` → `Packaging` 中设置：
    - **Debug** 配置下的 `Product Bundle Identifier`：
      ```
      com.linli.blackcatnews.debug
      ```

或者在项目配置中使用条件编译：

```swift
// 在 Info.plist 或 Config.xcconfig 中
PRODUCT_BUNDLE_IDENTIFIER = com.linli.blackcatnews$(BUNDLE_ID_SUFFIX)
```

然后在 Build Settings 中：

- Debug: `BUNDLE_ID_SUFFIX = .debug`
- Release: `BUNDLE_ID_SUFFIX = `

---

### 2.2 Release 版本设置

#### **步骤 1：在同一个 Firebase 项目添加另一个 iOS 应用**

1. 在 Firebase Console 项目页面
2. 点击 "添加应用" → 选择 iOS
3. 填写以下信息：

**iOS 软件包 ID（必填）**

```
com.linli.blackcatnews
```

> **说明**：Production 版本使用完整的 Bundle ID

**应用昵称（选填，建议填写）**

```
BlackCatNews iOS (Production)
```

**App Store ID（选填）**

- 上架后填写实际的 App Store ID

#### **步骤 2：下载 GoogleService-Info.plist (Release)**

1. 点击 "注册应用"
2. 下载 `GoogleService-Info.plist` 文件
3. 将文件放置到：
   ```
   iosApp/iosApp/GoogleService-Info-Release.plist
   ```

#### **步骤 3：在 Xcode 中配置动态加载**

在 `iOSApp.swift` 中添加代码来根据 Build Configuration 加载对应的配置：

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

---

## 🔌 使用 GitLive `firebase-kotlin-sdk`（KMP 共用層）

想在 `commonMain` 直接呼叫 Firebase（如 Firestore、Auth、Analytics）可使用 GitLive 提供的 Kotlin-first
SDK。

參考來源：`firebase-kotlin-sdk` 官方說明（`https://github.com/GitLiveApp/firebase-kotlin-sdk`）。

### A. 新增依賴（Version Catalog 範例）

在 `gradle/libs.versions.toml` 新增版本與依賴（挑選你需要的模組）：

```toml
[versions]
firebase-gitlive = "2.3.0"

[libraries]
gitlive-firebase-app = { module = "dev.gitlive:firebase-app", version.ref = "firebase-gitlive" }
gitlive-firebase-auth = { module = "dev.gitlive:firebase-auth", version.ref = "firebase-gitlive" }
gitlive-firebase-analytics = { module = "dev.gitlive:firebase-analytics", version.ref = "firebase-gitlive" }
gitlive-firebase-crashlytics = { module = "dev.gitlive:firebase-crashlytics", version.ref = "firebase-gitlive" }
gitlive-firebase-firestore = { module = "dev.gitlive:firebase-firestore", version.ref = "firebase-gitlive" }
gitlive-firebase-functions = { module = "dev.gitlive:firebase-functions", version.ref = "firebase-gitlive" }
gitlive-firebase-storage = { module = "dev.gitlive:firebase-storage", version.ref = "firebase-gitlive" }
gitlive-firebase-messaging = { module = "dev.gitlive:firebase-messaging", version.ref = "firebase-gitlive" }
```

在 `composeApp/build.gradle.kts` 的 `kotlin { sourceSets { commonMain.dependencies { ... } } }`
中加入你需要的共用依賴：

```kotlin
commonMain.dependencies {
    implementation(libs.gitlive.firebase.auth)
    implementation(libs.gitlive.firebase.firestore)
    // 視需求加入：analytics/functions/storage/messaging
}
```

> 提示：Crashlytics 若要啟用完整功能，Android 端仍需套用 Google 的 `crashlytics` 與 `google-services`
> 外掛；iOS 端需加入對應的 iOS SDK 套件，詳見下方平台設定。

### B. 平台層必要設定（無 CocoaPods，改用 SPM）

- Android：本文件「三、Gradle 配置」與「一、Android 平台设置」已涵蓋（`google-services.json`、
  `com.google.gms.google-services` 外掛）。
- iOS：因本專案已移除 CocoaPods，需改用 Swift Package Manager 將官方 Firebase iOS SDK 加入到 `iosApp`
  target。

步驟（iOS）：

1) 在 Xcode 選單點選 `File > Add Package Dependencies...`。
2) 貼上來源網址：
   ```
   https://github.com/firebase/firebase-ios-sdk
   ```
3) 勾選你實際會用到的產品（需對應你在 `commonMain` 使用的 GitLive 模組，例如：`FirebaseAuth`、
   `FirebaseFirestore`、`FirebaseAnalytics`、`FirebaseFunctions`、`FirebaseStorage`、`FirebaseMessaging`
   ）。Target 請選擇 `iosApp`。
4) 確認在 Xcode 的 `iosApp` → `General` → `Frameworks, Libraries, and Embedded Content` 能看到剛加入的
   Firebase frameworks。如未顯示，可重啟 Xcode 後再檢查。

> 目的：GitLive 的 iOS 端會橋接官方 Firebase iOS SDK。若沒有透過 SPM（或 CocoaPods）把對應 framework 加入
> iOS 專案，會出現例如 `ld: framework not found FirebaseFirestore` 的連結錯誤。

### C. 初始化建議

- Android：通常會自動初始化；若需手動可於 `Application` 或啟動處呼叫：
  ```kotlin
  import dev.gitlive.firebase.Firebase
  // Firebase.initialize(context) // 視需要呼叫；多數情況 Android 會自動完成
  ```

- iOS：必須在 App 啟動時初始化（Swift 端）。本指南「二、iOS 平台设置」已提供範例：
  ```swift
  import FirebaseCore
  FirebaseApp.configure()
  ```

若想把初始化動作抽到共用層，可建立 `expect/actual`：

```kotlin
// commonMain
import dev.gitlive.firebase.firestore.FirebaseFirestore

expect object FirebaseInitializer {
    fun initialize(context: Any?)
    val firestore: FirebaseFirestore
}
```

```kotlin
// androidMain
import android.content.Context
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore

actual object FirebaseInitializer {
    actual fun initialize(context: Any?) {
        // Android 多半可省略，必要時可啟用：
        // Firebase.initialize(context as? Context)
    }
    actual val firestore: FirebaseFirestore get() = Firebase.firestore
}
```

```kotlin
// iosMain（Swift 已呼叫 FirebaseApp.configure()，此處可不做事）
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.Firebase

actual object FirebaseInitializer {
    actual fun initialize(context: Any?) { /* no-op; Swift already configured */
    }
    actual val firestore: FirebaseFirestore get() = Firebase.firestore
}
```

### D. 範例使用

```kotlin
import dev.gitlive.firebase.firestore.firestore

suspend fun loadUser(uid: String) = FirebaseInitializer.firestore
    .collection("users")
    .document(uid)
    .get()
```

> 更多 API 與涵蓋率請見官方 README（如 `auth`, `analytics`, `database`, `firestore`, `functions`,
`storage`, `messaging`）。

---

## 🔧 三、Gradle 配置（Android）

### 3.1 添加 Google Services Plugin

在 `composeApp/build.gradle.kts` 中添加：

```kotlin
plugins {
    // ... 现有 plugins
    alias(libs.plugins.googleServices)
}
```

在 `gradle/libs.versions.toml` 中添加版本定义：

```toml
[versions]
google-services = "4.4.4"

[plugins]
googleServices = { id = "com.google.gms.google-services", version.ref = "google-services" }
```

### 3.2 添加 Firebase Crashlytics Plugin（必需！）

**重要**：如果你使用 Crashlytics，必须添加这个插件，否则会出现 "Crashlytics build ID is missing" 错误。

在 `composeApp/build.gradle.kts` 中添加：

```kotlin
plugins {
    // ... 现有 plugins
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)  // 必需！生成 Crashlytics Build ID
}
```

在 `gradle/libs.versions.toml` 中添加版本定义：

```toml
[versions]
firebase-crashlytics = "3.0.6"

[plugins]
firebaseCrashlytics = { id = "com.google.firebase.crashlytics", version.ref = "firebase-crashlytics" }
```

> **为什么需要这个插件？**
> - Firebase Crashlytics 需要在编译时生成一个唯一的 Build ID
> - 这个 ID 用于关联崩溃报告和对应的代码版本
> - 没有这个插件，应用启动时会抛出 `IllegalStateException`

### 3.3 添加 GitLive Firebase Kotlin SDK（跨平台支持）

本项目使用 [GitLive Firebase Kotlin SDK](https://github.com/GitLiveApp/firebase-kotlin-sdk) 来实现跨平台的
Firebase 功能。

在 `gradle/libs.versions.toml` 中添加版本和依赖：

```toml
[versions]
firebase-gitlive = "2.3.0"

[libraries]
gitlive-firebase-app = { module = "dev.gitlive:firebase-app", version.ref = "firebase-gitlive" }
gitlive-firebase-auth = { module = "dev.gitlive:firebase-auth", version.ref = "firebase-gitlive" }
gitlive-firebase-analytics = { module = "dev.gitlive:firebase-analytics", version.ref = "firebase-gitlive" }
gitlive-firebase-crashlytics = { module = "dev.gitlive:firebase-crashlytics", version.ref = "firebase-gitlive" }
```

在 `composeApp/build.gradle.kts` 的 `commonMain.dependencies` 中添加：

```kotlin
commonMain.dependencies {
    // ... 其他依赖

    // GitLive Firebase Kotlin SDK - 只使用需要的功能
    implementation(libs.gitlive.firebase.app)        // 核心依赖，必须包含
    implementation(libs.gitlive.firebase.auth)       // 用户认证
    implementation(libs.gitlive.firebase.analytics)  // 分析统计
    implementation(libs.gitlive.firebase.crashlytics) // 崩溃报告
}
```

> **重要提示**：
> - `firebase-app` 是核心依赖，**必须包含**
> - 其他模块根据需求添加
> - 如果需要 Firestore、Storage、Messaging 等，可以添加对应的依赖

### 3.4 完整的 plugins 配置示例

```kotlin
// composeApp/build.gradle.kts
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    id("com.github.triplet.play") version "3.12.1"
    alias(libs.plugins.googleServices)        // Google Services Plugin
    alias(libs.plugins.firebaseCrashlytics)   // Firebase Crashlytics Plugin
}
```

### 3.5 配置 Build Variants

你的项目已经配置好了 debug 和 release 的 applicationIdSuffix：

```kotlin
buildTypes {
    debug {
        applicationIdSuffix = ".debug"
        versionNameSuffix = "-debug"
    }
    release {
        isMinifyEnabled = false
        if (hasUploadKeystore) {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

## 📂 四、最终目录结构

```
BlackCatNews/
├── composeApp/
│   ├── src/
│   │   ├── androidMain/
│   │   │   └── google-services.json          # Release 配置
│   │   ├── androidDebug/
│   │   │   └── google-services.json          # Debug 配置
│   │   └── androidRelease/
│   │       └── google-services.json          # 或者放这里
│   └── build.gradle.kts
├── iosApp/
│   └── iosApp/
│       ├── GoogleService-Info-Debug.plist    # iOS Debug 配置
│       ├── GoogleService-Info-Release.plist  # iOS Release 配置
│       └── iOSApp.swift
└── build.gradle.kts
```

---

## ✅ 五、验证设置

### Android

运行 debug 版本：

```bash
./gradlew :composeApp:assembleDebug
```

运行 release 版本：

```bash
./gradlew :composeApp:assembleRelease
```

检查生成的 APK 是否包含正确的 `google-services.json`

### iOS

1. 在 Xcode 中选择 Debug scheme 并运行
2. 检查 Firebase Console 中是否收到 debug 版本的事件
3. 切换到 Release scheme 并运行
4. 检查是否使用了正确的配置

---

## 🎯 六、总结对照表

| 平台      | 环境      | Package/Bundle ID              | 配置文件位置                                             | Firebase 昵称                   |
|---------|---------|--------------------------------|----------------------------------------------------|-------------------------------|
| Android | Debug   | `com.linli.blackcatnews.debug` | `composeApp/src/androidDebug/google-services.json` | BlackCatNews (Debug)          |
| Android | Release | `com.linli.blackcatnews`       | `composeApp/src/androidMain/google-services.json`  | BlackCatNews (Production)     |
| iOS     | Debug   | `com.linli.blackcatnews.debug` | `iosApp/iosApp/GoogleService-Info-Debug.plist`     | BlackCatNews iOS (Debug)      |
| iOS     | Release | `com.linli.blackcatnews`       | `iosApp/iosApp/GoogleService-Info-Release.plist`   | BlackCatNews iOS (Production) |

---

## 💡 七、常见问题

### Q1: 为什么要分开 Debug 和 Release 配置？

**答**：

- 避免测试数据污染生产环境
- 可以使用不同的 Firebase 功能配置（如 Analytics、Crashlytics）
- 方便团队协作，测试环境独立
- App Store 和 Google Play 审核时不会触发测试事件

### Q2: SHA-1 证书指纹必须添加吗？

**答**：

- 如果使用 Google Sign-In：**必须**
- 如果使用 Firebase Dynamic Links：**必须**
- 如果只使用 Analytics、Crashlytics：**可选**
- 建议无论如何都添加，避免未来功能扩展时出问题

### Q3: iOS 可以像 Android 一样自动切换配置文件吗？

**答**：iOS 不支持像 Android 那样的 source sets，需要：

- 方案 1：使用不同文件名 + 代码动态加载（推荐）
- 方案 2：使用 Xcode Build Phases 脚本复制对应文件
- 方案 3：使用 Swift Package Manager 配置不同 targets

### Q4: 如何在代码中判断当前环境？

**Android (Kotlin)**：

```kotlin
val isDebug = BuildConfig.DEBUG
```

**iOS (Swift)**：

```swift
#if DEBUG
print("Debug mode")
    #else
print("Release mode")
    #endif
```

### Q5: 为什么不使用 CocoaPods？

**答**：本项目使用 Kotlin Multiplatform 的 `embedAndSignAppleFrameworkForXcode` 任务直接编译和集成
iOS framework，不需要 CocoaPods。这种方式：

- 更简单，减少依赖
- 编译速度更快
- 避免 CocoaPods 版本冲突
- 与 Kotlin Multiplatform 集成更好

---

## 📞 需要帮助？

如果在设置过程中遇到问题：

1. 检查 Package/Bundle ID 是否完全匹配
2. 确认配置文件放置在正确位置
3. 检查 Firebase Console 中应用状态是否正常
4. 查看 Gradle sync 或 Xcode build 日志中的错误信息

---

**最后更新**：2025-01-19

