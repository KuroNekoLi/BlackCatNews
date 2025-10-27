#!/bin/bash
set -euo pipefail

# ============================================
# 🧩 Purpose:
#   Remove "Compile Kotlin Framework" build phase that conflicts with CocoaPods
#   When using CocoaPods, the framework build is handled by the podspec script phase
# ============================================

echo "🔧 Fixing Xcode build phases for CocoaPods compatibility..."

PBXPROJ="iosApp/iosApp.xcodeproj/project.pbxproj"
BACKUP="${PBXPROJ}.backup.buildphase.$(date +%Y%m%d_%H%M%S)"

# Create backup
echo "📦 Creating backup: $BACKUP"
cp "$PBXPROJ" "$BACKUP"

# Find the UUID of "Compile Kotlin Framework" build phase
BUILD_PHASE_UUID=$(grep -E 'name = "Compile Kotlin Framework"' "$PBXPROJ" | grep -oE '[A-Z0-9]{24}' | head -1 || echo "")

if [ -z "$BUILD_PHASE_UUID" ]; then
  echo "ℹ️  No 'Compile Kotlin Framework' build phase found. Already clean!"
  exit 0
fi

echo "🔍 Found build phase UUID: $BUILD_PHASE_UUID"

# Remove the reference from buildPhases array
echo "🧹 Removing build phase reference from target..."
perl -i -pe "s/^\s+$BUILD_PHASE_UUID \/\* Compile Kotlin Framework \*\/,?\n//" "$PBXPROJ"

# Remove the build phase definition (multi-line removal)
echo "🧹 Removing build phase definition..."
perl -i -0pe "s/\s+$BUILD_PHASE_UUID \/\* Compile Kotlin Framework \*\/ = \{[^}]+\};\n//s" "$PBXPROJ"

echo "✅ Build phase removed successfully!"
echo "📝 Backup saved to: $BACKUP"
echo ""
echo "ℹ️  CocoaPods will now handle the framework build via the podspec script phase."
echo "   This prevents conflicts with embedAndSignAppleFrameworkForXcode task."
