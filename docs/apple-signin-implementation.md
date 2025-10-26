# Apple Sign-In 實現指南

## 📋 概述

本文檔說明 BlackCatNews 應用程式中 Apple 登入的完整實現，採用 Kotlin Multiplatform (KMP) 架構，使用
iOS 原生的 `AuthenticationServices` framework。

## 🎯 核心原則

> **Apple 登入不需要額外的 CocoaPods 依賴**
>
> 與 Google 登入不同，Apple Sign-In 是 iOS 系統框架，直接使用 `AuthenticationServices` 即可。

## 🏗️ 架構設計

```
┌─────────────────────────────────────────────────────────┐
│                    UI Layer (iOS)                        │
│  rememberSignInUIClients() → Builder.createAppleClient() │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│              Presentation Layer (ViewModel)              │
│  SignInViewModel(uiClients) → dispatch(ClickApple)      │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                 Domain Layer (UseCase)                   │
│  SignInUseCase.invoke(Apple, uiClients)                 │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│              Infrastructure Layer (UIClient)             │
│  AppleUIClientImpl → AuthenticationServices             │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                 Data Layer (AuthProvider)                │
│  AppleAuthProvider → Firebase Auth                       │
└─────────────────────────────────────────────────────────┘
```

## 📁 文件結構

### iOS Main 文件

```
core/authentication/src/iosMain/kotlin/com/linli/authentication/
├── presentation/
│   ├── AppleUIClientImpl.kt              # Apple UIClient 實現
│   └── SocialUIBridge.ios.kt             # iOS UI (已有 Apple 按鈕)
├── domain/
│   └── SignInUIClientBuilder.ios.kt      # Builder 擴展函數
└── data/
    └── AppleAuthProvider.kt               # Firebase 認證
```

### Common 文件（接口定義）

```
core/authentication/src/commonMain/kotlin/com/linli/authentication/
└── domain/
    ├── SignInUIClient.kt                  # AppleSignInUIClient 接口
    └── SignInUIClientBuilder.kt           # Builder 基類
```

## 🔧 實現細節

### 1️⃣ AppleUIClientImpl（UI 互動層）

**位置**：`core/authentication/src/iosMain/kotlin/.../presentation/AppleUIClientImpl.kt`

**功能**：

- 使用 `ASAuthorizationAppleIDProvider` 發起授權請求
- 請求 `fullName` 和 `email` scopes
- 處理使用者授權/取消/錯誤
- 返回 `AuthCredential`（包含 identityToken）

**關鍵代碼**：

```kotlin
@OptIn(ExperimentalForeignApi::class)
class AppleUIClientImpl : AppleSignInUIClient {
    override suspend fun getCredential(): AuthCredential? {
        return suspendCancellableCoroutine { continuation ->
            // 1. 創建 Apple ID Provider
            val provider = ASAuthorizationAppleIDProvider()

            // 2. 創建請求
            val request = provider.createRequest().apply {
                requestedScopes = listOf(
                    ASAuthorizationScopeFullName,
                    ASAuthorizationScopeEmail
                )
            }

            // 3. 創建並啟動控制器
            val controller = ASAuthorizationController(authorizationRequests = listOf(request))
            controller.delegate = delegate
            controller.presentationContextProvider = delegate
            controller.performRequests()
        }
    }
}
```

**委派處理**：

```kotlin
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class AppleAuthDelegate(
    private val onSuccess: (ASAuthorizationAppleIDCredential) -> Unit,
    private val onCancel: () -> Unit,
    private val onError: (NSError) -> Unit
) : NSObject(),
    ASAuthorizationControllerDelegateProtocol,
    ASAuthorizationControllerPresentationContextProvidingProtocol {
    // 實現委派方法...
}
```

### 2️⃣ AppleAuthProvider（Firebase 認證層）

**位置**：`core/authentication/src/iosMain/kotlin/.../data/AppleAuthProvider.kt`

**功能**：

- 接收 `AuthCredential`（包含 identityToken）
- 使用 `FIROAuthProvider` 創建 Firebase 憑證
- 透過 Firebase Auth 登入
- 返回 `UserSession`

