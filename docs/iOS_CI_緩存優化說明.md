# iOS CI/CD 緩存優化說明

> **提交**: `ca276f4` - 全面優化 iOS CI 緩存策略  
> **基於**: 專家建議 + Kotlin Multiplatform 最佳實踐  
> **預期效果**: 縮短構建時間 **9-15 分鐘**

---

## 📋 實施的優化

### 1. 鎖定 Konan 快取路徑 🔒

**問題**：子流程可能使用不同的 Konan 路徑，導致快取失效。

**解決方案**：

```yaml
env:
  KONAN_DATA_DIR: $HOME/.konan
```

**效果**：

- ✅ 確保所有 Gradle 任務使用同一路徑
- ✅ 避免重複下載 Kotlin/Native 工具鏈
- ✅ 提高快取命中率

---

### 2. 提升快取 Key 敏感度 🎯

**修改前**：

```yaml
key: ${{ runner.os }}-konan-${{ hashFiles('**/gradle/libs.versions.toml', '**/build.gradle.kts') }}
```

**修改後**：

```yaml
key: ${{ runner.os }}-konan-${{ hashFiles('**/gradle/libs.versions.toml', '**/build.gradle.kts', '**/gradle.properties') }}
```

**為什麼加入 `gradle.properties`？**

| 檔案 | 包含什麼 | 影響 Konan 的因素 |
|------|---------|------------------|
| `libs.versions.toml` | Kotlin 版本 | ✅ 決定 K/N 編譯器版本 |
| `build.gradle.kts` | 依賴配置 | ✅ 影響需要的原生庫 |
| `gradle.properties` | **編譯旗標** | ✅ **K/N 優化選項、目標架構** |

**範例場景**：

```properties
# gradle.properties 變更
kotlin.native.cacheKind=static  # 從 dynamic 改為 static
```

如果不納入 key，會命中**不相容的舊快取**，導致構建失敗！

**效果**：

- ✅ 避免編譯旗標變更時使用不相容快取
- ✅ 精確控制快取失效時機
- ✅ 減少構建錯誤

---

### 3. 優化快取順序 ⚡

**修改前（錯誤）**：

```yaml
- name: Install Bundler dependencies  # ❌ 先安裝
  run: bundle install

- name: Cache Ruby gems              # ❌ 後快取（無效）
  uses: actions/cache@v4
```

**修改後（正確）**：

```yaml
- name: Cache Ruby gems              # ✅ 先還原快取
  uses: actions/cache@v4

- name: Install Bundler dependencies  # ✅ 後補齊差異
  run: bundle install
```

**為什麼順序重要？**

```
錯誤順序：
1. bundle install（下載所有 gems，5-10 秒）
2. 保存快取（已經下載完了，沒意義）
3. 下次構建：重複步驟 1（快取無用）

正確順序：
1. 還原快取（如果存在，1-2 秒）
2. bundle install（只補差異，1-2 秒）
3. 保存新快取（供下次使用）
4. 下次構建：跳過大部分下載（節省 8-10 秒）
```

**效果**：

- ✅ Ruby gems 快取實際生效
- ✅ 節省 **8-10 秒** bundle install 時間
- ✅ 減少網路請求

---

### 4. 完整 Git 歷史 📚

**修改前**：

```yaml
- uses: actions/checkout@v4
```

**修改後**：

```yaml
- uses: actions/checkout@v4
  with:
    fetch-depth: 0
```

**為什麼需要完整歷史？**

| `fetch-depth` | 取得內容 | 適用場景 |
|--------------|---------|---------|
| 預設（1） | 只有當前 commit | ✅ 簡單構建 |
| 0 | **完整 Git 歷史** | ✅ 版本標籤、變更日誌、Git 統計 |

**未來可能需要的場景**：

- 生成變更日誌（`git log`）
- 計算距離上次 tag 的 commits（版本號）
- Git-based 版本控制工具
- 審計和追溯

**效果**：

- ✅ 一次取得，避免日後需要時重跑
- ✅ 成本低（多 1-2 秒）
- ✅ 靈活性高

---

## 📊 效能提升對比

### 快取命中情況

| 場景 | Konan 快取 | Gradle 快取 | Ruby Gems | 總時間 |
|------|-----------|-----------|-----------|--------|
| **首次構建**（無快取） | ❌ 下載 ~10 分鐘 | ❌ 下載 ~3 分鐘 | ❌ 安裝 ~10 秒 | **~16-20 分鐘** |
| **優化前**（部分快取） | ⚠️ 可能失效 | ✅ 命中 | ❌ 無效 | **~12-15 分鐘** |
| **優化後**（全部命中） | ✅ 命中 ~30 秒 | ✅ 命中 ~20 秒 | ✅ 命中 ~2 秒 | **~6-7 分鐘** ⭐ |

**節省時間**: **9-13 分鐘** 🚀

---

## 🔧 完整的優化配置

