import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Base64
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    // Google Play Publisher plugin（用於自動化上傳至 Play Console）
    alias(libs.plugins.gpp)
    // Google Services plugin（处理 google-services.json 文件和 Firebase 配置）
    alias(libs.plugins.googleServices)
    // Firebase Crashlytics plugin（生成 Crashlytics Build ID）
    alias(libs.plugins.firebaseCrashlytics)
}
// 暫時移除 Room plugin，等確認版本相容性

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // iOS targets
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)
            implementation(libs.ksoup)
            implementation(libs.coil.compose)
            implementation(libs.coil.network)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentnegotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            // GitLive Firebase Kotlin SDK - 只使用需要的功能
            implementation(libs.gitlive.firebase.app)        // 核心依赖，必须包含
            implementation(libs.gitlive.firebase.auth)       // 用户认证
            implementation(libs.gitlive.firebase.analytics)  // 分析统计
            implementation(libs.gitlive.firebase.crashlytics) // 崩溃报告
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    // Room KSP 編譯器 - 官方最佳實踐：每個 target 都要配置
    add("kspAndroid", libs.room.compiler)
}

android {
    namespace = "com.linli.blackcatnews"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    // 讀取 keystore.properties
    val keystorePropertiesFile = rootProject.file("composeApp/keystore.properties")
    val keystoreProperties = Properties()
    if (keystorePropertiesFile.exists()) {
        keystoreProperties.load(keystorePropertiesFile.inputStream())
    }

    // Release 簽名配置：優先使用環境變數（CI/CD），其次使用 keystore.properties（本地開發）
    val finalKeystorePath =
        System.getenv("UPLOAD_KEYSTORE") ?: keystoreProperties.getProperty("keystore.path")
    val finalKeystorePassword = System.getenv("UPLOAD_KEYSTORE_PASSWORD")
        ?: keystoreProperties.getProperty("keystore.password")
    val finalKeyAlias =
        System.getenv("UPLOAD_KEY_ALIAS") ?: keystoreProperties.getProperty("key.alias")
    val finalKeyPassword =
        System.getenv("UPLOAD_KEY_PASSWORD") ?: keystoreProperties.getProperty("key.password")

    signingConfigs {
        create("release") {
            val pathEmpty = finalKeystorePath.isNullOrBlank()
            val storePwdEmpty = finalKeystorePassword.isNullOrBlank()
            val aliasEmpty = finalKeyAlias.isNullOrBlank()
            val keyPwdEmpty = finalKeyPassword.isNullOrBlank()

            if (pathEmpty || storePwdEmpty || aliasEmpty || keyPwdEmpty) {
                throw GradleException(
                    "Release 簽名設定不完整，請補齊以下項目：\n" +
                            "- keystore.path${if (pathEmpty) " (缺失)" else ""}\n" +
                            "- keystore.password${if (storePwdEmpty) " (缺失)" else ""}\n" +
                            "- key.alias${if (aliasEmpty) " (缺失)" else ""}\n" +
                            "- key.password${if (keyPwdEmpty) " (缺失)" else ""}\n" +
                            "請檢查 `composeApp/keystore.properties` 或對應環境變數。"
                )
            }

            storeFile = file(finalKeystorePath!!)
            storePassword = finalKeystorePassword
            keyAlias = finalKeyAlias
            keyPassword = finalKeyPassword
        }
    }

    defaultConfig {
        applicationId = "com.linli.blackcatnews"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 2
        versionName = System.getenv("VERSION_NAME") ?: "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
        }
    }

}

// Gradle Play Publisher 設定改以 gradle.properties 參數提供
// 可在 CI 以 -Pplay.track=internal/production 等方式覆寫
play {
    // 優先從環境變數注入服務帳戶金鑰，以避免 CI 中的編碼問題
    // - PLAY_CREDENTIALS_JSON: 直接貼上原始 JSON 內容（推薦）
    // - PLAY_CREDENTIALS_JSON_B64: 將 JSON 先做 base64（備選）
    val credsFile = file("${project.rootDir}/play-credentials.json")
    val credsRaw = System.getenv("PLAY_CREDENTIALS_JSON")
    val credsB64 = System.getenv("PLAY_CREDENTIALS_JSON_B64")
    when {
        !credsRaw.isNullOrBlank() -> {
            // 直接寫入原始 JSON（避免 base64 解碼造成格式破壞）
            credsFile.writeText(credsRaw)
        }
        !credsB64.isNullOrBlank() -> {
            // 從 base64 來源解碼寫入
            val decoded = Base64.getDecoder().decode(credsB64)
            credsFile.writeBytes(decoded)
        }

        else -> {
            // 無環境變數時，沿用既有檔案（若不存在會導致後續任務報錯，屆時提示補上）
        }
    }
    // 若 ANDROID_PUBLISHER_CREDENTIALS 由外部設定，GPP 會優先使用該環境變數（且需為 JSON 內容而非檔案路徑）。
    // 建議不要同時設定，以避免來源衝突。
    serviceAccountCredentials.set(credsFile)
    defaultToAppBundles.set(true)
    // 允許透過 -Pplay.track 或環境變數覆寫軌道
    track.set(
        project.findProperty("play.track")?.toString() ?: System.getenv("PLAY_TRACK") ?: "internal"
    )
}
