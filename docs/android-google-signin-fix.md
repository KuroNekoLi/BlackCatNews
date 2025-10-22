# Android Google 登入無法找到 Activity 問題修復

## 問題描述

在 Android 平台上執行 Google 登入時，`GoogleUIClientImpl` 無法找到 Activity，拋出異常：

```
IllegalStateException: 無法找到 Activity context
```

## 根本原因

### 問題分析

1. **Credential Manager 需要 Activity Context**
    - Android 的 Credential Manager API 必須使用 Activity Context 才能顯示登入 UI
    - 但在 Koin 依賴注入模組中，只能獲取 Application Context

2. **原本的實現嘗試從 Application Context 查找 Activity**
   ```kotlin
   // PlatformLoginModule.kt (舊版)
   single<SignInUIClient> {
       val context = androidContext()  // 這是 Application Context！
       GoogleUIClientImpl(
           context = context,  // 傳入 Application Context
           serverClientId = serverClientId
       )
   }
   ```

3. **為什麼會失敗**
    - `GoogleUIClientImpl` 嘗試使用 `findActivity()` 函數從 Context 中遞迴尋找 Activity
    - 但 **Application Context 不是 ContextWrapper，也不包裝 Activity**
    - 因此 `findActivity()` 總是返回 `null`，導致拋出異常

   ```kotlin
   // GoogleUIClientImpl.kt (舊版)
   private fun Context.findActivity(): Activity? {
       var context = this
       while (context is ContextWrapper) {  // Application Context 不是 ContextWrapper
           if (context is Activity) return context
           context = context.baseContext
       }
       return null  // 總是返回 null
   }
   ```

## 解決方案

### 採用方案：動態設置 Activity

創建一個新的 `AndroidGoogleUIClient` 類別，支援在運行時動態設置 Activity：

#### 1. 創建 `AndroidGoogleUIClient`

```kotlin:core/authentication/src/androidMain/kotlin/com/linli/authentication/presentation/AndroidGoogleUIClient.kt
class AndroidGoogleUIClient(
    private val serverClientId: String
) : GoogleSignInUIClient {

    private var currentActivity: Activity? = null

    fun setActivity(activity: Activity) {
        currentActivity = activity
    }

    fun clearActivity() {
        currentActivity = null
    }

    override suspend fun getCredential(): AuthCredential? {
        val activity = currentActivity
            ?: throw IllegalStateException("無法找到 Activity")
        
        // 使用 activity 執行 Google 登入
        val idToken = activity.getGoogleIdToken(serverClientId)
        return AuthCredential(idToken = idToken, accessToken = null)
    }
}
```

#### 2. 在 Koin 模組中註冊

```kotlin:core/authentication/src/androidMain/kotlin/com/linli/authentication/presentation/PlatformLoginModule.kt
val platformLoginModule = module {
    single<SignInUIClient> {
        val context = androidContext()
        val serverClientId = // ... 從 resources 讀取
        AndroidGoogleUIClient(serverClientId = serverClientId)
    }
}
```

#### 3. 在 UI 層設置 Activity

使用 `DisposableEffect` 在 Composable 渲染時設置 Activity：

```kotlin:core/authentication/src/androidMain/kotlin/com/linli/authentication/presentation/SocialUIBridge.android.kt
@Composable
actual fun SocialMediaButtonListPlatformSpecificUI(
    onAppleClicked: () -> Unit,
    onGoogleClicked: () -> Unit,
    onFacebookClicked: () -> Unit
) {
    val context = LocalContext.current
    val signInUIClient = koinInject<SignInUIClient>()
    
    // 當 Composable 進入/離開時設置/清除 Activity
    DisposableEffect(context) {
        val activity = context as? Activity
        if (activity != null && signInUIClient is AndroidGoogleUIClient) {
            signInUIClient.setActivity(activity)
        }
        
        onDispose {
            if (signInUIClient is AndroidGoogleUIClient) {
                signInUIClient.clearActivity()
            }
        }
    }
    
    // 登入按鈕...
}
```

#### 4. 清理舊代碼

刪除已廢棄的 `GoogleUIClientImpl.kt`（Android 版本）：

- ❌ 已刪除：
  `core/authentication/src/androidMain/kotlin/com/linli/authentication/presentation/GoogleUIClientImpl.kt`
- ✅ 新實現：
  `core/authentication/src/androidMain/kotlin/com/linli/authentication/presentation/AndroidGoogleUIClient.kt`
- ℹ️ iOS 版本的 `GoogleUIClientImpl` 保持不變，繼續使用

## 優勢

1. **保持架構一致性**
    - 仍然使用依賴注入和統一的 `SignInUIClient` 接口
    - ViewModel 和 UseCase 層不需要修改

2. **生命週期管理**
    - 使用 `DisposableEffect` 自動管理 Activity 的生命週期
    - 當登入畫面離開時自動清理 Activity 引用，避免記憶體洩漏

3. **類型安全**
    - 在 UI 層進行 Activity 的類型檢查
    - 明確的錯誤訊息

## 其他考慮的方案

### 方案 A：工廠模式（未採用）

使用 Koin 的 `factory` 並在使用時傳入 Activity：

```kotlin
factory { (activity: Activity) -> GoogleUIClientImpl(activity, serverClientId) }
```

**缺點**：需要修改 `UIClientManager` 和整個調用鏈

### 方案 B：直接在 ViewModel 處理（未採用）

在 ViewModel 層直接處理 Activity：

```kotlin
viewModel.signIn(activity, providerType)
```

**缺點**：破壞了架構的分層設計，ViewModel 不應該知道 Android 特定的 Activity

## 測試驗證

編譯成功：

```bash
./gradlew :core:authentication:compileAndroidMain
./gradlew :composeApp:assembleDebug
```

## 相關文件

### 新增/修改的文件

- ✅
  `core/authentication/src/androidMain/kotlin/com/linli/authentication/presentation/AndroidGoogleUIClient.kt` -
  新增
- ✅
  `core/authentication/src/androidMain/kotlin/com/linli/authentication/presentation/PlatformLoginModule.kt` -
  修改
- ✅
  `core/authentication/src/androidMain/kotlin/com/linli/authentication/presentation/SocialUIBridge.android.kt` -
  修改
- ✅ `core/authentication/src/commonMain/kotlin/com/linli/authentication/domain/SignInUIClient.kt` -
  更新註釋

### 已刪除的文件

- ❌
  `core/authentication/src/androidMain/kotlin/com/linli/authentication/presentation/GoogleUIClientImpl.kt` -
  已刪除

### 不變的文件

- ℹ️
  `core/authentication/src/androidMain/kotlin/com/linli/authentication/GoogleCredentialHelper.kt` -
  保持不變
- ℹ️
  `core/authentication/src/iosMain/kotlin/com/linli/authentication/presentation/GoogleUIClientImpl.kt` -
  iOS 版本保持不變

## 總結

通過創建 `AndroidGoogleUIClient` 並在 UI 層動態設置 Activity，我們成功解決了 Android 平台 Google
登入無法找到 Activity 的問題。同時刪除了已廢棄的 `GoogleUIClientImpl`（Android 版本），保持代碼庫的整潔和一致性。
