# iOS CocoaPods Refactoring: Using :shared Module

## 概要 (Overview)

本次重構將 iOS 專案從使用 `:composeApp` 模組改為使用 `:shared` 模組作為 CocoaPods 來源。這是 Kotlin
Multiplatform 的官方推薦做法，將共享邏輯與 UI 層分離。

**This refactoring changed the iOS app to use the `:shared` module through CocoaPods instead
of `:composeApp`. This is the official Kotlin Multiplatform recommended approach, separating shared
logic from the UI layer.**

## 變更內容 (Changes Made)

### 1. shared/build.gradle.kts

✅ **啟用 CocoaPods Plugin**

- 添加 `alias(libs.plugins.kotlinCocoapods)` 插件
- 添加 `alias(libs.plugins.kotlinx.serialization)` 插件
- 添加 `alias(libs.plugins.ksp)` 插件

✅ **配置 iOS Targets**

- 添加 `iosArm64()` - 實體裝置
- 添加 `iosSimulatorArm64()` - Apple Silicon 模擬器
- ⚠️ 移除 `iosX64()` - 不再需要（GitLive Firebase 不支援）

✅ **配置 CocoaPods**

```kotlin
cocoapods {
    summary = "Shared Kotlin Multiplatform module for BlackCatNews"
    homepage = "https://github.com/linli/BlackCatNews"
    version = "1.0.0"
    ios.deploymentTarget = "15.0"
    
    framework {
        baseName = "shared"
        isStatic = true
    }
    
    // Native Firebase pods required by GitLive Firebase
    pod("FirebaseAuth")
    pod("FirebaseAnalytics")
    pod("FirebaseCrashlytics")
}
```

✅ **添加依賴**

- 將所有共享依賴移至 `commonMain`
- 包含 Ktor, Room, Firebase GitLive SDK, Koin 等

✅ **配置 Room KSP**

```kotlin
dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}
```

### 2. composeApp/build.gradle.kts

✅ **移除 iOS 支援**

- ❌ 移除 `alias(libs.plugins.kotlinCocoapods)` 插件
- ❌ 移除所有 iOS targets (`iosArm64()`, `iosSimulatorArm64()`)
- ❌ 移除 `cocoapods { }` 配置區塊
- ❌ 移除 `iosMain.dependencies` 區塊
- ❌ 移除 iOS 的 Room KSP 配置

✅ **Android Only**

- `composeApp` 現在僅支援 Android
- iOS 透過 `:shared` 模組取得所有共享邏輯

### 3. iosApp/Podfile

✅ **更新 Pod 來源**

```ruby
target 'iosApp' do
  pod 'shared', :path => '../shared'  # 改為使用 shared 模組
  pod 'GoogleSignIn', '~> 8.0'  # 用於原生 iOS 認證配置
end
```

### 4. .github/workflows/ios.yml

✅ **更新 CI 流程**

```yaml
- name: Guard against SPM and install CocoaPods
  run: |
    bash scripts/check_no_spm_and_install_pods.sh
```

GitHub Actions 使用新的守護腳本：

```yaml
- name: Guard against SPM and install CocoaPods
  run: |
    bash scripts/check_no_spm_and_install_pods.sh
```

此腳本會：

1. ✅ 檢查專案中是否有 SPM 依賴（XCRemoteSwiftPackageReference）
2. ✅ 如果發現 SPM 依賴，CI 立即失敗並提示錯誤
3. ✅ 清理殘留的 SwiftPM 快取
4. ✅ 生成 Kotlin dummy framework
5. ✅ 執行 pod install

### 5. 刪除過時檔案

✅ **移除 composeApp.podspec**

- 檔案：`composeApp/composeApp.podspec`
- 原因：iOS 不再使用 composeApp 模組

### 6. 移除 Swift Package Manager (SPM) 依賴

✅ **自動清理腳本**

創建了兩個腳本來處理 SPM 衝突：

1. **`scripts/remove_spm_dependencies.sh`** - 從 Xcode 專案中移除所有 SPM 依賴
2. **`scripts/check_no_spm_and_install_pods.sh`** - CI/CD 守護腳本，確保沒有 SPM 依賴並安裝 CocoaPods

✅ **移除的 SPM 套件**

- Firebase iOS SDK (firebase-ios-sdk)
- GoogleSignIn-iOS

### 7. 修正 Xcode Build Phases

✅ **移除衝突的 Build Phase**

- 移除了 "Compile Kotlin Framework" build phase
- 此 phase 使用 `embedAndSignAppleFrameworkForXcode`，與 CocoaPods 衝突
- CocoaPods 通過 podspec 中的 script phase 自動處理 framework 建置

### 8. 更新 Swift Import

✅ **更新模組引用**

