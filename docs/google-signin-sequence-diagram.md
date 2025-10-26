# Google 登入流程時序圖

## 📋 概述

本文檔說明 BlackCatNews 應用程式中，使用者按下 Google 登入按鈕後的完整執行流程。

## 🏗️ 架構層次

```
┌─────────────────────────────────────────────────────────┐
│                    UI Layer (Compose)                    │
│  SignInScreen → rememberSignInUIClients()               │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│               Presentation Layer (ViewModel)             │
│  SignInViewModel → dispatch(Action) → performSignIn()   │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                  Domain Layer (UseCase)                  │
│  SignInUseCase → 驗證 → 協調 UIClient 和 AuthManager     │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│            Data Layer (Repository/Manager)               │
│  AuthManager → GoogleAuthProvider → Firebase Auth       │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                 External Services                        │
│  Firebase Authentication Service                         │
└─────────────────────────────────────────────────────────┘
```

---

## 🎬 完整流程時序圖

### Phase 1: 初始化階段（App 啟動時）

```
MainActivity    App.kt    AppNavigation    rememberSignInUIClients    SignInUIClientBuilder
    |            |             |                      |                         |
    |--onCreate()->|            |                      |                         |
    |            |--setup------>|                      |                         |
    |            |             |                      |                         |
    |            |             |--composable-------->|                         |
    |            |             |   SignInRoute        |                         |
    |            |             |                      |                         |
    |            |             |                      |--創建 Builder---------->|
    |            |             |                      |                         |
    |            |             |                      |--獲取 Activity/Provider->|
    |            |             |                      |   (平台特定)             |
    |            |             |                      |                         |
    |            |             |                      |<--UIClients Map---------|
    |            |             |<--uiClients---------|                         |
    |            |             |                      |                         |
    |            |             |--koinViewModel------>|                         |
    |            |             |  (parametersOf)      |                         |
    |            |             |                      |                         |
```

**說明**：

1. `MainActivity` 啟動後載入 `App` composable
2. `AppNavigation` 設置導航路由
3. 在 `SignInRoute` composable 中：
    - 呼叫 `rememberSignInUIClients()` 創建 UIClient Map
    - 使用 Koin `parametersOf` 創建 ViewModel

---

## 📱 詳細流程圖

### Phase 2: 使用者點擊 Google 登入按鈕

```
User  SignInScreen  SignInViewModel  SignInUseCase  AndroidGoogleUIClient  CredentialManager  Firebase
 |         |              |                |                  |                    |             |
 |--點擊--->|              |                |                  |                    |             |
 |         |              |                |                  |                    |             |
 |         |--onGoogleClick()-->           |                  |                    |             |
 |         |              |                |                  |                    |             |
 |         |              |--dispatch(ClickGoogle)            |                    |             |
 |         |              |                |                  |                    |             |
```

### Phase 3: ViewModel 處理事件

```
SignInViewModel  SignInUseCase  UIClients Map  AndroidGoogleUIClient  CredentialManager
      |                |              |                  |                    |
      |--performSignIn(Google)        |                  |                    |
      |                |              |                  |                    |
      |--emit(Loading)                |                  |                    |
      |                |              |                  |                    |
      |--invoke(Google, uiClients)--->|                  |                    |
      |                |              |                  |                    |
      |                |--檢查支援---->|                  |                    |
      |                |              |                  |                    |
      |                |<--GoogleUIClient----------------|                    |
      |                |              |                  |                    |
      |                |--getCredential()--------------->|                    |
      |                |              |                  |                    |
```

### Phase 4: Android 平台獲取 Google 憑證

