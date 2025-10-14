## Fastlane 使用說明（iOS，僅使用 App Store Connect API）

### 1) 安裝需求

- 安裝 Xcode 與 Command Line Tools：
  ```bash
  xcode-select --install
  ```
- 安裝 fastlane（建議使用 Bundler）：
  ```bash
  gem install bundler --no-document
  bundle install
  ```

### 2) 認證方式（僅此一種）

- App Store Connect API 金鑰（免 2FA，CI 友善）
  - 需要環境變數：
    ```bash
    export ASC_KEY_ID=...
    export ASC_ISSUER_ID=...
    # 二選一：
    export ASC_PRIVATE_KEY="$(cat /absolute/path/to/AuthKey_xxx.p8)"
    # 或
    export ASC_PRIVATE_KEY="<base64-encoded-contents>"
    export ASC_PRIVATE_KEY_BASE64=true
    ```

### 3) 必要簽章設定（p12 + Provisioning Profile）

- GitHub Actions Secrets 建議：
  - `IOS_DIST_CERT_BASE64`: Apple Distribution 憑證 `.p12` 的 base64 字串
  - `IOS_DIST_CERT_PASSWORD`: `.p12` 密碼
- 打包時會：
  - 建立臨時 keychain 並匯入 `.p12`
  - 以 API Key 透過 `sigh` 下載 App Store 描述檔

### 4) 出口合規（Export Compliance）

- 預設 `ITSAppUsesNonExemptEncryption=false` 並於上傳時提供 `submission_information`
- 若你的 App 使用需申報之加密，請在 CI 設定對應環境變數：
  ```bash
  EXPORT_COMPLIANCE_USES_ENCRYPTION=true
  EXPORT_COMPLIANCE_IS_EXEMPT=true
  EXPORT_COMPLIANCE_THIRD_PARTY=false
  EXPORT_COMPLIANCE_PROPRIETARY=false
  # 可選：
  # EXPORT_COMPLIANCE_AVAILABLE_ON_FRENCH_STORE=true
  ```

### 5) 可用 Lanes

- 打包 ipa（App Store 發布設定）：
  ```bash
  BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane ios build
  ```
- 上傳 TestFlight：
  ```bash
  BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane ios beta
  ```
- 上傳到 App Store Connect（不自動送審）：
  ```bash
  BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane ios release
  ```

### 6) 簽章與專案設定

- 請在 Xcode 的 `iosApp.xcworkspace` 中，`iosApp` target → `Signing & Capabilities`：
  - Release 使用 Apple Distribution 憑證與 App Store Profile
  - `Automatically manage signing` 務必可正常產生 `App Store` 描述檔
- Fastlane `gym` 以 `Release` + `export_method: app-store` 打包

### 7) 常見問題

- 要求輸入 Apple ID 密碼：
  - 使用 API Key 路徑可避免
  - Apple ID 路徑需以 `spaceauth` 先產生 `FASTLANE_SESSION`
- 2FA 問題：
  - `spaceauth` 互動一次即可，之後用 `FASTLANE_SESSION`
- 上傳卡在 Processing：
  - 等幾分鐘；或至 App Store Connect 查看錯誤信件

### 8) 安全與版控

- `FASTLANE_SESSION`、金鑰與密碼請勿提交到 Git
- CI 環境請使用 Secrets 注入（GitHub Actions → Secrets and variables → Actions）

### 9) 參考

- fastlane 官方文件：`https://docs.fastlane.tools`
- App Store Connect API：`https://developer.apple.com/documentation/appstoreconnectapi`
