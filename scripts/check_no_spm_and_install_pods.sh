#!/bin/bash
set -euo pipefail

# ============================================
# 🧩 Purpose:
#   Enforce CocoaPods-only build for Kotlin Multiplatform (shared) module.
#   Prevent Swift Package Manager (SPM) conflicts (e.g. nanopb duplicate output).
# ============================================

echo "🔍 Checking project for unwanted Swift Package Manager references..."

PBXPROJ="iosApp/iosApp.xcodeproj/project.pbxproj"

if grep -q "XCRemoteSwiftPackageReference" "$PBXPROJ"; then
  echo "❌ Detected SwiftPM package references in $PBXPROJ"
  echo "   You must remove all 'Package Dependencies' from Xcode project before pushing."
  echo "   Otherwise, Xcode will mix SPM + CocoaPods and cause duplicate builds (e.g. nanopb)."
  exit 1
fi

echo "✅ No SwiftPM references found."

# 保險：清理任何 SwiftPM 殘留快取（這些不影響專案檔案結構）
rm -rf iosApp/**/swiftpm || true
rm -f  iosApp/**/Package.resolved || true

echo "🧹 Cleaned residual SwiftPM cache."

# ============================================
# 🔧 Build CocoaPods-only configuration
# ============================================

echo "🚀 Generating Kotlin dummy frameworks for CocoaPods..."
./gradlew :shared:generateDummyFramework --no-daemon --stacktrace
./gradlew :composeApp:generateDummyFramework --no-daemon --stacktrace

echo "📦 Installing CocoaPods dependencies..."
cd iosApp
pod repo update
pod deintegrate || true
pod install

echo "✅ CocoaPods setup complete. Proceeding with Xcode build."