```
AndroidGoogleUIClient  CredentialManager  Google SDK  User  Firebase SDK
         |                    |               |        |         |
         |--getGoogleCredentials()            |        |         |
         |                    |               |        |         |
         |--getCredential()-->|               |        |         |
         |                    |               |        |         |
         |                    |--請求 Google-->|        |         |
         |                    |    Sign-In    |        |         |
         |                    |               |        |         |
         |                    |               |--顯示---> 選擇器 |
         |                    |               |<-選擇--|         |
         |                    |               |  帳號  |         |
         |                    |               |        |         |
         |                    |<--ID Token----|        |         |
         |                    |               |        |         |
         |<--Credential-------|               |        |         |
         |  (ID Token)        |               |        |         |
         |                    |               |        |         |
         |--GoogleAuthProvider.credential()--------------->     |
         |                    |               |        |         |
         |<--AuthCredential--------------------------------|     |
         |                    |               |        |         |
```

**關鍵步驟**：

1. 使用 Android Credential Manager API
2. 顯示 Google 帳號選擇器
3. 使用者選擇帳號
4. 獲取 Google ID Token
5. 包裝成 Firebase `AuthCredential`

### Phase 5: iOS 平台獲取 Google 憑證

```
GoogleUIClientImpl  GoogleAuthProvider  GoogleSignIn SDK  User  Swift Bridge
        |                  |                   |          |         |
        |--getCredential()-->                  |          |         |
        |                  |                   |          |         |
        |                  |--getCredentialWithViewController()    |
        |                  |                   |          |         |
        |                  |--呼叫 Swift------>|          |         |
        |                  |                   |          |         |
        |                  |                   |--顯示----> Google  |
        |                  |                   |          |  登入   |
        |                  |                   |<-授權---|  視圖   |
        |                  |                   |          |         |
        |                  |<--ID Token--------|          |         |
        |                  |   Access Token    |          |         |
        |                  |                   |          |         |
        |                  |--包裝 AuthCredential          |         |
        |                  |                   |          |         |
        |<--AuthCredential-|                   |          |         |
        |                  |                   |          |         |
```

### Phase 6: UseCase 驗證與處理

```
SignInUseCase  AuthCredential  AuthManager  GoogleAuthProvider  Firebase Auth
      |              |              |               |                 |
      |<--AuthCredential-----------|               |                 |
      |              |              |               |                 |
      |--驗證 idToken              |               |                 |
      |   不為空      |              |               |                 |
      |              |              |               |                 |
      |--authManager.signIn()----->|               |                 |
      |              |              |               |                 |
      |              |              |--找到 Provider->                |
      |              |              |  (GoogleAuthProvider)           |
      |              |              |               |                 |
      |              |              |--signIn()---->|                 |
      |              |              |               |                 |
      |              |              |               |--signInWithCredential()-->
      |              |              |               |                 |
      |              |              |               |                 |--驗證 Token-->
      |              |              |               |                 |   Google    |
      |              |              |               |                 |<--確認有效--|
      |              |              |               |                 |             |
      |              |              |               |<--FirebaseUser--|             |
      |              |              |               |                 |             |
      |              |              |<--UserSession-|                 |             |
      |              |              |               |                 |             |
      |<--Result.Success-----------|               |                 |             |
      |              |              |               |                 |             |
```

**驗證步驟**：

1. 檢查 ID Token 不為空
2. 透過 `AuthManager` 分發到 `GoogleAuthProvider`
3. 使用 Firebase SDK 驗證憑證
4. Firebase 向 Google 確認 Token 有效性
5. 返回 `FirebaseUser` 物件

### Phase 7: 結果返回與 UI 更新

```
SignInUseCase  SignInViewModel  reduceSignIn  SignInScreen  Navigation
      |              |                |             |             |
      |--Result.Success->             |             |             |
      |              |                |             |             |
      |              |--fold(onSuccess)             |             |
      |              |                |             |             |
      |              |--SignInResult.Success        |             |
      |              |                |             |             |
      |              |--reduceSignIn()->             |             |
      |              |                |             |             |
      |              |<--new State----|             |             |
      |              |                |             |             |
      |              |--_state.value = newState     |             |
      |              |                |             |             |
      |              |--emit(NavigateHome)          |             |
      |              |                |             |             |
      |              |                |             |--LaunchedEffect->
      |              |                |             |             |
      |              |                |             |             |--navigate(HomeRoute)-->
      |              |                |             |             |
      |              |                |             |<--顯示主畫面--|
      |              |                |             |             |
```