```yaml
jobs:
  ios:
    runs-on: macos-15
    
    env:
      # ... 其他環境變數 ...
      
      # ⭐ 1. 鎖定 Konan 路徑
      KONAN_DATA_DIR: $HOME/.konan
    
    steps:
      # ⭐ 4. 完整 Git 歷史
      - name: Checkout
        uses: actions/checkout@v4
        with:
          fetch-depth: 0
      
      # Gradle 快取（自動處理）
      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v4
        with:
          gradle-home-cache-cleanup: true
          cache-encryption-key: ${{ secrets.GRADLE_CACHE_ENCRYPTION_KEY }}
      
      # ⭐ 2. 提升 Konan key 敏感度
      - name: Cache Konan
        uses: actions/cache@v4
        with:
          path: ~/.konan
          key: ${{ runner.os }}-konan-${{ hashFiles('**/gradle/libs.versions.toml', '**/build.gradle.kts', '**/gradle.properties') }}
          restore-keys: |
            ${{ runner.os }}-konan-
      
      # ⭐ 3. 優化快取順序（先快取後安裝）
      - name: Cache Ruby gems
        uses: actions/cache@v4
        with:
          path: iosApp/fastlane/vendor/bundle
          key: ${{ runner.os }}-gems-${{ hashFiles('iosApp/fastlane/Gemfile.lock') }}
      
      - name: Install Bundler dependencies
        run: bundle install
```

---

## 📝 Gradle 配置檢查

確認 `gradle.properties` 已啟用快取：

```properties
# ✅ 已配置（無需修改）
org.gradle.caching=true
org.gradle.configuration-cache=true
```

**這些設定的效果**：

- `org.gradle.caching=true` - 啟用 Gradle 構建快取
- `org.gradle.configuration-cache=true` - 快取配置階段

**不會直接跳過 K/N 編譯**，但能減少：

- Gradle 任務配置時間
- 重複任務的執行成本

---

## 🎯 未來可考慮的進階優化

### 5. 只建必要架構（建議考慮）

**當前**：可能構建多個架構（arm64 + x64）

**優化**：

```kotlin
// build.gradle.kts
kotlin {
    listOf(
        iosArm64()        // ✅ 只建真機架構（發佈時）
        // iosSimulatorArm64()  // ❌ CI 不需要
    ).forEach { ... }
}
```

**效果**：

- 縮短 Kotlin Framework 編譯時間
- 減少 Xcode link/簽章時間
- 對「Compile Kotlin Framework」之後的階段最有感

**適用時機**：

- 正式發佈時（只需 `iosArm64`）
- 不適用於本地開發（需要模擬器）

---

### 6. 鎖定 Xcode 次版本（高穩定性需求）

**當前**：

```yaml
xcode-version: '16.*'  # 任何 16.x 版本
```

**高穩定性需求**：

```yaml
xcode-version: '16.2.*'  # 鎖定 16.2.x
```

**權衡**：

- ✅ 完全可重現的構建
- ❌ 需���手動更新版本號

---

## ✅ 驗證檢查清單

在 GitHub Actions 日誌中確認：

```
✅ Cache Konan: Cache hit - 快取命中
✅ Cache Ruby gems: Cache hit - 快取命中
✅ Setup Gradle: Build cache restored - Gradle 快取還原
✅ KONAN_DATA_DIR=/Users/runner/.konan - 環境變數生效

時間節省：
⏱️ Konan 下載: 10 分鐘 → 30 秒（節省 9.5 分鐘）
⏱️ Ruby gems: 10 秒 → 2 秒（節省 8 秒）
⏱️ Gradle 配置: 2 分鐘 → 30 秒（節省 1.5 分鐘）

📊 總計節省: ~11 分鐘
```

---

## 🎉 總結

| 優化項目 | 實施狀態 | 預期效果 |
|---------|---------|---------|
| 1. 鎖定 Konan 路徑 | ✅ 完成 | 提高命中率 |
| 2. 提升 Key 敏感度 | ✅ 完成 | 避免錯誤快取 |
| 3. 優化快取順序 | ✅ 完成 | Gems 快取生效 |
| 4. 完整 Git 歷史 | ✅ 完成 | 未來靈活性 |
| 5. 只建必要架構 | 📝 可選 | 進一步優化 |
| 6. 鎖定 Xcode 版本 | 📝 可選 | 高穩定性 |

**實施的優化已符合最佳實踐，會大幅縮短構建時間！** 🚀

---

## 📚 參考資源

- [Kotlin Multiplatform iOS CI/CD](https://www.marcogomiero.com/posts/2024/kmp-ci-ios/)
- [Gradle Setup Action](https://github.com/gradle/actions/blob/main/docs/setup-gradle.md)
- [GitHub Actions Cache](https://docs.github.com/en/actions/using-workflows/caching-dependencies-to-speed-up-workflows)
- [Kotlin/Native Caching](https://kotlinlang.org/docs/native-caching.html)
