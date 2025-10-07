# BlackCatNews App Store 上架完整指南

本指南將協助您將 BlackCatNews 應用程式上架到 Apple App Store。

## 📋 目錄

- [前置準備](#前置準備)
- [配置專案](#配置專案)
- [App Store Connect 設定](#app-store-connect-設定)
- [建置和上傳](#建置和上傳)
- [審核和上架](#審核和上架)
- [常見問題](#常見問題)
- [檢查清單](#檢查清單)

---

## 🎯 前置準備

### 1. Apple 開發者帳號

#### 註冊步驟

1. 前往 [Apple Developer Program](https://developer.apple.com/programs/)
2. 點擊「Enroll」開始註冊
3. 使用您的 Apple ID 登入（或建立新的 Apple ID）
4. 選擇註冊類型：
    - **個人開發者**：使用個人名義
    - **組織開發者**：使用公司名義（需要公司文件）
5. 完成付款（年費 $99 美金）
6. 等待 Apple 審核（通常 24-48 小時）

#### 確認註冊完成

- 收到 Apple 的確認郵件
- 可以登入 [App Store Connect](https://appstoreconnect.apple.com/)
- 可以在 Xcode 中看到您的開發者帳號

### 2. 取得 Team ID

#### 方法一：從 Xcode 取得

1. 打開 Xcode
2. 點擊 `Xcode` > `Preferences` (或 `Settings`)
3. 選擇 `Accounts` 分頁
4. 點擊您的 Apple ID
5. 選擇您的 Team，可以看到 Team ID（格式：`XXXXXXXXXX`）

#### 方法二：從 Apple Developer 網站取得

1. 登入 [Apple Developer](https://developer.apple.com/account/)
2. 點擊 `Membership`
3. 在頁面中找到 `Team ID`

### 3. 更新專案配置

編輯 `iosApp/Configuration/Config.xcconfig` 檔案：

```xcconfig
TEAM_ID=XXXXXXXXXX  # 填入您的 Team ID

PRODUCT_NAME=BlackCatNews
PRODUCT_BUNDLE_IDENTIFIER=com.linli.blackcatnews.BlackCatNews$(TEAM_ID)

CURRENT_PROJECT_VERSION=1
MARKETING_VERSION=1.0
```

---

## ⚙️ 配置專案

### 4. 在 Xcode 中開啟專案

```bash
cd /Users/linli/AndroidStudioProjects/BlackCatNews/iosApp
open iosApp.xcodeproj
```

### 5. 配置 Signing & Capabilities

1. 在 Xcode 左側選擇 `iosApp` 專案
2. 選擇 `iosApp` Target
3. 點擊 `Signing & Capabilities` 分頁
4. 配置以下項目：

#### 自動簽名（推薦）

- 勾選 ✅ `Automatically manage signing`
- 選擇您的 Team
- Xcode 會自動產生和管理憑證

#### 手動簽名（進階）

- 取消勾選 `Automatically manage signing`
- 在 Apple Developer 網站手動建立憑證和描述檔
- 在 Xcode 中選擇對應的憑證

#### Bundle Identifier

確認 Bundle Identifier 為：

```
com.linli.blackcatnews.BlackCatNews
```

#### Deployment Target

建議設定為：

- **iOS 15.0** 或更高版本
- 考慮目標用戶群體的 iOS 版本分布

### 6. 準備 App 圖示

#### 圖示規格

- **尺寸**：1024x1024 像素
- **格式**：PNG
- **色彩空間**：RGB
- **不可包含**：透明度、圓角（系統會自動處理）

#### 放置位置

將準備好的圖示放入：

```
iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/
```

#### 在 Xcode 中設定

1. 打開 `iosApp/iosApp/Assets.xcassets`
2. 選擇 `AppIcon`
3. 拖曳 1024x1024 的圖示到 `App Store iOS 1024pt` 格子中
4. Xcode 會自動產生其他尺寸

### 7. 配置啟動畫面（Launch Screen）

編輯 `iosApp/iosApp/Assets.xcassets` 中的啟動畫面資源，或建立自訂的 Launch Screen。

### 8. 重要：更新 Info.plist 安全性設定

**當前問題**：您的 `Info.plist` 設定允許所有 HTTP 連線，App Store 可能會拒絕。

#### 選項 1：使用 HTTPS（推薦）

確保所有 API 都使用 HTTPS，然後移除 `NSAllowsArbitraryLoads`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
    <dict>
        <key>CADisableMinimumFrameDurationOnPhone</key>
        <true/>
    </dict>
</plist>
```

#### 選項 2：指定允許的域名

如果必須使用 HTTP，請明確指定域名：

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSExceptionDomains</key>
    <dict>
        <key>your-api-domain.com</key>
        <dict>
            <key>NSExceptionAllowsInsecureHTTPLoads</key>
            <true/>
            <key>NSIncludesSubdomains</key>
            <true/>
        </dict>
    </dict>
</dict>
```

### 9. 新增隱私權描述（如需要）

如果您的 App 使用以下功能，需要在 `Info.plist` 中新增說明：

```xml
<!-- 如果使用相機 -->
<key>NSCameraUsageDescription</key>
<string>BlackCatNews 需要存取相機以拍攝照片</string>

<!-- 如果使用相簿 -->
<key>NSPhotoLibraryUsageDescription</key>
<string>BlackCatNews 需要存取相簿以選擇照片</string>

<!-- 如果使用定位 -->
<key>NSLocationWhenInUseUsageDescription</key>
<string>BlackCatNews 需要您的位置以提供在地新聞</string>
```

---

## 🏪 App Store Connect 設定

### 10. 建立新 App

1. 登入 [App Store Connect](https://appstoreconnect.apple.com/)
2. 點擊 `我的 App`（My Apps）
3. 點擊左上角的 `+` 按鈕
4. 選擇 `新增 App`（New App）

### 11. 填寫基本資訊

#### App 資訊

- **平台**：iOS
- **名稱**：BlackCatNews（或您想要的名稱，最多 30 字元）
- **主要語言**：繁體中文
- **Bundle ID**：選擇 `com.linli.blackcatnews.BlackCatNews`
- **SKU**：唯一識別碼，例如 `blackcatnews001`
- **使用者存取權限**：完整存取權

### 12. 準備 App Store 頁面內容

#### 必要文字內容

##### App 名稱

```
BlackCatNews
```

（最多 30 字元）

##### 副標題

```
每日精選新聞，雙語學習
```

（最多 30 字元）

##### 描述

```
BlackCatNews 是您的每日新聞閱讀夥伴，提供精選的新聞內容和獨特的雙語學習功能。

主要功能：
• 📰 精選新聞 - 每日更新，掌握最新資訊
• 🌍 雙語閱讀 - 中英文對照，提升語言能力
• 📝 互動測驗 - 透過問答加深對新聞的理解
• 🎨 簡潔介面 - 流暢的閱讀體驗
• 🔖 個人化 - 收藏您感興趣的文章

BlackCatNews 讓您在忙碌的生活中，輕鬆掌握世界脈動，同時提升語言能力。

立即下載，開始您的新聞閱讀之旅！
```

（最多 4000 字元）

##### 關鍵字

```
新聞,news,雙語,bilingual,英文學習,english,閱讀,reading,時事,測驗,quiz
```

（最多 100 字元，用逗號分隔）

##### 宣傳文字（Promotional Text）

```
全新版本上線！體驗更流暢的新聞閱讀和雙語學習功能。
```

（最多 170 字元，可隨時更新）

#### URL 資訊

##### 支援 URL（必要）

提供使用者支援的網頁，例如：

```
https://yourwebsite.com/support
```

##### 行銷 URL（選填）

您的產品行銷網頁：

```
https://yourwebsite.com/blackcatnews
```

##### 隱私權政策 URL（必要）

**重要**：這是必填項目，您需要建立一個隱私權政策頁面。

內容應包含：

- 收集哪些資料
- 如何使用資料
- 是否分享給第三方
- 使用者權利
- 聯絡方式

範例：

```
https://yourwebsite.com/privacy-policy
```

### 13. 準備螢幕截圖

#### 必要尺寸（至少需要一組）

**6.7" 顯示器**（iPhone 15 Pro Max, iPhone 14 Pro Max）

- 尺寸：1290 x 2796 像素（直向）或 2796 x 1290 像素（橫向）
- 格式：PNG 或 JPG
- 數量：1-10 張

#### 建議尺寸

**6.5" 顯示器**（iPhone 11 Pro Max, iPhone XS Max）

- 尺寸：1242 x 2688 像素（直向）

**5.5" 顯示器**（iPhone 8 Plus, iPhone 7 Plus）

- 尺寸：1242 x 2208 像素（直向）

#### 如何製作截圖

**方法 1：使用 iOS 模擬器**

```bash
# 啟動特定模擬器
xcrun simctl boot "iPhone 15 Pro Max"
open -a Simulator

# 在模擬器中執行 App
# 使用 Cmd + S 擷取螢幕截圖
# 截圖會儲存到桌面
```

**方法 2：使用真實裝置**

1. 在實機上執行 App
2. 按下 `音量上鍵 + 側邊按鈕` 擷取螢幕
3. 透過 AirDrop 或 iCloud 傳送到 Mac
4. 使用圖片編輯工具調整（如需要）

**方法 3：使用設計工具美化**

- 使用 Figma、Sketch 或 Photoshop
- 加入裝置外框
- 新增說明文字
- 突顯主要功能

#### 截圖內容建議

1. 首頁/主要功能
2. 新聞列表
3. 新聞詳細頁面
4. 雙語閱讀功能
5. 測驗功能
6. 其他特色功能

### 14. 設定分類和年齡分級

#### 選擇分類

- **主要分類**：新聞（News）
- **次要分類**（選填）：教育（Education）

#### 年齡分級

填寫年齡分級問卷，根據您的內容誠實回答：

- 暴力內容
- 性或裸露內容
- 褻瀆或粗俗幽默
- 等等...

對於新聞 App，通常會是 **12+** 或 **17+**，取決於新聞內容。

### 15. App 審核資訊

#### 聯絡資訊

提供 Apple 審核團隊可以聯繫的資訊：

- 姓名
- 電話號碼
- Email

#### 登入資訊（如適用）

如果您的 App 需要登入才能使用：

- 提供測試帳號和密碼
- 說明如何使用

#### 備註

說明任何特殊的測試步驟或注意事項。

---

## 🚀 建置和上傳

### 16. 確認版本號

確認 `iosApp/Configuration/Config.xcconfig` 中的版本設定：

```xcconfig
CURRENT_PROJECT_VERSION=1        # Build Number
MARKETING_VERSION=1.0            # Version Number
```

**版本號規則**：

- **Version Number (MARKETING_VERSION)**：使用者看到的版本，例如 1.0, 1.1, 2.0
- **Build Number (CURRENT_PROJECT_VERSION)**：每次上傳必須遞增，例如 1, 2, 3...

### 17. 建置 Archive

#### 步驟

1. 在 Xcode 中選擇：
    - **Product** > **Scheme** > **iosApp**
2. 在工具列選擇目標裝置：
    - 選擇 **Any iOS Device** 或實際的 iOS 裝置（不要選擇模擬器）
3. 執行建置：
    - **Product** > **Archive**
4. 等待建置完成（可能需要幾分鐘）

#### 可能遇到的問題

**問題 1：簽名錯誤**

```
Code signing error: ...
```

解決方法：

- 確認 Team ID 設定正確
- 檢查 Signing & Capabilities 設定
- 確認憑證有效

**問題 2：建置錯誤**

```
Build failed
```

解決方法：

- 檢查編譯錯誤訊息
- 確認所有依賴套件已正確安裝
- 嘗試清理建置：**Product** > **Clean Build Folder** (Shift + Cmd + K)

### 18. 上傳到 App Store Connect

#### 步驟

1. 建置完成後，Xcode 會自動開啟 **Organizer** 視窗
    - 如果沒有自動開啟：**Window** > **Organizer**
2. 在 **Archives** 分頁中，選擇您剛建立的 Archive
3. 點擊 **Distribute App** 按鈕
4. 選擇發布方式：**App Store Connect**
5. 選擇上傳方式：**Upload**
6. 配置選項：
    - ✅ Upload your app's symbols...（推薦，用於崩潰分析）
    - ✅ Manage version and build number（自動管理版本號）
7. 檢查憑證和描述檔
8. 點擊 **Upload**
9. 等待上傳完成（時間取決於網路速度）

#### 上傳成功

您會看到成功訊息，並收到 Apple 的確認郵件。

#### 處理中

在 App Store Connect 中，建置版本會顯示「處理中」，通常需要 10-30 分鐘完成處理。

### 19. 選擇建置版本

1. 回到 [App Store Connect](https://appstoreconnect.apple.com/)
2. 進入您的 App
3. 在左側選擇 **App Store**
4. 在 **iOS App** 區段，找到 **建置版本**
5. 等待處理完成後，點擊 **+ 版本或平台**
6. 選擇您剛上傳的建置版本
7. 點擊 **完成**

---

## 🔍 審核和上架

### 20. 最後檢查

在提交審核前，確認以下項目：

- ✅ 所有必填欄位已填寫
- ✅ 螢幕截圖已上傳（至少 1 組）
- ✅ App 圖示已設定
- ✅ 隱私權政策 URL 可存取
- ✅ 支援 URL 可存取
- ✅ 建置版本已選擇
- ✅ 分類和年齡分級已設定
- ✅ 定價和銷售範圍已設定

### 21. 設定定價和銷售範圍

#### 定價

1. 在 App Store Connect 中選擇 **定價與銷售範圍**
2. 選擇價格等級：
    - **免費**：0 元
    - **付費**：選擇價格等級（例如 $0.99, $2.99...）

#### 銷售範圍

選擇您要銷售的國家/地區：

- 可以選擇 **所有地區**
- 或手動選擇特定國家/地區

### 22. 提交審核

1. 回到 **App Store** 分頁
2. 確認所有資訊正確
3. 點擊右上角的 **提交以供審核**（Submit for Review）
4. 回答出口合規性問題：
    - 您的 App 是否使用加密？（通常選「是」，因為 HTTPS 算是加密）
    - 如果是，是否符合豁免條件？（HTTPS 通常符合豁免）
5. 確認提交

### 23. 審核狀態追蹤

#### 審核狀態說明

| 狀態 | 說明 |
|------|------|
| 等待審核（Waiting for Review） | 已提交，排隊等待審核 |
| 正在審核（In Review） | Apple 團隊正在審核 |
| 待開發者發布（Pending Developer Release） | 審核通過，等待您手動發布 |
| 可供銷售（Ready for Sale） | 已上架，使用者可以下載 |
| 被拒絕（Rejected） | 審核未通過，請查看 Resolution Center |

#### 審核時間

- **正常情況**：1-3 天
- **首次提交**：可能需要更長時間
- **假日期間**：可能延遲

#### 收到通知

Apple 會透過以下方式通知審核結果：

- Email 通知
- App Store Connect 中的通知
- Resolution Center 中的訊息

### 24. 處理審核被拒

如果審核被拒絕：

1. **閱讀拒絕原因**
    - 在 Resolution Center 中查看詳細說明
    - Apple 會明確指出問題點

2. **常見被拒原因**
    - 功能不完整或有 Bug
    - 違反 App Store 審核指南
    - 元數據（截圖、描述）不準確
    - 隱私權問題
    - 效能問題（崩潰、卡頓）

3. **修正問題**
    - 根據 Apple 的回饋修正 App
    - 更新建置版本（Build Number 必須遞增）
    - 重新上傳

4. **回覆 Apple**
    - 在 Resolution Center 中說明您的修正
    - 如有疑問，可以要求澄清
    - 如不同意，可以申訴

5. **重新提交**
    - 修正後重新提交審核

### 25. 發布 App

#### 自動發布

- 如果設定為自動發布，審核通過後立即上架

#### 手動發布

1. 審核通過後，狀態會顯示「待開發者發布」
2. 在 App Store Connect 中點擊 **發布此版本**
3. App 會在幾小時內在 App Store 上架

### 26. 確認上架成功

1. **在 App Store Connect 檢查**
    - 狀態顯示「可供銷售」

2. **在 App Store 搜尋**
    - 在 iPhone 上打開 App Store
    - 搜尋 "BlackCatNews"
    - 確認可以找到並下載

3. **分享您的 App**
    - 從 App Store Connect 取得 App Store URL
    - 格式：`https://apps.apple.com/app/idXXXXXXXXXX`

---

## ❓ 常見問題

### Q1: 我沒有 Mac，可以上架 iOS App 嗎？

**答**：很遺憾，上架 iOS App 必須使用 Xcode，而 Xcode 只能在 macOS 上執行。您需要：

- 購買 Mac 電腦
- 租用 Mac 雲端服務（例如 MacStadium）
- 使用線上建置服務（例如 Codemagic, Bitrise）

### Q2: 審核要多��？

**答**：

- 正常情況：1-3 天
- 首次提交或重大更新：可能需要更長時間
- 小更新：可能在 24 小時內完成

### Q3: 可以加速審核嗎？

**答**：Apple 提供「加急審核」（Expedited Review）功能，但僅限於：

- 修正嚴重 Bug
- 時效性事件（例如與特定日期相關的功能）
- 申請網址：https://developer.apple.com/contact/app-store/?topic=expedite

### Q4: 免費 App 也需要付費帳號嗎？

**答**：是的，無論 App 是否免費，都需要加入 Apple Developer Program（$99/年）。

### Q5: 可以更改 Bundle ID 嗎？

**答**：Bundle ID 一旦在 App Store Connect 建立 App 後就無法更改。如需更改，必須建立新的 App。

### Q6: 如何更新已上架的 App？

**答**：

1. 增加版本號（例如從 1.0 升到 1.1）
2. 增加 Build Number
3. 建置新的 Archive
4. 上傳到 App Store Connect
5. 在 App Store Connect 中建立新版本
6. 填寫「此版本的新內容」
7. 提交審核

### Q7: 被拒絕後需要重新付費嗎？

**答**：不需要。開發者帳號是年費制，審核和重新提交不需額外付費。

### Q8: 如何處理隱私權政策？

**答**：您需要建立一個網頁說明隱私權政策。可以：

- 自己架設網站
- 使用 GitHub Pages（免費）
- 使用隱私權政策產生器工具

### Q9: 我的 App 需要後端服務嗎？

**答**：根據您的專案結構，BlackCatNews 有一個 `server` 模組，建議：

- 確保後端服務穩定運行
- 使用 HTTPS
- 有適當的錯誤處理

### Q10: TestFlight 是什麼？

**答**：TestFlight 是 Apple 的 Beta 測試平台，可以讓測試者在正式上架前測試您的 App：

- 上傳 Archive 到 App Store Connect
- 在 TestFlight 區段新增測試者
- 測試者可透過 TestFlight App 安裝
- 最多可以有 10,000 名外部測試者

---

## ✅ 檢查清單

### 提交前檢查清單

#### 開發者帳號

- [ ] 已註冊 Apple Developer Program
- [ ] 已取得 Team ID
- [ ] 已更新 Config.xcconfig 中的 TEAM_ID

#### 專案配置

- [ ] Bundle Identifier 已設定
- [ ] App 圖示已準備（1024x1024）
- [ ] 啟動畫面已設定
- [ ] Info.plist 安全性設定已更新
- [ ] 隱私權描述已新增（如需要）
- [ ] Signing & Capabilities 已配置
- [ ] Deployment Target 已設定

#### App Store Connect

- [ ] 已建立 App
- [ ] App 名稱已填寫
- [ ] 副標題已填寫
- [ ] 描述已填寫
- [ ] 關鍵字已填寫
- [ ] 支援 URL 已填寫
- [ ] 隱私權政策 URL 已填寫且可存取
- [ ] 螢幕截圖已上傳（至少 1 組）
- [ ] 分類已選擇
- [ ] 年齡分級已完成
- [ ] 定價已設定
- [ ] 銷售範圍已選擇
- [ ] App 審核資訊已填寫

#### 建置和上傳

- [ ] 版本號已設定正確
- [ ] Build Number 已遞增
- [ ] Archive 建置成功
- [ ] 已上傳到 App Store Connect
- [ ] 建置版本處理完成
- [ ] 已選擇建置版本

#### 測試

- [ ] App 在實機上運行正常
- [ ] 所有主要功能已測試
- [ ] 無嚴重 Bug 或崩潰
- [ ] 網路連線正常
- [ ] 使用者介面正常顯示
- [ ] 效能可接受

#### 法律和合規

- [ ] 內容符合 App Store 審核指南
- [ ] 隱私權政策完整且正確
- [ ] 出口合規性已確認
- [ ] 版權和授權已確認

---

## 📚 相關資源

### Apple 官方文件

- [App Store Connect 使用指南](https://developer.apple.com/app-store-connect/)
- [App Store 審核指南](https://developer.apple.com/app-store/review/guidelines/)
- [iOS Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/ios)
- [Xcode 使用說明](https://developer.apple.com/documentation/xcode)

### 工具

- [App Store Connect](https://appstoreconnect.apple.com/)
- [Apple Developer Portal](https://developer.apple.com/account/)
- [TestFlight](https://developer.apple.com/testflight/)

### 社群資源

- [Apple Developer Forums](https://developer.apple.com/forums/)
- [Stack Overflow - iOS 標籤](https://stackoverflow.com/questions/tagged/ios)

---

## 🎉 完成！

恭喜您完成所有步驟！您的 BlackCatNews App 即將與全世界的使用者見面。

祝您上架順利！🚀

---

**最後更新日期**：2025年1月

**文件版本**：1.0

如有任何問題或需要協助，歡迎隨時提出。
