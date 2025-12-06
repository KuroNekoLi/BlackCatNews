# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/google/home/user/Android/Sdk/tools/proguard/proguard-android.txt
# and each project's build.gradle.kts file.

# Compose
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Keep all ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Kotlin Coroutines
-dontwarn kotlinx.coroutines.**

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontwarn kotlinx.serialization.json.**

# Ktor
-dontwarn io.ktor.**

# Room
-keep class androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# Firebase
-dontwarn com.google.firebase.**
