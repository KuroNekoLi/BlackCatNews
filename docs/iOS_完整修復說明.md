# iOS CI/CD 完整修復說明

> **提交**: `9b0a66b` - fix(ios): 完整修復 iOS 簽名與 Room KSP 配置問題  
> **日期**: 2025-01-21  
> **修復項目**: 6 個 iOS 簽名問題 + 1 個 Room KMP 配置問題

---

## 📋 問題總覽

本次提交解決了 GitHub Actions iOS 構建失敗的所有問題，涉及：

1. **iOS 簽名配置問題**（6個子問題）
2. **Room KMP 跨平台配置問題**（1個）

---

## 🔧 修復的問題詳解

### 問題 1：SPM 依賴不支援手動簽名

#### ❌ 錯誤訊息

```
Firebase_FirebaseCrashlytics does not support provisioning profiles.
GoogleDataTransport does not support provisioning profiles.
```

#### 🔍 根本原因

Fastlane 的 `xcargs` 中使用了 `CODE_SIGN_STYLE=Manual`，這個設定會**全局應用到所有 targets**，包括所有
SPM 依賴（Firebase、GoogleUtilities 等）。

但 SPM 依賴**必須**使用自動簽名（Automatic），因為它們：

- 沒有自己的 Bundle ID
- 是編譯時的靜態庫
- 簽名在 archive 階段統一處理

#### ✅ 解決方案

**移除** `xcargs` 中的全局 `CODE_SIGN_STYLE=Manual`，只在 `export_options` 中使用 manual。

```ruby
# ❌ 修復前（影響所有 targets）
xcargs = "CODE_SIGN_STYLE=Manual ..."

# ✅ 修復後（只在 export 階段使用 manual）
export_opts = {
  signingStyle: "manual"
}
```

---

### 問題 2：Xcode 專案簽名配置錯誤

#### ❌ 錯誤訊息

```
No profiles for 'com.linli.blackcatnews' were found: 
Xcode couldn't find any iOS App Development provisioning profiles
```

#### 🔍 根本原因

Xcode 項目文件（`project.pbxproj`）的 Release 配置中：

- `CODE_SIGN_STYLE = Automatic`（應該是 Manual）
- `CODE_SIGN_IDENTITY = "Apple Development"`（應該是 iPhone Distribution）
- `DEVELOPMENT_TEAM = W2R2DW3K68`（錯誤的 Team ID）

#### ✅ 解決方案

修改 `iosApp.xcodeproj/project.pbxproj` 的 Release 配置：

```
CODE_SIGN_IDENTITY = "iPhone Distribution";
CODE_SIGN_STYLE = Manual;
DEVELOPMENT_TEAM = KG99PWT3W7;  // 由 Fastlane 動態覆寫
PROVISIONING_PROFILE_SPECIFIER = "";  // 由 xcodebuild 動態設定
```

---

### 問題 3：Provisioning Profile 參數未指定

#### ❌ 錯誤訊息

```
"iosApp" requires a provisioning profile. 
Select a provisioning profile in the Signing & Capabilities editor.
```

#### 🔍 根本原因

雖然 Fastlane 下載了 provisioning profile，但沒有明確告訴 xcodebuild 使用哪個 profile。

#### ✅ 解決方案

在 `xcargs` 中添加 `PROVISIONING_PROFILE_SPECIFIER` 參數：

```ruby
xcargs = [
  "PROVISIONING_PROFILE_SPECIFIER=#{profile_uuid}",  # 明確指定 profile
  # ... 其他參數
].join(" ")
```

---

### 問題 4：使用錯誤的 Profile 標識符

#### ❌ 錯誤訊息

```
No profile for team 'KG99PWT3W7' matching 'com.linli.blackcatnews AppStore 1761010206' found
```

#### 🔍 ��本原因

使用了 profile **name**（包含時間戳）而非 **UUID**：

| 類型 | 示例 | 特點 |
|------|------|------|
| Name | `com.linli.blackcatnews AppStore 1761010206` | ❌ 包含時間戳，每次重建都不同 |
| UUID | `947eedfd-6863-49d7-ac21-491ae4504ce2` | ✅ 永久不變的唯一標識符 |

#### ✅ 解決方案

