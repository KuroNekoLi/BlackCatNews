# BlackCatNews 技術文檔

## 📋 目錄

1. [Clean Architecture 架構](#clean-architecture-架構)
2. [Google Sign-In 雙平台實作](#google-sign-in-雙平台實作)
3. [Koin 依賴注入配置](#koin-依賴注入配置)
4. [Firebase Crashlytics 設定](#firebase-crashlytics-設定)
5. [iOS CI/CD 優化](#ios-cicd-優化)

---

## Clean Architecture 架構

本專案採用 **Clean Architecture** 分層架構，確保代碼的可測試性、可維護性和關注點分離。

### 架構層級

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  ViewModel → StateFlow<State>           │
│  (單向資料流 UDF/MVI 模式)                │
└────────────┬────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────┐
│         Domain Layer                    │
│  UseCases (業務邏輯)                     │
│  - SignInWithGoogleUseCase              │
│  - SignInWithAppleUseCase               │
│  - GetArticlesUseCase                   │
└────────────┬────────────────────────────┘
             │
             ▼
┌────────────────────���────────────────────┐
│         Data Layer                      │
│  Repository (AuthManager, etc.)         │
│  DataSource (AuthProvider, API, DB)     │
└─────────────────────────────────────────┘
```

### 認證模組的層級劃分

#### 1. **Presentation Layer** (`core/login`)

- `SignInViewModel` - 管理 UI 狀態和事件
- `SignInScreen` - UI 元件

#### 2. **Domain Layer** (`core/authentication/domain/usecase`)

- `SignInWithGoogleUseCase` - Google 登入業務邏輯
- `SignInWithAppleUseCase` - Apple 登入業務邏輯
- `SignOutUseCase` - 登出業務邏輯

#### 3. **Data Layer** (`core/authentication`)

- `AuthManager` - Repository，管理所有認證供應商
- `GoogleAuthProvider` - Google 認證資料來源（iOS/Android 分別實作）
- `AppleAuthProvider` - Apple 認證資料來源

---

## Google Sign-In 雙平台實作

### ✅ 當前狀態

- **Android**: ✅ 完成（使用 Credential Manager API）
- **iOS**: ✅ 完成（使用 CocoaPods + GoogleSignIn SDK）
- **Apple Sign-In**: ⏸️ 待實作

### Clean Architecture 流程

```
┌──────────────┐
│ LoginScreen  │ 使用者點擊 Google 登入
└──────┬───────┘
       │ viewModel.onGoogleClick()
       ▼
┌──────────────────┐
│ SignInViewModel  │ dispatch(ClickGoogle)
└──────┬───────────┘
       │
       ▼
┌──────────────────────────┐
│ GoogleUIClient           │ 取得 Google 憑證
│ (平台特定實作)            │
└──────┬───────────────────┘
       │ AuthCredential(idToken, accessToken)
       ▼
┌──────────────────────────┐
│ SignInWithGoogleUseCase  │ 驗證輸入 + 業務邏輯
└──────┬───────────────────┘
       │
       ▼
┌──────────────────┐
│ AuthManager      │ 路由到正確的 Provider
└──────┬───────────┘
       │
       ▼
┌──────────────────────┐
│ GoogleAuthProvider   │ 使用 GitLive Firebase SDK
│ (使用 CocoaPods)     │ 完成 Firebase 認證
└──────┬───────────────┘
       │
       ▼
┌──────────────────┐
│   UserSession    │ 返回登入結果
└──────────────────┘
```

### 關鍵配置檔案

#### 1. iOS App 配置 (`iosApp/iosApp/iOSApp.swift`)

```swift
import GoogleSignIn

init() {
    configureFirebase()
    configureGoogleSignIn()  // 必須在啟動時配置
}
```

#### 2. URL Schemes (`iosApp/iosApp/Info.plist`)

必須包含：
- Debug: `com.googleusercontent.apps.181914763371-4i1ekkidl3it9bn5jfukq0318am9ppn4`
- Release: `com.googleusercontent.apps.181914763371-ujrkpnn8jdtmgn5neih2lcnd49gb3t84`

#### 3. CocoaPods 配置 (`core/authentication/build.gradle.kts`)

```kotlin
cocoapods {
    summary = "Authentication module"
    homepage = "https://github.com/linli/BlackCatNews"
    version = "1.0"
    ios.deploymentTarget = "15.0"
    
    pod("GoogleSignIn") {
        version = "~> 7.0"
    }
}
```

### 使用方式

```kotlin
// UI 層 - 非常簡單！
Button(onClick = { viewModel.onGoogleClick() }) {
    Text("使用 Google 登入")
}

// ViewModel 會自動處理整個流程：
// 1. 透過 GoogleUIClient 取得憑證
// 2. 呼叫 SignInWithGoogleUseCase
// 3. UseCase 驗證輸入並呼叫 AuthManager
// 4. AuthManager 路由到 GoogleAuthProvider
// 5. Provider 完成 Firebase 登入
// 6. 返回 UserSession 或錯誤
```

---

## Koin 依賴注入配置

### 模組結構

```kotlin
// core/authentication/AuthModule.kt
val authModule = module {
    // Repository 層
    single { AuthManager(getAll<AuthProvider>().toSet()) }
    
    // Domain 層 UseCases
    singleOf(::SignInWithGoogleUseCase)
    singleOf(::SignInWithAppleUseCase)
    singleOf(::SignOutUseCase)
}

// core/login/LoginModule.kt
val loginModule = module {
    viewModel { (apple: AppleUIClient?, google: GoogleUIClient?) ->
        SignInViewModel(
            signInWithGoogleUseCase = get(),
            signInWithAppleUseCase = get(),
            appleClient = apple,
            googleClient = google
        )
    }
}
```

### ⚠️ 常見錯誤

```
InstanceCreationException: Could not create instance for 'SignInViewModel'
```

**原因**：參數傳遞不完整或順序錯誤

**解決方案**：

```kotlin
// ✅ 正確
val appleClient = getPlatformAppleUIClient()
val googleClient = getPlatformGoogleUIClient()
val viewModel = koinViewModel {
    parametersOf(appleClient, googleClient)
}
```

---

## Firebase Crashlytics 設定

### 功能

- ✅ 自動捕獲崩潰
- ✅ 手動記錄非致命錯誤
- ✅ 測試功能（設定頁面）

### 使用範例

```kotlin
// 記錄非致命錯誤
Firebase.crashlytics.recordException(exception)

// 添加自定義鍵值
Firebase.crashlytics.setCustomKey("user_action", "login_attempt")

// 設定用戶標識
Firebase.crashlytics.setUserId(userId)
```

---

## iOS CI/CD 優化

### 緩存策略

#### 1. CocoaPods 緩存
```yaml
- name: Cache CocoaPods
  uses: actions/cache@v4
  with:
    path: |
      iosApp/Pods
      ~/.cocoapods
    key: ${{ runner.os }}-pods-${{ hashFiles('**/Podfile.lock') }}
```

#### 2. Kotlin/Native 編譯緩存
```yaml
- name: Cache Kotlin/Native compiler
  uses: actions/cache@v4
  with:
    path: ~/.konan
    key: ${{ runner.os }}-konan-${{ hashFiles('**/*.gradle*') }}
```

#### 3. Gradle 緩存

使用官方 `gradle-build-action`，自動處理所有 Gradle 緩存。

### 構建優化

```yaml
- name: Build iOS Framework
  run: ./gradlew :composeApp:podInstall --no-daemon
```

使用 `--no-daemon` 避免在 CI 環境中保持 Gradle daemon。

---

## 疑難排解

### Google Sign-In 常見問題

1. **"Google Sign-In 未配置"**
   - 檢查 `iOSApp.swift` 是否呼叫 `configureGoogleSignIn()`
   - 確認 `GoogleService-Info.plist` 包含 `CLIENT_ID`

2. **"missing support for URL schemes"**
   - 在 `Info.plist` 添加正確的 `REVERSED_CLIENT_ID`

3. **"InstanceCreationException: SignInViewModel"**
   - 確保 `authModule` 和 `loginModule` 都已註冊到 Koin
   - 檢查參數傳遞順序：`parametersOf(appleClient, googleClient)`

### Koin DI 問題

- 啟用 Koin 日誌：`Koin { logger(Level.DEBUG) }`
- 檢查所有模組是否已註冊
- 確認參數傳遞順序正確

### Clean Architecture 最佳實踐

1. **ViewModel 不應該直接依賴 Repository**
   - ❌ 錯誤：`ViewModel → AuthManager`
   - ✅ 正確：`ViewModel → UseCase → AuthManager`

2. **UseCase 應該只有一個職責**
   - 每個 UseCase 處理一個業務邏輯
   - 使用 `operator fun invoke()` 簡化呼叫

3. **平台特定實作應該在最外層**
   - CommonMain 定義介面
   - Platform Main 提供實作
   - 透過 Koin 注入

---

## 相關檔案位置

### Core Modules
- `core/authentication/` - 認證核心邏輯
   - `domain/usecase/` - UseCases（業務邏輯層）
   - `src/commonMain/` - Repository 和介面
   - `src/iosMain/`, `src/androidMain/` - 平台實作
- `core/login/` - 登入 UI 和 ViewModel
- `core/session/` - 使用者 Session 管理

### iOS Specific
- `iosApp/iosApp/iOSApp.swift` - iOS App 入口
- `iosApp/iosApp/Info.plist` - iOS 配置
- `core/authentication/src/iosMain/` - iOS 平台實作

### Android Specific
- `composeApp/src/androidMain/` - Android 平台實作

---

## Text-to-Speech 朗讀

- Android 與 iOS 以平台內建 TTS（Android TextToSpeech、iOS AVSpeechSynthesizer）搭配
  `TextToSpeechManager` 封裝，於 `commonMain` 暴露統一接口。
- `rememberTextToSpeechManager()` 於未支援的目標（如桌面）會回傳 `null`，UI 仍顯示播放按鈕但不會觸發朗讀。
- 語言以 IETF 語言代碼設定，預設為 `en-US`，必要時可由呼叫端覆寫。

---

## 版本資訊

- Kotlin: 2.1.0
- Compose Multiplatform: 1.7.1
- GoogleSignIn iOS SDK: ~> 7.0
- Firebase: 12.4.0
- Koin: 4.0.1

---

## 架構優勢

### 1. **可測試性**

- UseCases 可以獨立測試，不依賴 UI 或平台實作
- Repository 可以用 Mock 替換

### 2. **可維護性**

- 清晰的層級劃分
- 單一職責原則
- 關注點分離

### 3. **可擴展性**

- 新增認證方式只需：
   1. 新增 AuthProvider 實作
   2. 新增對應的 UseCase
   3. 在 UI 層呼叫

### 4. **平台無關**

- 業務邏輯在 CommonMain
- 平台特定實作在各自的 Main
- 透過依賴注入解耦