**UI 更新流程**：

1. UseCase 返回 `Result.Success`
2. ViewModel 轉換為 `SignInResult.Success`
3. Reducer 更新 State
4. 發送 `NavigateHome` Effect
5. Screen 的 `LaunchedEffect` 監聽到 Effect
6. 執行導航到主畫面

---

## 🔄 錯誤處理流程

### 使用者取消登入

```
User  GoogleUIClient  SignInUseCase  SignInViewModel  SignInScreen
 |          |              |                |              |
 |--取消--->|              |                |              |
 |          |              |                |              |
 |          |--return null->                |              |
 |          |              |                |              |
 |          |              |--UserCancelledException      |
 |          |              |                |              |
 |          |              |--Result.Failure->             |
 |          |              |                |              |
 |          |              |                |--fold(onFailure)
 |          |              |                |              |
 |          |              |                |--SignInResult.Failure
 |          |              |                |              |
 |          |              |                |--emit(ShowSnackbar)
 |          |              |                |              |
 |          |              |                |              |--顯示錯誤-->
 |          |              |                |              |   訊息     |
```

### Firebase 認證失敗

```
Firebase  GoogleAuthProvider  AuthManager  SignInUseCase  SignInViewModel
   |              |                 |            |              |
   |--失敗------->|                 |            |              |
   |  Exception   |                 |            |              |
   |              |                 |            |              |
   |              |--Result.Failure->            |              |
   |              |  (ProviderError)             |              |
   |              |                 |            |              |
   |              |                 |--Result.Failure->         |
   |              |                 |            |              |
   |              |                 |            |--Exception-->|
   |              |                 |            |              |
   |              |                 |            |              |--SignInResult.Failure
   |              |                 |            |              |  "Google 登入失敗"
```

---

## 🔑 Token 流轉詳細圖

### Android Token 流程

```
Google       CredentialManager    AndroidGoogleUIClient    Firebase SDK    Firebase Server
Account              |                      |                    |                |
  |                  |                      |                    |                |
  |--使用者授權----->|                      |                    |                |
  |                  |                      |                    |                |
  |                  |--返回 GoogleIdTokenCredential------------>|                |
  |                  |    {                 |                    |                |
  |                  |      idToken: "eyJhbG...",               |                |
  |                  |      id: "user@gmail.com"                |                |
  |                  |    }                 |                    |                |
  |                  |                      |                    |                |
  |                  |                      |--提取 idToken----->|                |
  |                  |                      |                    |                |
  |                  |                      |--GoogleAuthProvider.credential()   |
  |                  |                      |    (idToken, null) |                |
  |                  |                      |                    |                |
  |                  |                      |<--AuthCredential---|                |
  |                  |                      |                    |                |
  |                  |                      |                    |--signInWithCredential()-->
  |                  |                      |                    |   (AuthCredential)        |
  |                  |                      |                    |                           |
  |                  |                      |                    |                |--驗證--->|
  |                  |                      |                    |                |  idToken |
  |                  |                      |                    |                |          |
  |                  |                      |                    |                |<--確認---|
  |                  |                      |                    |<--FirebaseUser-----------|
  |                  |                      |<------------------返回 User 資訊--------------|
```

### iOS Token 流程

```
Google       GoogleSignIn SDK    Swift Bridge    GoogleAuthProvider    Firebase
Account             |                  |                  |                |
  |                 |                  |                  |                |
  |--使用者授權---->|                  |                  |                |
  |                 |                  |                  |                |
  |                 |--返回 GIDGoogleUser                 |                |
  |                 |    {            |                  |                |
  |                 |      idToken: "eyJhbG...",         |                |
  |                 |      accessToken: "ya29.a0..."     |                |
  |                 |    }            |                  |                |
  |                 |                  |                  |                |
  |                 |                  |--callback(idToken, accessToken)-->
  |                 |                  |                  |                |
  |                 |                  |                  |--GoogleAuthProvider.credential()
  |                 |                  |                  |   (idToken, accessToken)
  |                 |                  |                  |                |
  |                 |                  |                  |<--AuthCredential
  |                 |                  |                  |                |
  |                 |                  |                  |--signInWithCredential()-->
  |                 |                  |                  |                |
  |                 |                  |                  |<--FirebaseUser--|
```

