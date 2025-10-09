# DatabaseFactory 官方最佳實踐配置完成

## 最新架構概覽

* **平台層**（androidMain / iosMain）只負責提供 `RoomDatabase.Builder`
* **共用層**（commonMain）透過 `buildDatabase` 套用 `BundledSQLiteDriver` 並建置 `NewsDatabase`
* **Koin 模組** 透過 DI 管理資料庫與 DAO，符合官方 Room KMP 指南

---

## 核心實作

### commonMain

```kotlin
// DatabaseFactory.kt
fun buildDatabase(builder: RoomDatabase.Builder<NewsDatabase>): NewsDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .build()

// DatabaseModule.kt
val databaseModule = module {
    single<NewsDatabase> {
        val builder: RoomDatabase.Builder<NewsDatabase> = get()
        buildDatabase(builder)
    }
    single { get<NewsDatabase>().articleDao() }
}
```

### androidMain

```kotlin
// DatabaseFactory.android.kt
fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<NewsDatabase> {
    val appContext = context.applicationContext
    val file = appContext.getDatabasePath(NewsDatabase.DATABASE_NAME)
    return Room.databaseBuilder(
        appContext,
        NewsDatabase::class.java,
        file.absolutePath
    )
}

// PlatformModule.kt
val androidPlatformModule = module {
    single<RoomDatabase.Builder<NewsDatabase>> {
        getDatabaseBuilder(androidContext())
    }
}
```

### iosMain

```kotlin
// DatabaseFactory.ios.kt
@OptIn(ExperimentalForeignApi::class)
private fun documentsDirectoryPath(): String =
    requireNotNull(
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null
        )?.path
    )

fun getDatabaseBuilder(): RoomDatabase.Builder<NewsDatabase> =
    Room.databaseBuilder<NewsDatabase>(
        name = documentsDirectoryPath() + "/" + NewsDatabase.DATABASE_NAME
    )

// PlatformModule.kt
val iosPlatformModule = module {
    single<RoomDatabase.Builder<NewsDatabase>> { getDatabaseBuilder() }
}

fun initKoin(vararg extraModules: Module) {
    stopKoin()
    startKoin { modules(listOf(databaseModule, iosPlatformModule) + extraModules) }
}
```

---

## 在 Android / iOS 啟動 Koin

### Android

```kotlin
class BlackCatApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BlackCatApp)
            modules(databaseModule, androidPlatformModule /*, other modules */)
        }
    }
}
```

### iOS

```kotlin
fun BootstrapKoin() = initKoin(/* 可傳入其他 module */)
```

---

## 額外驗證

* `@ConstructedBy(NewsDatabaseConstructor::class)` 已套用
* Android / iOS KSP 任務均成功
* `koin-android` 依賴已加入，支援 `androidContext()`

---

## 官方參考

1. [Room KMP 官方文檔](https://developer.android.com/kotlin/multiplatform/room)
2. [Room + Koin 指南 · Fruitties 範例](https://github.com/android/kotlin-multiplatform-samples)
3. [NSFileManager.URLForDirectory](https://developer.apple.com/documentation/foundation/nsfilemanager)

---
✅ 架構現已完全貼齊官方最佳實踐，並可安全整合至 Koin 依賴注入流程。
