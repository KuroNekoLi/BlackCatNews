# 架構說明：統一 Scaffold vs 分散 Scaffold

## 問題
最初的設計中，`AppNavigation` 和各個 Screen（如 `HomeScreen`）都有自己的 `Scaffold`，這導致：
1. **重複的結構**：每個 Screen 都需要定義 `TopAppBar` 和 `Scaffold`
2. **不一致的體驗**：頂部欄和底部導航可能會有閃爍或重新渲染
3. **維護困難**：要修改全局 UI（如頂部欄樣式）需要修改多個文件

## 解決方案：統一 Scaffold 架構 ✅

### 架構圖
```
AppNavigation (統一 Scaffold)
├── TopBar (動態顯示，根據路由變化)
│   ├── 標題（隨路由變化）
│   └── Actions（僅首頁顯示搜尋和通知按鈕）
├── BottomNavigation (固定顯示在主要頁面)
└── NavHost (內容區域)
    ├── HomeScreen (純內容)
    ├── CategoriesScreen (純內容)
    ├── FavoritesScreen (純內容)
    └── SettingsScreen (純內容)
```

### 實現方式

#### 1. AppNavigation.kt - 統一管理 Scaffold
```kotlin
@Composable
fun AppNavigation() {
    Scaffold(
        topBar = {
            // 根據當前路由動態顯示不同的頂部欄
            AppTopBar(
                title = getTopBarTitle(currentRoute),
                showActions = isHomeRoute(currentRoute)
            )
        },
        bottomBar = {
            // 只在主要頁面顯示
            if (shouldShowBottomBar(currentRoute)) {
                AppBottomNavigation(...)
            }
        }
    ) { innerPadding ->
        NavHost(...) { /* 路由定義 */ }
    }
}
```

#### 2. 各個 Screen - 只負責內容
```kotlin
@Composable
fun HomeScreen(onNewsItemClick: (NewsItem) -> Unit) {
    Column {  // 不再使用 Scaffold
        CategoryChipsRow(...)
        NewsListColumn(...)
    }
}
```

## 優點

### ✅ 1. 單一職責原則
- **AppNavigation**：負責全局 UI 框架（頂部欄、底部導航）
- **各個 Screen**：只負責具體內容展示

### ✅ 2. 一致的用戶體驗
- 頂部欄和底部導航不會重新渲染
- 切換頁面時只有內容區域變化
- 統一的動畫和轉場效果

### ✅ 3. 易於維護
```kotlin
// 修改全局頂部欄樣式？只需要修改一個地方！
@Composable
private fun AppTopBar(...) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            // 統一修改所有頁面的頂部欄顏色
        )
    )
}
```

### ✅ 4. 動態控制
```kotlin
// 根據路由顯示不同標題
private fun getTopBarTitle(route: String?) = when (route) {
    "HomeRoute" -> "黑貓新聞"
    "CategoriesRoute" -> "分類"
    // ...
}

// 只在首頁顯示搜尋和通知按鈕
showActions = isHomeRoute(currentRoute)
```

### ✅ 5. 符合 Material Design 3 規範
- 統一的容器層級
- 正確的 elevation 處理
- 符合 Scaffold 的設計意圖

## 何時使用分散 Scaffold？

在以下情況下，可以考慮讓每個 Screen 有自己的 Scaffold：

### 場景 1：完全不同的 UI 結構
```kotlin
// 登入頁面 - 全屏，無導航欄
@Composable
fun LoginScreen() {
    Scaffold { /* 完全自定義的佈局 */ }
}

// 詳情頁面 - 有返回按鈕，無底部導航
@Composable
fun DetailScreen() {
    Scaffold(
        topBar = { TopAppBar(navigationIcon = { BackButton() }) }
    ) { /* 內容 */ }
}
```

### 場景 2：複雜的狀態管理
```kotlin
// 需要自己的 Floating Action Button
@Composable
fun EditScreen() {
    Scaffold(
        floatingActionButton = { SaveFAB() }
    ) { /* 內容 */ }
}
```

## 當前項目的最佳實踐

### ✅ 主要頁面（首頁、分類、收藏、設定）
- 使用統一 Scaffold
- 由 `AppNavigation` 管理
- 內容簡潔，專注業務邏輯

### ⚡ 特殊頁面（未來可能添加）
```kotlin
// 新聞詳情頁 - 需要自己的 Scaffold
@Composable
fun ArticleDetailScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton() },
                actions = { ShareButton() }
            )
        }
    ) { 
        ArticleContent() 
    }
}
```

## 代碼對比

### ❌ 之前（重複的 Scaffold）
```kotlin
// HomeScreen.kt
@Composable
fun HomeScreen() {
    Scaffold(
        topBar = { TopAppBar(title = "黑貓新聞") }
    ) { /* 內容 */ }
}

// CategoriesScreen.kt
@Composable
fun CategoriesScreen() {
    Scaffold(
        topBar = { TopAppBar(title = "分類") }
    ) { /* 內容 */ }
}
```

### ✅ 現在（統一管理）
```kotlin
// AppNavigation.kt
@Composable
fun AppNavigation() {
    Scaffold(
        topBar = { AppTopBar(title = getTopBarTitle(route)) }
    ) {
        NavHost { 
            composable<HomeRoute> { HomeScreen() }
            composable<CategoriesRoute> { CategoriesScreen() }
        }
    }
}

// HomeScreen.kt
@Composable
fun HomeScreen() {
    Column { /* 純內容 */ }
}
```

## 總結

| 特性 | 統一 Scaffold | 分散 Scaffold |
|------|--------------|--------------|
| 適用場景 | 相似結構的頁面 | 完全不同的頁面 |
| 維護成本 | ✅ 低 | ❌ 高 |
| 用戶體驗 | ✅ 流暢一致 | ⚠️ 可能有閃爍 |
| 代碼複用 | ✅ 高 | ❌ 低 |
| 靈活性 | ⚠️ 中等 | ✅ 高 |

**當前項目使用統一 Scaffold 架構**，因為：
1. 4 個主要頁面結構相似
2. 需要統一的頂部欄和底部導航
3. 符合 Material Design 3 規範
4. 易於維護和擴展

## 參考資料

- [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [Material Design 3 - Navigation](https://m3.material.io/components/navigation-bar/overview)
- [Scaffold Layout](https://developer.android.com/jetpack/compose/layouts/material#scaffold)
