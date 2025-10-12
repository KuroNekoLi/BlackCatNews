import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.github.triplet.gradle.play.PlayPublisherExtension

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinCocoapods)
    // Google Play Publisher plugin（用於自動化上傳至 Play Console）
    id("com.github.triplet.play") version "3.12.1"
}

// 暫時移除 Room plugin，等確認版本相容性

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    // iOS targets
    iosArm64()
    iosSimulatorArm64()
    
    // CocoaPods 配置 - 自动支持 Debug 和 Release
    cocoapods {
        summary = "Black Cat News - AI 雙語新聞學習"
        homepage = "https://github.com/linli/BlackCatNews"
        version = "1.0"
        ios.deploymentTarget = "15.0"
        
        framework {
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
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

android {
    namespace = "com.linli.blackcatnews"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    // Release 簽章設定（從環境變數讀取）
    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("UPLOAD_KEYSTORE")
            val keystorePassword = System.getenv("UPLOAD_KEYSTORE_PASSWORD")
            val keyAlias = System.getenv("UPLOAD_KEY_ALIAS")
            val keyPassword = System.getenv("UPLOAD_KEY_PASSWORD")

            if (keystorePath.isNullOrBlank() ||
                keystorePassword.isNullOrBlank() ||
                keyAlias.isNullOrBlank() ||
                keyPassword.isNullOrBlank()
            ) {
                throw GradleException("請先設定 UPLOAD_KEYSTORE、UPLOAD_KEYSTORE_PASSWORD、UPLOAD_KEY_ALIAS、UPLOAD_KEY_PASSWORD 環境變數")
            }

            storeFile = file(keystorePath)
            storePassword = keystorePassword
            this.keyAlias = keyAlias
            this.keyPassword = keyPassword
        }
    }

    defaultConfig {
        applicationId = "com.linli.blackcatnews"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 2
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            // 建議真機測試時先關混淆，正式釋出再開
            isMinifyEnabled = false
            // 使用 release 簽章
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Gradle Play Publisher 設定改以 gradle.properties 參數提供
// 可在 CI 以 -Pplay.track=internal/production 等方式覆寫
play {
    // 預設讀取專案根目錄的憑證檔
    serviceAccountCredentials.set(file("${project.rootDir}/play-credentials.json"))
    defaultToAppBundles.set(true)
    // 允許透過 -Pplay.track 或環境變數覆寫軌道
    track.set(
        project.findProperty("play.track")?.toString() ?: System.getenv("PLAY_TRACK") ?: "internal"
    )
}
