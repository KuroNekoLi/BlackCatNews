# Clean Architecture 分析 - 登入流程

## 📊 架構概覽

當前的登入流程採用了 **Clean Architecture** 設計，具有清晰的分層結構：

```
┌─────────────────────────────────────────────────────────────┐
│                      Presentation Layer                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ SignInScreen │→ │SignInViewModel│→ │ SignInContract│      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│           ↓                ↓                                 │
│  ┌──────────────────────────────────────────┐              │
│  │     SocialUIBridge (平台特定 UI)          │              │
│  │  Android: 設置 Activity 到 UIClient       │              │
│  └──────────────────────────────────────────┘              │
└─────────────────────────────────────────────────────────────┘
                         ↓ dispatch(Action)
┌─────────────────────────────────────────────────────────────┐
│                       Domain Layer                           │
│  ┌──────────────┐      ┌──────────────────┐                │
│  │ SignInUseCase│ ←──→ │ UIClientManager  │                │
│  └──────────────┘      └──────────────────┘                │
│         ↓                       ↓                            │
│  ┌─────────────────────────────────────────┐               │
│  │      SignInUIClient (Interface)          │               │
│  │  - GoogleSignInUIClient                  │               │
│  │  - AppleSignInUIClient                   │               │
│  └─────────────────────────────────────────┘               │
│         ↓                                                    │
│  ┌────��────────────────────────────────────┐               │
│  │        Domain Models                     │               │
│  │  - UserSession                           │               │
│  │  - ProviderType                          │               │
│  └─────────────────────────────────────────┘               │
└─────────────────────────────────────────────────────────────┘
                         ↓ signIn(credential)
┌─────────────────────────────────────────────────────────────┐
│                        Data Layer                            │
│  ┌──────────────┐      ┌──────────────────┐                │
│  │  AuthManager │ ←──→ │  AuthProvider    │                │
│  └──────────────┘      └──────────────────┘                │
│         ↓                       ↓                            │
│  ┌─────────────────────────────────────────┐               │
│  │   Platform Implementations               │               │
│  │  - GoogleAuthProvider (Firebase)         │               │
│  │  - AppleAuthProvider (Firebase)          │               │
│  └─────────────────────────────────────────┘               │
│         ↓                                                    │
│  ┌─────────────────────────────────────────┐               │
│  │      External Services                   │               │
│  │  - Firebase Auth                         │               │
│  │  - Credential Manager (Android)          │               │
│  │  - GoogleSignIn SDK (iOS)                │               │
│  └─────────────────────────────────────────┘               │
└─────────────────────────────────────────────────────────────┘
```

## ✅ Clean Architecture 原則評估

### 1. 依賴規則 (Dependency Rule) ✅

**原則**：依賴方向應該從外層指向內層，內層不依賴外層。

**評估**：

```
Presentation → Domain → Data
     ↓           ↓        ↓
   ViewModel  UseCase  Repository
                ↓
            Entities
```

- ✅ **Presentation 依賴 Domain**：`SignInViewModel` 依賴 `SignInUseCase`
- ✅ **Domain 不依賴 Presentation**：`SignInUseCase` 完全不知道 UI 實現
- ✅ **Domain 依賴 Domain Models**：使用 `UserSession`、`ProviderType` 等
- ✅ **Data 層實現 Domain 接口**：`AuthProvider`、`SignInUIClient` 都是接口

### 2. 分層職責 (Separation of Concerns) ✅

#### Presentation Layer

```kotlin
// SignInViewModel.kt
class SignInViewModel(
    private val signInUseCase: SignInUseCase  // ✅ 只依賴 UseCase
) : ViewModel() {
    fun dispatch(action: SignInAction) {
        // ✅ 職責：UI 邏輯、狀態管理、導航
        // ❌ 不包含：業務邏輯、平台特定代碼
    }
}
```

**職責**：

- ✅ 處理 UI 事件
- ✅ 管理 UI 狀態
- ✅ 處理導航和副作用
- ✅ **不包含**業務邏輯

#### Domain Layer

```kotlin
// SignInUseCase.kt
class SignInUseCase(
    private val authManager: AuthManager,
    private val uiClientManager: UIClientManager
) {
    suspend operator fun invoke(providerType: ProviderType): Result<UserSession> {
        // ✅ 職責：業務邏輯、協調、驗證
        // 1. 檢查平台支援
        // 2. 取得憑證
        // 3. 驗證憑證
        // 4. 執行認證
    }
}
```

**職責**：

- ✅ 實現業務邏輯
- ✅ 協調不同組件
- ✅ 定義業務規則
- ✅ **不依賴**框架和平台

#### Data Layer

