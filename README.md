# Black Cat News - AI 雙語新聞學習

這是一個 Kotlin Multiplatform 專案，支援 Android、iOS、Web 與 Server。

## 專案結構

* [/composeApp](./composeApp/src) - 跨平台共享的 Compose Multiplatform 應用程式碼
    - [commonMain](./composeApp/src/commonMain/kotlin) - 所有平台共用的程式碼
    - 其他資料夾為平台專屬的 Kotlin 程式碼

* [/iosApp](./iosApp/iosApp) - iOS 應用程式進入點，包含 SwiftUI 程式碼

* [/server](./server/src/main/kotlin) - Ktor 伺服器應用程式

* [/shared](./shared/src) - 所有目標平台共享的程式碼
    - [commonMain](./shared/src/commonMain/kotlin) - 核心共用程式碼

## 本機開發

### Android 應用程式

使用 IDE 的執行設定，或從終端機建置：

```shell
# macOS/Linux
./gradlew :composeApp:assembleDebug

# Windows
.\gradlew.bat :composeApp:assembleDebug
```

### Server 應用程式

```shell
# macOS/Linux
./gradlew :server:run

# Windows
.\gradlew.bat :server:run
```

### iOS 應用程式

使用 IDE 的執行設定，或在 Xcode 中開啟 [/iosApp](./iosApp) 目錄執行。

---

## 📱 Android 自動化發佈（Google Play Console）

### 概述

- 使用 **Gradle Play Publisher (GPP)** 外掛自動上傳 AAB 到 Google Play Console
- 支援多軌道發布：`internal`（內測）、`alpha`（封閉測試）、`beta`（公測）、`production`（正式版）
- GitHub Actions 自動化：只在 main 分支和特定 tag 觸發，develop 分支不會觸發發布
- 自動遞增版本號，並在版本衝突時自動重試

### 分支策略

```
develop (開發分支)
  ↓ 日常 commit（不觸發 CI/CD）
  ↓ PR/merge
main (穩定分支)
  ↓ 自動發布到 internal 軌道
  ↓ 打 tag
android-beta-v* → 公開測試（beta 軌道）
android-v* → 正式發布（production 軌道）
```

**工作原則**：

1. 在 `develop` 分支日常開發（不會觸發發布）
2. 開 PR 從 `develop` 到 `main`（只建置驗證，不上傳）
3. Merge 到 `main` 後自動發布到 `internal` 軌道
4. 測試通過後打 tag 發布到 beta 或 production

### GitHub Actions 觸發策略

| 觸發方式                           | 目標軌道         | 說明             |
|--------------------------------|--------------|----------------|
| `develop` 分支 commit            | 不觸發          | 日常開發，不會建置或上傳   |
| PR: `develop` → `main`         | 不上傳          | 只建置驗證，確保可以正常打包 |
| Merge PR 或 push to `main`      | `internal`   | 自動發布到內部測試軌道    |
| `git tag android-alpha-v1.0.0` | `alpha`      | 封閉測試（特定測試人員）   |
| `git tag android-beta-v1.0.0`  | `beta`       | 公開測試（大規模驗證）    |
| `git tag android-v1.0.0`       | `production` | 正式發布           |
| 手動觸發（Actions UI）               | 自選           | 緊急修復或特殊發布      |

### 使用流程範例

#### 日常開發（在 develop 分支）

```bash
# 切換到 develop 分支
git checkout develop

# 開發功能並測試
git add .
git commit -m "feat: 新增某功能"
git push origin develop
# → 不會觸發任何 CI/CD，可以自由開發
```

#### 發布到內部測試（merge 到 main）

```bash
# 方式 1：使用 GitHub CLI 開 PR
gh pr create --base main --head develop --title "Release: v1.0.X"
# → PR 自動觸發建置驗證（只建置，不上傳）

# 方式 2：直接 merge
git checkout main
git merge develop
git push origin main
# → GitHub Actions 自動上傳到 internal 軌道
```

#### 發布到公開測試

```bash
# 確保已 merge 到 main 並在 internal 測試通過
git checkout main
git tag android-beta-v1.0.1
git push --tags
# → GitHub Actions 自動上傳到 beta 軌道
```

#### 正式發布

```bash
# 確保 beta 測試通過
git checkout main
git tag android-v1.0.1
git push --tags
# → GitHub Actions 自動上傳到 production 軌道
```

### 版本號管理

- **versionCode**：CI 自動遞增（使用 `github.run_number + 100`），本機預設為 2
- **versionName**：CI 自動產生（格式：`1.0.{run_number}`），本機預設為 `1.0`
- 本機測試時使用預設值，推送到 GitHub 後自動遞增，無需手動修改
- 若版本號已被使用，會自動偵測並遞增版本號重試