**關鍵差異**：

- **Android**: 只使用 ID Token
- **iOS**: 使用 ID Token + Access Token

---

## 📊 數據流轉圖

### 完整數據轉換鏈

```
使用者操作
    ↓
UI Event (點擊按鈕)
    ↓
SignInAction.ClickGoogle
    ↓
ViewModel dispatch
    ↓
providerType: ProviderType.Google
    ↓
SignInUseCase invoke(Google, uiClients)
    ↓
uiClients[Google] → AndroidGoogleUIClient
    ↓
getCredential() → AuthCredential
    ↓
    {
      idToken: "eyJhbGciOiJSUzI1NiIsImtpZCI6Ij...",
      accessToken: null  // Android
    }
    ↓
authManager.signIn(Google, credential)
    ↓
GoogleAuthProvider.signIn(credential)
    ↓
Firebase.signInWithCredential(credential)
    ↓
Result<FirebaseUser>
    ↓
UserSession
    {
      uid: "abc123...",
      email: "user@gmail.com",
      providerIds: ["google.com"]
    }
    ↓
Result.Success(UserSession)
    ↓
SignInResult.Success(session)
    ↓
SignInState(isLoading=false, user=session)
    ↓
SignInEffect.NavigateHome
    ↓
導航到主畫面
```

---

## 🎯 關鍵時間點

| 階段                  | 預估時間      | 說明              |
|---------------------|-----------|-----------------|
| UI 事件 → ViewModel   | < 1ms     | 同步操作            |
| ViewModel → UseCase | < 1ms     | 函數調用            |
| UseCase → UIClient  | 1-3秒      | 顯示 Google 登入 UI |
| UIClient → Firebase | 500ms-2秒  | 網絡請求 + 驗證       |
| Firebase → 結果返回     | 100-500ms | 數據處理            |
| 結果 → UI 更新          | < 10ms    | State 更新        |
| **總計**              | **2-6秒**  | 取決於網絡和使用者操作     |

---

## 🔍 平台差異對比

| 特性            | Android                 | iOS                       |
|---------------|-------------------------|---------------------------|
| **UI Client** | `AndroidGoogleUIClient` | `GoogleUIClientImpl`      |
| **憑證 API**    | Credential Manager      | GoogleSignIn SDK          |
| **創建位置**      | Composable (Activity)   | Composable (AuthProvider) |
| **Token 類型**  | ID Token only           | ID Token + Access Token   |
| **UI 顯示**     | 系統帳號選擇器                 | Google 登入視圖               |
| **原生橋接**      | 無需                      | Swift Bridge              |

---

## 📝 總結

### 流程特點

1. **分層清晰**: UI → Presentation → Domain → Data → External
2. **單向數據流**: MVI/UDF 模式確保可預測性
3. **平台隔離**: expect/actual 機制處理平台差異
4. **可測試性**: 每層都可獨立 mock 和測試
5. **錯誤處理**: 完整的異常捕獲和使用者反饋

### 優勢

✅ **生命週期安全**: 在 Composable 中創建，自動管理生命週期  
✅ **依賴注入**: 使用 Koin parametersOf 靈活注入  
✅ **Builder 模式**: 優雅的 API 設計  
✅ **Clean Architecture**: 完美遵循分層原則  
✅ **跨平台一致**: Android 和 iOS 共享核心邏輯

---

**文檔版本**: 1.0  
**最後更新**: 2025-01-24  
**架構評分**: ⭐⭐⭐⭐⭐ (5/5)
