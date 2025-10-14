## Fastlane 使用說明（iOS）

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

### 2) 認證方式（擇一）

- 推薦：App Store Connect API 金鑰（免 2FA，CI 友善）
  - 需要環境變數：
    ```bash
    export ASC_KEY_ID=...
    export ASC_ISSUER_ID=...
    export ASC_PRIVATE_KEY_PATH=/absolute/path/to/AuthKey_xxx.p8
    # 或用 ASC_PRIVATE_KEY（私鑰全文），搭配 ASC_PRIVATE_KEY_BASE64=true（若是 base64 編碼）
    ```
- 替代：Apple ID + App 專用密碼 + 會話（FASTLANE_SESSION）
  - 第一次互動產生會話：
    ```bash
    export FASTLANE_USER='你的 Apple ID'
    # 這一步會在終端顯示一段 FASTLANE_SESSION，複製下來
    BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane spaceauth -u "$FASTLANE_USER"
    ```
  - 上傳前設定：
    ```bash
    export FASTLANE_SESSION='<貼上剛才的整段 FASTLANE_SESSION>'
    export FASTLANE_APPLE_APPLICATION_SPECIFIC_PASSWORD='xxxx-xxxx-xxxx-xxxx'
    ```

### 3) 可用 Lanes

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

### 4) 簽章與專案設定

- 請在 Xcode 的 `iosApp.xcworkspace` 中，`iosApp` target → `Signing & Capabilities`：
  - Release 使用 Apple Distribution 憑證與 App Store Profile
  - `Automatically manage signing` 務必可正常產生 `App Store` 描述檔
- Fastlane `gym` 以 `Release` + `export_method: app-store` 打包

### 5) 常見問題

- 要求輸入 Apple ID 密碼：
  - 使用 API Key 路徑可避免
  - Apple ID 路徑需以 `spaceauth` 先產生 `FASTLANE_SESSION`
- 2FA 問題：
  - `spaceauth` 互動一次即可，之後用 `FASTLANE_SESSION`
- 上傳卡在 Processing：
  - 等幾分鐘；或至 App Store Connect 查看錯誤信件

### 6) 安全與版控

- `FASTLANE_SESSION`、金鑰與密碼請勿提交到 Git
- CI 環境請使用 Secrets 注入（GitHub Actions → Secrets and variables → Actions）

### 7) 參考

- fastlane 官方文件：`https://docs.fastlane.tools`
- App Store Connect API：`https://developer.apple.com/documentation/appstoreconnectapi`