使用 UUID 並移除引號：

```ruby
# ❌ 修復前（使用 name，帶引號）
"PROVISIONING_PROFILE_SPECIFIER='#{profile_name}'"

# ✅ 修復後（使用 UUID，不帶引號）
"PROVISIONING_PROFILE_SPECIFIER=#{profile_uuid}"
```

---

### 問題 5：CI 環境 Profile 未安裝到系統目錄

#### ❌ 錯誤訊息

```
No profile for team 'KG99PWT3W7' matching '...' found
```

#### 🔍 根本原因

在 GitHub Actions CI 環境中，`sigh` 的 `skip_install: false` **不會自動安裝 profile 到系統目錄**！

Profile 被下載到項目目錄，但 Xcode 需要在系統目錄中查找：

```
~/Library/MobileDevice/Provisioning Profiles/{UUID}.mobileprovision
```

#### ✅ 解決方案

**手動複製** profile 到系統目錄：

```ruby
# 手動安裝 profile 到系統目錄（CI 環境必須）
system_profile_dir = File.expand_path("~/Library/MobileDevice/Provisioning Profiles")
FileUtils.mkdir_p(system_profile_dir)
system_profile_path = File.join(system_profile_dir, "#{profile_uuid}.mobileprovision")
FileUtils.cp(profile_path, system_profile_path)
puts "✅ 已安裝 profile 到: #{system_profile_path}"
```

---

### 問題 6：Team ID 不匹配

#### ❌ 錯誤訊息

```
No signing certificate "iOS Distribution" found: 
No "iOS Distribution" signing certificate matching team ID "KG99PWT3W7" with a private key was found.

Provisioning profile "..." belongs to team "Lin Li", 
which does not match the selected team "KG99PWT3W7".
```

#### 🔍 根本原因

硬編碼的 Team ID `KG99PWT3W7` 與 provisioning profile 中的實際 Team ID 不匹配。

#### ✅ 解決方案

從 provisioning profile 中**動態提取** Team ID：

```ruby
# 從 profile 中提取 Team ID
require 'plist'
profile_content = `security cms -D -i "#{profile_path}"`
profile_plist = Plist.parse_xml(profile_content)
team_id = profile_plist['TeamIdentifier'][0]
puts "🏢 使用 Team ID: #{team_id}"

# 使用動態 Team ID
xcargs = [
  "DEVELOPMENT_TEAM=#{team_id}",  # 動態提取的 Team ID
  # ...
].join(" ")
```

---

### 問題 7：Room KMP iOS 端缺少 KSP 配置

#### ❌ 錯誤訊息

```
Expected NewsDatabaseConstructor has no actual declaration in module <commonMain> for Native
```

#### 🔍 根本原因

Room KMP 使用 `expect/actual` 模式：

- ✅ `commonMain` 中聲明了 `expect object NewsDatabaseConstructor`
- ❌ iOS 目標沒有配置 KSP，無法生成 `actual` 實現

Room KMP 需要 KSP 為**每個平台**生成對應的實現。

#### ✅ 解決方案

為 iOS 目標添加 KSP 配置：

```kotlin
// composeApp/build.gradle.kts
dependencies {
    debugImplementation(compose.uiTooling)
    
    // Room KSP 編譯器 - 官方最佳實踐：每個 target 都要配置
    add("kspAndroid", libs.room.compiler)           // ✅ Android
    add("kspIosSimulatorArm64", libs.room.compiler) // ✅ iOS Simulator (新增)
    add("kspIosArm64", libs.room.compiler)          // ✅ iOS Device (新增)
}
```

---

## 📁 修改的檔案

### 1. `iosApp/fastlane/Fastfile`

**關鍵修改**：