### 必要的 GitHub Secrets

前往 **Settings → Secrets and variables → Actions** 新增以下 Secrets：

| Secret 名稱                   | 說明                           | 如何取得                                           |
|-----------------------------|------------------------------|------------------------------------------------|
| `PLAY_CREDENTIALS_JSON_B64` | Service Account JSON（base64） | `base64 -i service-account.json \| tr -d '\n'` |
| `UPLOAD_KEYSTORE_BASE64`    | Upload keystore（base64）      | `base64 -i my_keystore.jks \| tr -d '\n'`      |
| `UPLOAD_KEYSTORE_PASSWORD`  | Keystore 密碼                  | 純文字                                            |
| `UPLOAD_KEY_ALIAS`          | Key alias                    | 純文字                                            |
| `UPLOAD_KEY_PASSWORD`       | Key 密碼                       | 純文字                                            |

#### 設定步驟

1. **取得 Service Account JSON**
    - 前往 Google Cloud Console 建立 Service Account
    - 下載 JSON 金鑰，轉換為 base64：
      ```bash
      base64 -i service-account.json | tr -d '\n' > creds.txt
      ```
    - 複製內容到 `PLAY_CREDENTIALS_JSON_B64`

2. **取得 Keystore**
    - 使用現有的 upload keystore 或建立新的
    - 轉換為 base64：
      ```bash
      base64 -i my_keystore.jks | tr -d '\n' > keystore.txt
      ```
    - 複製內容到 `UPLOAD_KEYSTORE_BASE64`

3. **設定密碼和 alias**
    - 將 keystore 密碼、key alias 和 key 密碼分別新增到對應的 Secrets

### 本機測試

如需在本機手動建置和上傳：

```bash
# 設定環境變數
export UPLOAD_KEYSTORE=/path/to/my_keystore.jks
export UPLOAD_KEYSTORE_PASSWORD='your_password'
export UPLOAD_KEY_ALIAS='your_alias'
export UPLOAD_KEY_PASSWORD='your_password'

# 建置 AAB
./gradlew :composeApp:bundleRelease

# 上傳到指定軌道
./gradlew :composeApp:publishReleaseBundle --track internal
./gradlew :composeApp:publishReleaseBundle --track beta
./gradlew :composeApp:publishReleaseBundle --track production
```

### 注意事項

- **首次上傳**：必須先在 Play Console 手動建立 App 並完成一次手動上傳
- **簽章一致性**：Upload keystore 必須與 Play Console 註冊的 Upload key SHA1 一致
- **Service Account 權限**：在 Play Console → Users and permissions 授予 **Release manager** 角色
- **版本號衝突**：系統會自動偵測並遞增版本號重試

### 參考資源

- [Gradle Play Publisher 官方文件](https://github.com/Triple-T/gradle-play-publisher)
- [Google Play Console 發布流程](https://support.google.com/googleplay/android-developer/answer/9859152)

---

## 🍎 iOS 上架與自動化（App Store Connect）

### 目前狀態

已完成 iOS 自動化框架，但因 App Store Connect API 驗證問題暫時停用。

#### 已完成的設定

- ✅ Fastlane 配置（`iosApp/fastlane/`）
- ✅ CocoaPods 整合
- ✅ GitHub Actions workflow（已暫時停用）
- ✅ 簽章與建置流程

#### 待解決問題

App Store Connect API 金鑰驗證失敗（HTTP 401），可能原因：

- API 存取權限尚未完全開通
- 帳號合約或條款待處理
- 角色權限設定需要 Apple 端調整

#### 暫時方案

在 API 問題解決前，可使用 Xcode Organizer 手動上傳：

1. 開啟 `iosApp/iosApp.xcworkspace`
2. 選擇 `Product → Archive`
3. 在 Organizer 中選擇 `Distribute App`
4. 選擇 `App Store Connect` 並完成上傳

#### 本機測試 Fastlane

```bash
cd iosApp
export ASC_KEY_ID=<你的 Key ID>
export ASC_ISSUER_ID=<你的 Issuer ID>
export ASC_PRIVATE_KEY_PATH=<你的 .p8 絕對路徑>
BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane beta
```

若需協助解決 API 驗證問題，請聯絡 Apple Developer Support。

---

## 📚 相關資源

- [Kotlin Multiplatform 官方文件](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/)
- [Kotlin/Wasm](https://kotl.in/wasm/)

問題回報請至 [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP)