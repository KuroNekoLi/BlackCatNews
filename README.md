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

### 概述

- 使用 **Fastlane** + **App Store Connect API Key**（優先）或 **Apple ID + App 專用密碼**（備用）
- 支援 TestFlight 與 App Store 發布：`beta`（TestFlight）、`release`（App Store 送審）
- GitHub Actions 自動化：只在 main 分支和特定 tag 觸發，develop 分支不會觸發發布

### 認證策略（雙路徑容錯）

**優先路徑：App Store Connect API Key**

- 需要 GitHub Secrets：`ASC_KEY_ID`、`ASC_ISSUER_ID`、`ASC_PRIVATE_KEY`
- 優點：無需 2FA、最穩定、CI 友善

**備用路徑：Apple ID + App 專用密碼**

- 需要 GitHub Secrets：`FASTLANE_USER`、`FASTLANE_APPLE_APPLICATION_SPECIFIC_PASSWORD`、
  `FASTLANE_SESSION`
- 當 API Key 失效或未設定時自動使用

### 分支策略

```
develop (開發分支)
  ↓ 日常 commit（不觸發 CI/CD）
  ↓ PR/merge
main (穩定分支)
  ↓ 自動發布到 TestFlight
  ↓ 打 tag
ios-alpha-v* → 封閉測試（TestFlight）
ios-beta-v* → 公開測試（TestFlight）
ios-v* → 正式發布（App Store 送審）
```

### 專案設定

1. **Fastlane 設定**（已配置於 `iosApp/fastlane/`）
    - 支援 App Store Connect API Key 與 Apple ID 雙路徑認證
    - 自動簽章（`-allowProvisioningUpdates`）
    - lanes：`build`、`beta`（TestFlight）、`release`（App Store）

2. **iOS 專案配置**
    - 使用 `iosApp.xcworkspace`（CocoaPods + KMP framework）
    - 自動簽章：Release 使用 Apple Distribution 憑證
    - Bundle ID：`com.linli.blackcatnews`

3. **版本管理**
    - 本機：可在 Xcode 更新 Version 和 Build
    - CI：已自動以 `GITHUB_RUN_NUMBER` 覆寫 `CURRENT_PROJECT_VERSION`（Build），確保每次上傳版本唯一

### GitHub Actions 觸發策略

| 觸發方式                                          | 目標軌道       | 使用場景                |
|-----------------------------------------------|------------|---------------------|
| `develop` 分支 commit                           | 不觸發        | 日常開發（不會建置/上傳）       |
| PR: `develop` → `main`                        | 不上傳        | 只建置驗證（確保可以正常打包）     |
| `git push origin main`                        | TestFlight | 合併後自動發布到 TestFlight |
| `git tag ios-alpha-v1.0.0 && git push --tags` | TestFlight | 封閉測試（特定測試人員）        |
| `git tag ios-beta-v1.0.0 && git push --tags`  | TestFlight | 公開測試（大規模驗證）         |
| `git tag ios-v1.0.0 && git push --tags`       | App Store  | 正式發布（自動送審）          |
| 手動觸發（Actions UI）                              | 自選         | 緊急修復、特殊發布           |

### 必要的 GitHub Secrets

**優先路徑：App Store Connect API Key**
在 **Settings → Secrets and variables → Actions** 新增：

| Secret Name       | 說明         | 取得方式                                                     |
|-------------------|------------|----------------------------------------------------------|
| `ASC_KEY_ID`      | API Key ID | App Store Connect → Integrations → App Store Connect API |
| `ASC_ISSUER_ID`   | Issuer ID  | 同上，團隊金鑰頁面的 Issuer ID                                     |
| `ASC_PRIVATE_KEY` | 私鑰內容       | 下載的 `.p8` 檔案內容（含 `-----BEGIN/END PRIVATE KEY-----`）      |

**備用路徑：Apple ID + App 專用密碼**（可選）

| Secret Name                                    | 說明        | 取得方式                         |
|------------------------------------------------|-----------|------------------------------|
| `FASTLANE_USER`                                | Apple ID  | 你的 Apple ID 帳號               |
| `FASTLANE_APPLE_APPLICATION_SPECIFIC_PASSWORD` | App 專用密碼  | appleid.apple.com → App 專用密碼 |
| `FASTLANE_SESSION`                             | 會話 Cookie | 本機執行 `fastlane spaceauth` 產生 |

### 簽章憑證（p12）提供給 CI

CI 會在打包前自動將你的 Apple Distribution 憑證匯入臨時 keychain。請新增以下 Secrets：
| Secret Name | 說明 |
|--------------------------|-------------------------------------|
| `IOS_DIST_CERT_BASE64`   | 你的 `.p12` 憑證以 Base64 編碼後的一行字串 |
| `IOS_DIST_CERT_PASSWORD` | 匯出 `.p12` 時設定的密碼 |

在本機把 `.p12` 轉成 Base64（並複製到剪貼簿）：

```bash
base64 -i "/path/to/dist_cert.p12" | tr -d '\n' | pbcopy
```

若需驗證 Base64 可還原：

```bash
echo '<貼上的Base64>' | base64 -d > restored.p12
```

### 本地測試

**使用 App Store Connect API Key**：
```bash
cd iosApp
export ASC_KEY_ID="你的 Key ID"
export ASC_ISSUER_ID="你的 Issuer ID"
export ASC_PRIVATE_KEY="$(cat /path/to/AuthKey_XXX.p8)"
BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane beta
```

**使用 Apple ID + App 專用密碼**：

```bash
cd iosApp
export FASTLANE_USER="你的 Apple ID"
export FASTLANE_APPLE_APPLICATION_SPECIFIC_PASSWORD="xxxx-xxxx-xxxx-xxxx"
export FASTLANE_SESSION="你的會話 Cookie"
BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane beta
```

### 工作流程範例

**日常開發**（在 develop 分支）：

```bash
# 1. 切換到 develop 分支
git checkout develop

# 2. 開發功能並測試
git add .
git commit -m "feat: 新增某功能"
git push origin develop
# → 不會觸發任何 CI/CD，可以自由開發
```

**準備發布到 TestFlight**（merge 到 main）：

```bash
# 1. 開 PR：develop → main
gh pr create --base main --head develop --title "Release: v1.0.X"

# 2. PR 自動觸發建置驗證（只建置，不上傳）
# → 確保代碼可以正常打包

# 3. Merge PR 後自動發布
git checkout main
git pull
# → GitHub Actions 自動上傳到 TestFlight
```

**準備公測**：

```bash
# 1. 確保已 merge 到 main 並在 TestFlight 測試通過
# 2. 打 beta tag 並推送
git checkout main
git tag ios-beta-v1.0.1
git push --tags
# → GitHub Actions 自動上傳到 TestFlight（公開測試）
```

**正式發布**：

```bash
# 1. 確保 beta 測試通過
# 2. 打正式版 tag 並推送
git checkout main
git tag ios-v1.0.1
git push --tags
# → GitHub Actions 自動上傳到 App Store Connect 並送審
# 
# 或手動觸發（備用方式）：
# 前往 GitHub → Actions → iOS Deploy
# → Run workflow → 選擇 'release' lane
```

### 注意事項

- **自動簽章**：使用 Xcode 的自動簽章，需確保 Apple Developer 帳號有效
- **版本管理**：發布前記得在 Xcode 更新 Version 和 Build 號碼
- **TestFlight 審查**：beta 版本可能需要 TestFlight 審查（通常幾小時內完成）
- **App Store 審查**：正式版本需要 App Store 審查（通常 1-3 天）

### 參考資源

- [Fastlane 官方文件](https://docs.fastlane.tools/)
- [App Store Connect API](https://developer.apple.com/documentation/appstoreconnectapi)
- [TestFlight 發布指南](https://developer.apple.com/testflight/)