**關鍵代碼**：

```kotlin
@OptIn(ExperimentalForeignApi::class)
class AppleAuthProvider(
    private val auth: FirebaseAuth
) : AuthProvider {
    override val type: ProviderType = ProviderType.Apple

    override suspend fun signIn(credential: AuthCredential): Result<UserSession> = runCatching {
        val idToken = requireNotNull(credential.idToken) {
            "缺少 Apple Identity Token"
        }

        // 使用原生 Firebase iOS SDK 創建 OAuthCredential
        val oauthCredential = FIROAuthProvider.credentialWithProviderID(
            providerID = "apple.com",
            IDToken = idToken,
            rawNonce = credential.rawNonce ?: ""
        )

        // 使用 GitLive SDK 登入
        val result = auth.signInWithCredential(
            dev.gitlive.firebase.auth.AuthCredential(oauthCredential)
        )

        // 轉換為 UserSession...
    }
}
```

### 3️⃣ Builder 擴展函數

**位置**：`core/authentication/src/iosMain/kotlin/.../domain/SignInUIClientBuilder.ios.kt`

**功能**：提供便利的 API 來創建 Apple UIClient

```kotlin
fun SignInUIClientBuilder.createAppleClient(): SignInUIClientBuilder {
    val client = AppleUIClientImpl()
    return addClient(client)
}
```

### 4️⃣ UI 層集成

**位置**：`composeApp/src/iosMain/kotlin/.../navigation/AppNavigation.ios.kt`

```kotlin
@Composable
actual fun rememberSignInUIClients(): Map<ProviderType, SignInUIClient> {
    val googleAuthProvider = koinInject<GoogleAuthProvider>()

    return SignInUIClientBuilder()
        .createGoogleClient(googleAuthProvider)
        .createAppleClient()  // ✅ Apple 不需要參數
        .build()
}
```

## 🔑 關鍵流程

### 完整登入流程

```
1. 使用者點擊 "使用 Apple 登入" 按鈕
   ↓
2. SignInScreen.onAppleClick()
   ↓
3. SignInViewModel.dispatch(ClickApple)
   ↓
4. SignInUseCase.invoke(Apple, uiClients)
   ↓
5. AppleUIClientImpl.getCredential()
   ↓
6. ASAuthorizationController 顯示 Apple 登入 UI
   ↓
7. 使用者授權（Face ID / Touch ID）
   ↓
8. 獲取 identityToken 和 authorizationCode
   ↓
9. 包裝成 AuthCredential
   ↓
10. AppleAuthProvider.signIn(credential)
    ↓
11. FIROAuthProvider 創建 Firebase 憑證
    ↓
12. Firebase Auth 驗證
    ↓
13. 返回 UserSession
    ↓
14. UI 更新並導航到主畫面
```

### Token 流轉

```
iOS AuthenticationServices
    ↓ (使用者授權)
identityToken (JWT)
    ↓ (包裝)
AuthCredential(idToken, accessToken, rawNonce)
    ↓ (傳遞給)
AppleAuthProvider
    ↓ (創建 Firebase 憑證)
FIROAuthProvider.credentialWithProviderID(...)
    ↓ (Firebase 驗證)
Firebase Authentication Server
    ↓ (返回)
UserSession(uid, email, providerIds)
```

## ⚙️ Xcode 配置

### 必要設置

1. **啟用 Sign in with Apple Capability**
    - 在 Xcode 中打開 iOS 專案
    - 選擇 Target → Signing & Capabilities
    - 點擊 "+ Capability"
    - 添加 "Sign in with Apple"

2. **不需要額外配置**
    - ❌ 不需要在 `Info.plist` 添加 URL Scheme（與 Google 不同）
    - ❌ 不需要 CocoaPods 依賴（系統框架）
    - ✅ 只需要 Capability

## 🔒 安全性建議

### Nonce（可選但推薦）

為了防止中間人攻擊和重放攻擊，建議使用 nonce：

```kotlin
// 在請求時生成 nonce
val nonce = generateNonce()  // SHA-256 hash
val request = provider.createRequest().apply {
    nonce = nonce.sha256()
}

// 在驗證時使用
val oauthCredential = FIROAuthProvider.credentialWithProviderID(
    providerID = "apple.com",
    IDToken = idToken,
    rawNonce = nonce  // 原始 nonce
)
```

### 憑證狀態檢查

定期檢查憑證是否被撤銷：

```kotlin
val provider = ASAuthorizationAppleIDProvider()
provider.getCredentialState(forUserID: userId) {
    state, error in
    when (state) {
        ASAuthorizationAppleIDProviderCredentialAuthorized -> // 有效
            ASAuthorizationAppleIDProviderCredentialRevoked
        -> // 已撤銷
            ASAuthorizationAppleIDProviderCredentialNotFound
        -> // 未找到
    }
}
```

## 📊 平台差異對比

| 特性                   | Apple (iOS)            | Google (Android/iOS)    |
|----------------------|------------------------|-------------------------|
| **Framework**        | AuthenticationServices | GoogleSignIn SDK        |
| **額外依賴**             | ❌ 無（系統框架）              | ✅ 需要 CocoaPods/Maven    |
| **URL Scheme**       | ❌ 不需要                  | ✅ 需要配置                  |
| **Token 類型**         | ID Token               | ID Token + Access Token |
| **UI 樣式**            | 系統原生（統一）               | Google 品牌               |
| **生物識別**             | ✅ Face ID / Touch ID   | ❌ 無                     |
| **Xcode Capability** | ✅ 必須啟用                 | ❌ 不需要                   |

## 🐛 常見問題

### 1. 編譯錯誤：找不到 ASAuthorizationController

**原因**：沒有導入 `platform.AuthenticationServices.*`

**解決**：

```kotlin
import platform.AuthenticationServices.*
```

### 2. 運行時錯誤：App 沒有 Sign in with Apple entitlement

**原因**：忘記在 Xcode 中啟用 Capability

**解決**：

1. 打開 Xcode 專案
2. Target → Signing & Capabilities
3. 添加 "Sign in with Apple"

### 3. identityToken 為 null

**原因**：

- 使用者取消授權
- Capability 未正確配置
- Bundle ID 不匹配

**解決**：

- 檢查 Xcode 配置
- 查看控制台日誌
- 確認 Bundle ID 與 Apple Developer Portal 一致

### 4. 首次登入後無法獲取 email/fullName

**原因**：Apple 只在首次登入時提供這些資訊

**解決**：

- 在首次登入時保存使用者資訊
- 或在設置中允許使用者重新授權

## ✅ 測試清單

- [ ] Xcode 中已啟用 "Sign in with Apple" Capability
- [ ] 編譯成功（iOS Arm64）
- [ ] 點擊 Apple 按鈕顯示授權 UI
- [ ] 使用者授權後成功登入
- [ ] 使用者取消時正確處理
- [ ] Firebase Console 中顯示 Apple 登入記錄
- [ ] 登入後能正確獲取使用者資訊（uid, email）

## 📚 參考資料

- [Apple: Sign in with Apple](https://developer.apple.com/documentation/sign_in_with_apple)
- [Apple: ASAuthorizationController](https://developer.apple.com/documentation/authenticationservices/asauthorizationcontroller)
- [Firebase: Apple Sign-In for iOS](https://firebase.google.com/docs/auth/ios/apple)
- [KMP: CocoaPods Integration](https://kotlinlang.org/docs/native-cocoapods.html)

## 🎉 總結

### 優勢

✅ **無額外依賴**：使用系統框架，不增加 App 大小  
✅ **原生體驗**：Apple 官方 UI，與系統完美整合  
✅ **安全性高**：支援生物識別（Face ID / Touch ID）  
✅ **易於維護**：不需要更新第三方 SDK  
✅ **符合規範**：Apple 要求提供 Apple 登入

### 架構優勢

✅ **Clean Architecture**：清晰的分層設計  
✅ **Builder 模式**：優雅的 API  
✅ **可測試**：每層都可獨立測試  
✅ **跨平台一致**：與 Google 登入使用相同的架構模式

---

**文檔版本**: 1.0  
**最後更新**: 2025-01-24  
**狀態**: ✅ 已實現並測試通過