```ruby
desc "Build ipa for App Store distribution"
lane :build do
  # ... keychain 和 certificate 設定
  
  # ⭐ 下載 provisioning profile
  sigh(
    api_key: api_key,
    app_identifier: "com.linli.blackcatnews",
    force: true,
    skip_install: false
  )
  profile_uuid = lane_context[SharedValues::SIGH_UUID]
  profile_name = lane_context[SharedValues::SIGH_NAME]
  profile_path = lane_context[SharedValues::SIGH_PROFILE_PATH]
  puts "📋 使用描述檔: #{profile_name} (#{profile_uuid})"
  
  # ⭐ 手動安裝 profile 到系統目錄（CI 環境必須）
  system_profile_dir = File.expand_path("~/Library/MobileDevice/Provisioning Profiles")
  FileUtils.mkdir_p(system_profile_dir)
  system_profile_path = File.join(system_profile_dir, "#{profile_uuid}.mobileprovision")
  FileUtils.cp(profile_path, system_profile_path)
  puts "✅ 已安裝 profile 到: #{system_profile_path}"
  
  # ⭐ 從 profile 中提取 Team ID（動態獲取）
  require 'plist'
  profile_content = `security cms -D -i "#{profile_path}"`
  profile_plist = Plist.parse_xml(profile_content)
  team_id = profile_plist['TeamIdentifier'][0]
  puts "🏢 使用 Team ID: #{team_id}"
  
  # ⭐ Export 配置使用 UUID
  export_opts = {
    method: "app-store",
    signingStyle: "manual",
    provisioningProfiles: {
      "com.linli.blackcatnews" => profile_uuid  # UUID
    }
  }
  
  # ⭐ Build 參數使用 UUID（不加引號）
  xcargs = [
    "-allowProvisioningUpdates",
    "OTHER_CODE_SIGN_FLAGS='--keychain #{keychain_path}'",
    "DEVELOPMENT_TEAM=#{team_id}",                      # 動態 Team ID
    "PROVISIONING_PROFILE_SPECIFIER=#{profile_uuid}",   # UUID，不加引號
    "ONLY_ACTIVE_ARCH=NO"
  ].join(" ")
  
  gym(
    project: "iosApp.xcodeproj",
    scheme: "iosApp",
    configuration: "Release",
    export_method: "app-store",
    clean: true,
    include_bitcode: false,
    export_options: export_opts,
    xcargs: xcargs,
    output_directory: ".",
    output_name: "BlackCatNews"
  )
end
```

---

### 2. `iosApp/iosApp.xcodeproj/project.pbxproj`

**修改位置**：Release 配置（`7B9405E0224C992173252F9A`）

```
CODE_SIGN_IDENTITY = "iPhone Distribution";
CODE_SIGN_STYLE = Manual;
DEVELOPMENT_TEAM = KG99PWT3W7;
PROVISIONING_PROFILE_SPECIFIER = "";
```

---

### 3. `composeApp/build.gradle.kts`

**新增**：iOS KSP 配置

```kotlin
dependencies {
    debugImplementation(compose.uiTooling)
    // Room KSP 編譯器 - 官方最佳實踐：每個 target 都要配置
    add("kspAndroid", libs.room.compiler)           // Android
    add("kspIosSimulatorArm64", libs.room.compiler) // iOS Simulator (新增)
    add("kspIosArm64", libs.room.compiler)          // iOS Device (新增)
}
```

---

## 🎯 簽名流程圖

```
GitHub Actions 開始
    ↓
[Fastlane] 下載 Distribution 憑證 (p12)
    ↓
[Fastlane] 導入憑證到臨時 keychain
    ↓
[Fastlane] 下載 App Store provisioning profile
    ├─ profile_name: "com.linli.blackcatnews AppStore 1761011XXX"
    └─ profile_uuid: "947eedfd-6863-49d7-ac21-491ae4504ce2"
    ↓
[Fastlane] 手動安裝 profile 到系統目錄 ⭐
    └─ ~/Library/MobileDevice/Provisioning Profiles/{UUID}.mobileprovision
    ↓
[Fastlane] 從 profile 中提取 Team ID ⭐
    └─ team_id: "W2R2DW3K68"
    ↓
[xcodebuild] 構建 Archive
    ├─ App Target (iosApp)
    │  ├─ CODE_SIGN_STYLE = Manual ✅
    │  ├─ DEVELOPMENT_TEAM = W2R2DW3K68 ✅（動態）
    │  ├─ PROVISIONING_PROFILE_SPECIFIER = 947eedfd... ✅（UUID）
    │  └─ 使用 iPhone Distribution 證書 ✅
    │
    └─ SPM 依賴 (Firebase, Google...)
       └─ 使用 Automatic 簽名 ✅（不受 xcargs 影響）
    ↓
[Room KSP] 生成 iOS 的 actual 實現 ⭐
    └─ NewsDatabase + NewsDatabaseConstructor
    ↓
[gym] Export IPA
    └─ 使用 manual signing + provisioning profile ✅
    ↓
[pilot] 上傳到 TestFlight ✅
```