```kotlin
// GoogleAuthProvider.kt
class GoogleAuthProvider(
    private val auth: FirebaseAuth
) : AuthProvider {
    override suspend fun signIn(credential: AuthCredential): Result<UserSession> {
        // ✅ 職責：與外部服務互動（Firebase）
    }
}
```

**職責**：

- ✅ 實現數據存取
- ✅ 與外部服務互動
- ✅ 實現 Domain 層定義的接口

### 3. 抽象穩定性 (Stable Abstractions Principle) ✅

**Domain 層的抽象接口**：

```kotlin
// SignInUIClient.kt - Domain 接口
interface SignInUIClient {
    val providerType: ProviderType
    suspend fun getCredential(): AuthCredential?
}

// AuthProvider.kt - Domain 接口  
interface AuthProvider {
    val type: ProviderType
    suspend fun signIn(credential: AuthCredential): Result<UserSession>
}
```

- ✅ **接口在 Domain 層定義**
- ✅ **實現在 Data/Infrastructure 層**
- ✅ **依賴反轉原則 (DIP)**：高層模組不依賴低層模組，都依賴抽象

### 4. 測試性 (Testability) ✅

每一層都可以獨立測試：

```kotlin
// ViewModel 測試 - 可以 mock UseCase
class SignInViewModelTest {
    @Test
    fun `test google sign in success`() {
        val mockUseCase = mockk<SignInUseCase>()
        val viewModel = SignInViewModel(mockUseCase)
        // 測試 ViewModel 邏輯
    }
}

// UseCase 測試 - 可以 mock AuthManager 和 UIClientManager
class SignInUseCaseTest {
    @Test
    fun `test sign in flow`() {
        val mockAuthManager = mockk<AuthManager>()
        val mockUIClientManager = mockk<UIClientManager>()
        val useCase = SignInUseCase(mockAuthManager, mockUIClientManager)
        // 測試業務邏輯
    }
}
```

### 5. 依賴注入 (Dependency Injection) ✅

使用 Koin 實現完整的 DI：

```kotlin
// AuthModule.kt
val authModule = module {
    // Data 層
    single { Firebase.auth }
    single { AuthManager(getAll<AuthProvider>().toSet()) }
    
    // Domain 層
    single { UIClientManager(getAll<SignInUIClient>().toSet()) }
    single { SignInUseCase(get(), get()) }
    
    // Presentation 層
    viewModel { SignInViewModel(get()) }
}
```

- ✅ 所有依賴都通過構造函數注入
- ✅ 使用接口而非具體實現
- ✅ 容易替換和測試

## 🔍 特殊設計模式

### 1. Manager Pattern (Repository 層的變體)

```kotlin
// AuthManager - 管理多個 AuthProvider
class AuthManager(
    private val providers: Set<AuthProvider>
) {
    suspend fun signIn(type: ProviderType, credential: AuthCredential) {
        val provider = providers.firstOrNull { it.type == type }
        return provider.signIn(credential)
    }
}

// UIClientManager - 管理多個 SignInUIClient
class UIClientManager(
    private val clients: Set<SignInUIClient>
) {
    suspend fun getCredential(type: ProviderType) {
        val client = clients.firstOrNull { it.providerType == type }
        return client.getCredential()
    }
}
```

**評估**：

- ✅ 符合 Open/Closed 原則：新增供應商不需修改 Manager
- ✅ 符合 Single Responsibility：每個 Provider 只負責一種認證方式
- ✅ 策略模式 (Strategy Pattern)：動態選擇認證策略

### 2. MVI/UDF Pattern (Presentation 層)

```kotlin
// SignInAction → SignInResult → State Update → Effect
fun dispatch(action: SignInAction) {
    val result = when (action) {
        is SignInAction.ClickGoogle -> performSignIn(ProviderType.Google)
        // ...
    }
    _state.value = reduceSignIn(_state.value, result)
}
```

**評估**：

- ✅ 單向數據流
- ✅ 可預測的狀態管理
- ✅ 易於測試和調試

## ⚠️ 潛在問題與改進

### 1. ⚠️ AndroidGoogleUIClient 的 Activity 管理

**當前實現**：

```kotlin
class AndroidGoogleUIClient {
    private var currentActivity: Activity? = null
    
    fun setActivity(activity: Activity) {
        currentActivity = activity
    }
}
```

**問題**：

- ⚠️ **破壞封裝**：Domain 層的實現需要 UI 層手動設置 Activity
- ⚠️ **狀態管理**：Activity 引用可能導致記憶體洩漏
- ⚠️ **依賴方向**：雖然使用 `DisposableEffect`，但仍有隱性依賴

**改進建議**：

