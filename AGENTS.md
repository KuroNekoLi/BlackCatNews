# Repository Guidelines (AGENTS.md)

本文件定義本專案的開發規範與協作原則，Codex 會參考此文件進行自動化協助；  
團隊成員亦應以此作為提交程式碼、開發與審查時的基準。

---

# 🧱 專案結構（Project Structure）

composeApp/ ← Compose Multiplatform UI（Android/iOS）
├─ androidMain/
├─ iosMain/
├─ commonMain/
├─ commonTest/
├─ debug/
└─ release/

shared/ ← KMP domain + data（business logic, networking, db）

core/ ← Clean Architecture 分層（presentation → domain → data）

feature/ ← 各功能模組（如 dictionary、news、settings）

iosApp/ ← iOS SwiftUI Runner（使用 SPM）

server/ ← 內部工具、後端 script

docs/ ← 架構說明、開發指南、平台注意事項

---

# 🏗 Build, Test & Development Commands

## Android

```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug

Release：

./gradlew :composeApp:assembleRelease
./gradlew reinstallRelease
./gradlew installAndRunRelease

iOS（建議從 Xcode 執行）

open iosApp/iosApp.xcodeproj
# 選取 scheme + 裝置後 Cmd+R

Lint & Static Check

./gradlew :composeApp:lint
./gradlew :composeApp:check

Unit Tests（KMP）

./gradlew :shared:allTests

PR 必須至少成功執行：:composeApp:lint + :shared:allTests

⸻

📚 Coding Style & Naming Conventions

Kotlin 基本規範
	•	遵循 Kotlin 官方 Code Style
	•	4 spaces、multiline 使用 trailing commas
	•	儘量使用 val、不可變物件
	•	明確標示 public/internal/private
	•	Package 一律小寫：com.xxx.yyy

⸻

📘 繁體中文 KDoc 強制規範

✔ 所有「公開」成員都必須撰寫 KDoc（繁體中文）

適用於：
	•	public class / interface
	•	public function
	•	public property
	•	public sealed hierarchy

內容至少需包含：
	•	功能說明
	•	輸入 / 輸出 / 副作用
	•	若屬 Domain 物件需補充語意

✘ private / internal 非強制，但複雜邏輯仍建議補充文件或註解

⸻

🧱 架構原則（Architecture Principles）

本專案遵循：

✔ Clean Code
	•	小函式
	•	意圖呈現的命名
	•	早期回傳
	•	避免重複（DRY）

✔ SOLID
	•	單一職責（SRP）
	•	開放封閉
	•	介面分離
	•	依賴反轉（DIP）

✔ Clean Architecture

UI (presentation)
   ↓
ViewModel
   ↓
UseCases (domain)
   ↓
Repositories (data)
   ↓
Data Sources (network/db/platform)

UI 層不可直接呼叫資料來源，必須經由 domain。

⸻

🎨 Compose / UI 規範（Material3）

✔ 一律使用 Material 3
	•	使用 MaterialTheme.colorScheme
	•	使用 M3 Typography 與 Shape
	•	不引入 Material2

🖼 Composable Preview 規範

每個 composable 都需要對應的 Preview

情境：Composable 需要 ViewModel

請使用 Content 分離策略：

對外 API：

@Composable
fun NewsScreen(viewModel: NewsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    NewsScreenContent(
        uiState = uiState,
        onRefresh = viewModel::refresh,
    )
}

可 Preview 的部分：

@Composable
fun NewsScreenContent(
    uiState: NewsUiState,
    onRefresh: () -> Unit
) { ... }

@Preview
@Composable
private fun NewsScreenContentPreview() {
    NewsScreenContent(
        uiState = sampleNewsState(),
        onRefresh = {}
    )
}

Preview Data

使用 fake 或 sample data，不依賴真實 repository。

⸻

🧪 測試規範（Testing Guidelines）

原則：有邏輯就要有測試

需測試的區塊：
	•	UseCases
	•	Repositories
	•	Domain 轉換
	•	複雜 ViewModel（state 轉換）

測試命名

functionName_shouldDoX_whenY

或繁體中文描述行為皆可。

避免使用真實外部服務
	•	Firebase → 使用 fake/stub
	•	API → mock server or fake
	•	DB → in-memory

Regression Test

修 bug 時必須補「重現情境測試」。

⸻

📝 Commit & Pull Request Guidelines

Commit（繁體中文）

使用語法：

<type>: <摘要>

範例：

feat: 新增熱門新聞列表
fix: 修正新聞列表旋轉後閃退
chore: 更新 Gradle 與套件版本
docs: 補充資料模型 KDoc
refactor: 優化首頁 UI 邏輯

Pull Request
	•	以繁體中文撰寫說明
	•	包含：
	•	變更原因與摘要
	•	關聯 Issue
	•	測試方式
	•	已執行的 Gradle 指令
	•	UI 變更需附截圖或錄影（Android/iOS）
	•	若變更 Public API：
	•	必須補上/更新 KDoc
	•	必須新增/更新測試

⸻

🔐 Security & Configuration
	•	Firebase 設定檔位置（請勿更改）：
	•	Android:
	•	composeApp/src/debug/google-services.json
	•	composeApp/src/release/google-services.json
	•	iOS:
	•	iosApp/iosApp/GoogleService-Info-Debug.plist
	•	iosApp/iosApp/GoogleService-Info-Release.plist
	•	不可提交：
	•	local.properties
	•	keystore / p8 / p12 / provisioning profile
	•	任意憑證、密鑰、token