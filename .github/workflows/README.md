# BlackCatNews CI/CD 流程說明

此專案使用 GitHub Actions 進行雙平台（iOS 和 Android）的自動化建置和部署。

## 分支策略

- `main` - 正式版本分支（Production）
- `develop` - 開發版本分支（Beta）
- `feature/*` - 功能開發分支
- `bugfix/*` - 錯誤修復分支

## Android CI/CD 流程

### 觸發條件和部署目標

| 事件           | 分支/標籤                   | 部署目標                        |
|--------------|-------------------------|-----------------------------|
| Push         | `main`                  | Google Play - Production 軌道 |
| Push         | `develop`               | Google Play - Beta 軌道       |
| Push         | `feature/*`, `bugfix/*` | 僅建置，不部署                     |
| Pull Request | `main`, `develop`       | 僅建置驗證，不部署                   |
| Tag          | `android-alpha-v*`      | Google Play - Alpha 軌道      |
| Tag          | `android-beta-v*`       | Google Play - Beta 軌道       |
| Tag          | `android-v*`            | Google Play - Production 軌道 |
| 手動觸發         | -                       | 可選擇任意軌道                     |

### 版本號規則

- Version Code: `100 + GitHub Run Number`
- Version Name: `1.0.{Version Code}`
- 發生版本衝突時會自動遞增重試

## iOS CI/CD 流程

### 觸發條件和部署目標

| 事件           | 分支/標籤                   | 部署目標                    |
|--------------|-------------------------|-------------------------|
| Push         | `main`                  | App Store - Release（送審） |
| Push         | `develop`               | TestFlight - Beta       |
| Push         | `feature/*`, `bugfix/*` | 僅建置，不部署                 |
| Pull Request | `main`, `develop`       | 僅建置驗證，不部署               |
| Tag          | `ios-alpha-v*`          | TestFlight - Beta       |
| Tag          | `ios-beta-v*`           | TestFlight - Beta       |
| Tag          | `ios-v*`                | App Store - Release（送審） |
| 手動觸發         | -                       | 可選擇 beta 或 release      |

### 版本號規則

- Build Number: GitHub Run Number
- 自動更新 `CURRENT_PROJECT_VERSION`

## 開發流程建議

### 1. 功能開發

```bash
# 從 develop 分支創建功能分支
git checkout -b feature/new-feature develop

# 開發完成後推送
git push origin feature/new-feature

# CI 會自動建置驗證（不部署）
```

### 2. 提交測試版本

```bash
# 合併到 develop 分支
git checkout develop
git merge feature/new-feature
git push origin develop

# CI 會自動部署：
# - Android → Google Play Beta 軌道
# - iOS → TestFlight Beta
```

### 3. 發布正式版本

```bash
# 合併到 main 分支
git checkout main
git merge develop
git push origin main

# CI 會自動部署：
# - Android → Google Play Production 軌道
# - iOS → App Store（送審）
```

### 4. 緊急修復

```bash
# 從 main 創建 bugfix 分支
git checkout -b bugfix/critical-fix main

# 修復後可直接合併到 main
git checkout main
git merge bugfix/critical-fix
git push origin main
```

## 必要的 GitHub Secrets

### Android

- `PLAY_CREDENTIALS_JSON` - Google Play Console API 憑證
- `UPLOAD_KEYSTORE_BASE64` - 簽名用 Keystore (Base64 編碼)
- `UPLOAD_KEYSTORE_PASSWORD` - Keystore 密碼
- `UPLOAD_KEY_ALIAS` - Key 別名
- `UPLOAD_KEY_PASSWORD` - Key 密碼

### iOS

- `ASC_KEY_ID` - App Store Connect API Key ID
- `ASC_ISSUER_ID` - App Store Connect Issuer ID
- `ASC_PRIVATE_KEY` - App Store Connect Private Key
- `ASC_PRIVATE_KEY_BASE64` - App Store Connect Private Key (Base64，可選)
- `IOS_DIST_CERT_BASE64` - iOS Distribution 憑證 (Base64 編碼)
- `IOS_DIST_CERT_PASSWORD` - 憑證密碼

## 注意事項

1. **版本號管理**：兩個平台都使用 GitHub Run Number 作為建置號碼的基礎，確保每次建置都有唯一的版本號。

2. **分支保護**：建議為 `main` 和 `develop` 分支設置保護規則，要求 PR 和通過 CI 檢查。

3. **標籤發布**：除了分支觸發外，也可以使用標籤來觸發特定的發布流程。

4. **手動觸發**：兩個平台都支援手動觸發 workflow，可在需要時選擇部署目標。