#### 方案 A：使用 Composition Local (推薦)

```kotlin
// 在 Presentation 層創建
val LocalActivityProvider = compositionLocalOf<() -> Activity?> { { null } }

// AndroidGoogleUIClient 使用 callback
class AndroidGoogleUIClient(
    private val activityProvider: () -> Activity?
) : GoogleSignInUIClient {
    override suspend fun getCredential(): AuthCredential? {
        val activity = activityProvider() ?: throw IllegalStateException(...)
        // ...
    }
}

// 在 App 層設置
CompositionLocalProvider(
    LocalActivityProvider provides { LocalContext.current as? Activity }
) {
    AppContent()
}
```

**優點**：

- ✅ Activity 引用不被持有
- ✅ 每次調用時動態獲取
- ✅ 生命週期安全

#### 方案 B：使用 Android Context Wrapper

```kotlin
// 創建一個 ActivityContextProvider 接口
interface ActivityContextProvider {
    fun getActivity(): Activity?
}

// 在 App 模組實現
class ComposeActivityContextProvider : ActivityContextProvider {
    override fun getActivity(): Activity? {
        // 使用某種方式獲取當前 Activity
    }
}
```

### 2. ✅ UIClientManager 與 AuthManager 的職責

**當前設計**：

- `UIClientManager`：負責 UI 互動（取得憑證）
- `AuthManager`：負責後端認證（使用憑證登入）

**評估**：

- ✅ 職責明確分離
- ✅ 符合 Single Responsibility Principle
- ✅ **這是正確的設計**，不需要改進

### 3. ✅ SignInUseCase 的職責

**當前實現**：

```kotlin
class SignInUseCase(
    private val authManager: AuthManager,
    private val uiClientManager: UIClientManager
) {
    suspend operator fun invoke(providerType: ProviderType): Result<UserSession> {
        // 1. 檢查支援
        // 2. 取得憑證
        // 3. 驗證憑證
        // 4. 執行認證
    }
}
```

**評估**：

- ✅ 協調 UI 互動和後端認證
- ✅ 包含業務邏輯（驗證、錯誤處理）
- ✅ 符合 Use Case 的定義
- ✅ **設計正確**

## 📊 Clean Architecture 評分

| 原則    | 評分    | 說明                                        |
|-------|-------|-------------------------------------------|
| 依賴規則  | ⭐⭐⭐⭐⭐ | 完全符合，依賴方向正確                               |
| 分層職責  | ⭐⭐⭐⭐⭐ | 職責劃分清晰                                    |
| 抽象穩定性 | ⭐⭐⭐⭐⭐ | 使用接口和依賴反轉                                 |
| 測試性   | ⭐⭐⭐⭐⭐ | 每層都可獨立測試                                  |
| 依賴注入  | ⭐⭐⭐⭐⭐ | 完整的 DI 實現                                 |
| 平台獨立性 | ⭐⭐⭐⭐☆ | Domain 層完全獨立，但 AndroidGoogleUIClient 需要改進 |

**總體評分**：⭐⭐⭐⭐⭐ (4.8/5)

## 🎯 總結

### 優點 ✅

1. **清晰的分層架構**
    - Presentation、Domain、Data 層次分明
    - 依賴方向正確，內層不依賴外層

2. **高度可測試性**
    - 每層都可以獨立測試
    - 使用接口和依賴注入

3. **良好的擴展性**
    - 新增認證供應商只需實現接口
    - Manager 模式支持動態策略選擇

4. **平台獨立性**
    - Domain 層完全平台無關
    - 平台特定實現在 Data 層

5. **現代化設計模式**
    - MVI/UDF 單向數據流
    - Manager/Repository 模式
    - 策略模式

### 需改進 ⚠️

1. **AndroidGoogleUIClient 的 Activity 管理**
    - 建議使用 Composition Local 或 Provider 模式
    - 避免持有 Activity 引用

2. **錯誤處理可以更統一**
    - 考慮定義統一的 Domain Error 類型
    - 減少 String 錯誤訊息的使用

3. **文檔和註釋**
    - 可以添加更多架構決策的文檔
    - 說明為何採用當前設計

## 📚 參考資料

- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Android Clean Architecture Guide](https://developer.android.com/topic/architecture)
- [Kotlin Multiplatform Best Practices](https://kotlinlang.org/docs/multiplatform-mobile-best-practices.html)

---

**結論**：當前的登入流程實現 **高度符合 Clean Architecture 原則**，具有良好的分層、清晰的職責劃分和高度的可測試性。唯一需要改進的是
Android 平台的 Activity 管理方式，建議採用 Composition Local 或 Provider 模式來進一步提升架構質量。
