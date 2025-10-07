# 黑貓新聞 - 首頁導航架構

## 概述

實現了完整的首頁導航結構，使用 **Type-Safe Navigation** 和 Jetpack Compose Multiplatform 最佳實踐。

## 架構特點

### 1. Type-Safe Navigation (類型安全導航)

使用 `@Serializable` 註解定義路由，避免字符串硬編碼：

```kotlin
@Serializable
object HomeRoute

@Serializable
object CategoriesRoute

@Serializable
object FavoritesRoute

@Serializable
object SettingsRoute
```

### 2. 主要功能

#### 首頁 (HomeScreen)

- ✅ 頂部 App Bar（應用標題、搜尋按鈕、通知按鈕）
- ✅ 分類 Chips 橫向滾動（最新、世界新聞、科技、商業、體育、娛樂、健康、科學）
- ✅ 新聞卡片列表（縮圖、標題、摘要、來源、發布時間）
- ✅ 分類過濾功能

#### 底部導航

- 🏠 首頁
- 📑 分類
- ❤️ 收藏
- ⚙️ 設定

### 3. 文件結構

```
composeApp/src/commonMain/kotlin/com/linli/blackcatnews/
├── navigation/
│   ├── NavRoutes.kt           # 路由定義
│   └── AppNavigation.kt       # 主導航結構
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt      # 首頁
│   │   ├── CategoriesScreen.kt # 分類頁
│   │   ├── FavoritesScreen.kt # 收藏頁
│   │   └── SettingsScreen.kt  # 設定頁
│   └── components/
│       ├── NewsCard.kt        # 新聞卡片
│       ├── CategoryChip.kt    # 分類標籤
│       └── BottomNavigation.kt # 底部導航欄
└── model/
    └── NewsItem.kt            # 新聞數據模型
```

### 4. 數據模型

#### NewsItem

```kotlin
data class NewsItem(
    val id: String,
    val title: String,
    val summary: String,
    val imageUrl: String?,
    val source: String,
    val publishTime: String,
    val category: NewsCategory
)
```

#### NewsCategory

```kotlin
enum class NewsCategory(val displayName: String) {
    LATEST("最新"),
    WORLD("世界新聞"),
    TECH("科技"),
    BUSINESS("商業"),
    SPORTS("體育"),
    ENTERTAINMENT("娛樂"),
    HEALTH("健康"),
    SCIENCE("科學")
}
```

### 5. UI 組件

#### NewsCard

新聞卡片展示：

- 新聞圖片（200dp 高度，橫向填滿）
- 標題（最多 2 行，超出顯示省略號）
- 摘要（最多 3 行，超出顯示省略號）
- 來源和發布時間（底部左右對齊）

#### CategoryChip

分類標籤：

- 使用 `FilterChip` 實現
- 支持選中狀態
- 橫向滾動展示

### 6. 依賴項

已添加以下依賴：

- `navigation-compose`: Jetpack Navigation for Compose Multiplatform
- `kotlinx-serialization-json`: Kotlin 序列化支持
- `materialIconsExtended`: Material Icons 擴展（使用 emoji 替代）

### 7. 使用方式

在 `App.kt` 中調用：

```kotlin
@Composable
fun App() {
    // Coil ImageLoader 初始化...
    
    AppTheme {
        AppNavigation()
    }
}
```

### 8. 待實現功能

- [ ] 搜尋功能
- [ ] 通知功能
- [ ] 新聞詳情頁面
- [ ] 實際 API 數據獲取
- [ ] 收藏功能實現
- [ ] 設定頁面詳細選項
- [ ] 下拉刷新
- [ ] 分頁加載

## 最佳實踐

1. **類型安全**：使用 `@Serializable` 標記路由
2. **清晰的結構**：按功能組織文件
3. **可重用組件**：將 UI 拆分為小型可組合函數
4. **狀態管理**：使用 `remember` 和 `mutableStateOf`
5. **Material Design 3**：遵循 Material You 設計規範
6. **響應式設計**：適配不同屏幕尺寸
7. **代碼註釋**：清晰的中文註釋說明

## 運行截圖

首頁包含：

- 頂部工具欄：「黑貓新聞」標題 + 搜尋 🔍 + 通知 🔔
- 分類標籤：橫向滾動的 8 個分類
- 新聞列表：帶圖片的卡片式展示
- 底部導航：4 個主要分頁

## 開發者備註

- 目前使用模擬數據（`getSampleNewsList()`）
- 圖片使用 `picsum.photos` 提供的隨機圖片
- 圖標暫時使用 emoji，可替換為 Material Icons
- 導航已實現 Type-Safe，確保編譯時類型檢查