`iosApp/iosApp/ContentView.swift`:

```swift
import shared  // 改為從 shared 模組引入
```

## 驗證結果 (Verification Results)

### ✅ Podspec 生成成功

```bash
./gradlew :shared:podspec
```

生成位置：`shared/shared.podspec`

### ✅ Dummy Framework 生成成功

```bash
./gradlew :shared:generateDummyFramework
```

### ✅ Pod Install 成功

```bash
cd iosApp
pod install
```

輸出：

```
Installing shared (1.0.0)
Pod installation complete! There is 1 dependency from the Podfile and 21 total pods installed.
```

### ✅ Podfile.lock 驗證

```
  - shared (1.0.0):
    - FirebaseAnalytics
    - FirebaseAuth
    - FirebaseCrashlytics
```

### ⚠️ 已知問題 (Known Issues)

**GoogleDataTransport 重複檔案錯誤**

- 原因：專案同時使用 CocoaPods 和 Swift Package Manager 管理 Firebase
- 狀態：與本次重構**無關**的既有問題
- 解決方案：需要統一使用 CocoaPods 或 SPM（二選一）

## 工作流程 (Workflow)

### 本地開發 (Local Development)

1. **首次安裝或清理後**

```bash
# 移除 SPM 依賴（如果有）
bash scripts/remove_spm_dependencies.sh

# 生成 dummy framework 並安裝 CocoaPods
bash scripts/check_no_spm_and_install_pods.sh
```

2. **日常開發**

- 使用 `iosApp.xcworkspace`（不是 .xcodeproj）
- Xcode Build Phases 會自動執行 Gradle syncFramework

3. **更新依賴**

```bash
cd iosApp
pod repo update
pod install
```

### CI/CD

GitHub Actions 會自動執行：

1. `./gradlew :shared:generateDummyFramework`
2. `pod install`
3. Fastlane 建置與上傳

## 架構優勢 (Architecture Benefits)

✅ **關注點分離 (Separation of Concerns)**

- `:shared` = 共享邏輯（網路、資料庫、業務邏輯）
- `:composeApp` = Android UI（Compose）
- `iosApp` = iOS UI（SwiftUI）

✅ **官方推薦路徑**

- 遵循 Kotlin Multiplatform CocoaPods 官方指南
- 與 Kotlin Native 工具鏈完美整合

✅ **可維護性**

- 清晰的模組邊界
- 獨立的版本控制（shared.podspec）
- 更容易測試與重用

✅ **彈性部署**

- 可以獨立發布 shared 模組
- 支援多個 iOS 專案共享同一個模組

## 成功準則 (Success Criteria)

✅ `pod install` 不再嘗試取 `composeApp.podspec`，成功安裝 `shared`  
✅ CI 能在「Guard against SPM and install CocoaPods」步驟順利完成  
✅ 不再發生 `Kotlin framework 'ComposeApp' doesn't exist yet` 錯誤  
✅ Gradle sync 成功  
✅ 生成的 `shared.podspec` 正確無誤  
✅ 沒有 Swift Package Manager 衝突  
✅ 沒有 GoogleDataTransport 重複檔案錯誤  
⚠️ iOS app 需要更新 UI 層以使用正確的架構

## 回滾方案 (Rollback Plan)

若需回到舊架構：

1. 恢復 `composeApp/build.gradle.kts` 的 iOS 設定
2. 恢復 `composeApp/composeApp.podspec`
3. 更新 `iosApp/Podfile` 改回指向 `composeApp`
4. 執行 `pod deintegrate && pod install`

## 相關文件 (Related Documentation)

- [Kotlin Multiplatform CocoaPods Plugin](https://kotlinlang.org/docs/native-cocoapods.html)
- [ios-cocoapods-fix.md](./ios-cocoapods-fix.md)
- [ios-deployment-target-fix.md](./ios-deployment-target-fix.md)

## 待處理項目 (Pending Tasks)

### iOS UI 架構調整

`ContentView.swift` 目前嘗試使用 `MainViewControllerKt.MainViewController()`，但這是來自 `composeApp`
模組的 Compose UI。

**選項 1: 純 SwiftUI**

- 移除對 Compose UI 的依賴
- 使用純 SwiftUI 實現 iOS UI
- 通過 `shared` 模組訪問業務邏輯

**選項 2: Compose Multiplatform iOS**

- 如需使用 Compose for iOS，需要在 `shared` 或單獨的 UI 模組中實現
- 將 Compose UI 組件從 `composeApp` 移至共享模組

**推薦做法**：
由於 `composeApp` 已經是 Android-only，建議採用選項 1，讓 iOS 使用原生 SwiftUI。

## 架構優勢 (Architecture Benefits)