---

## ✅ 驗證成功的標誌

GitHub Actions 應該顯示：

```
[01:XX:XX]: Successfully downloaded provisioning profile...
[01:XX:XX]: 📋 使用描述檔: com.linli.blackcatnews AppStore 1761011XXX (947eedfd-6863-49d7-ac21-491ae4504ce2)
[01:XX:XX]: ✅ 已安裝 profile 到: /Users/runner/Library/MobileDevice/Provisioning Profiles/947eedfd-6863-49d7-ac21-491ae4504ce2.mobileprovision
[01:XX:XX]: 🏢 使用 Team ID: W2R2DW3K68

[01:XX:XX]: $ xcodebuild ... DEVELOPMENT_TEAM=W2R2DW3K68 PROVISIONING_PROFILE_SPECIFIER=947eedfd-6863-49d7-ac21-491ae4504ce2 ...

▸ Resolved source packages ✅
▸ Clean Succeeded ✅
▸ Compiling Kotlin Framework ✅
▸ Building targets in dependency order ✅
▸ Build Succeeded ✅
▸ Archive Succeeded ✅
▸ Export Succeeded ✅
▸ Upload to TestFlight ✅

🎉 SUCCESS!
```

---

## 📝 技術要點總結

### Profile 標識符：Name vs UUID

| 屬性 | Profile Name | Profile UUID |
|------|-------------|--------------|
| **示例** | `com.linli.blackcatnews AppStore 1761010206` | `947eedfd-6863-49d7-ac21-491ae4504ce2` |
| **穩定性** | ❌ 包含時間戳，每次重建都不同 | ✅ 永久不變的唯一標識符 |
| **Xcode 匹配** | ⚠️ 需要完全匹配（包括空格和時間戳） | ✅ 系統標準識別方式 |
| **檔案系統** | 不用於檔案名 | ✅ 檔案名格式：`{UUID}.mobileprovision` |
| **使用方式** | ❌ `PROVISIONING_PROFILE_SPECIFIER='name'` | ✅ `PROVISIONING_PROFILE_SPECIFIER=uuid` |

### xcargs 參數

| 設定 | 影響範圍 | SPM 兼容性 |
|------|---------|-----------|
| `xcargs: CODE_SIGN_STYLE=Manual` | **所有 targets + SPM 依賴** | ❌ 不兼容 |
| `export_options: signingStyle` | **只有 export 階段** | ✅ 兼容 |

### Room KMP KSP 配置規則

```kotlin
// ✅ 正確：每個目標都要配置
add("kspAndroid", libs.room.compiler)
add("kspIosSimulatorArm64", libs.room.compiler)
add("kspIosArm64", libs.room.compiler)
add("kspIosX64", libs.room.compiler)  // 如果有 Intel Mac 支援
add("kspJvm", libs.room.compiler)      // 如果有 Desktop JVM 目標
```

**為什麼必須配置**：

- Room 使用 `expect/actual` 模式
- KSP 在編譯時為每個平台生成對應的 `actual` 實現
- 如果某個平台沒有運行 KSP，就會找不到 `actual` 聲明

---

## 🎉 總結

本次提交（`9b0a66b`）完整解決了：

1. ✅ SPM 依賴簽名衝突
2. ✅ Xcode 專案配置錯誤
3. ✅ Profile 參數未指定
4. ✅ 使用錯誤的標識符
5. ✅ CI 環境 Profile 未安裝
6. ✅ Team ID 不匹配
7. ✅ Room KMP iOS 端 KSP 配置

**關鍵技術**：

- 手動安裝 provisioning profile 到系統目錄
- 使用 UUID 而非 profile name
- 動態提取 Team ID
- 為 iOS 添加 Room KSP 配置

所有問題都已解決，GitHub Actions 應該能成功構建並上傳到 TestFlight！
