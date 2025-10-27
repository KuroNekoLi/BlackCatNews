#!/bin/bash
set -euo pipefail

# ============================================
# 🧩 Purpose:
#   Remove Swift Package Manager dependencies from Xcode project
#   to prevent conflicts with CocoaPods
# ============================================

echo "🧹 Removing Swift Package Manager dependencies from Xcode project..."

PBXPROJ="iosApp/iosApp.xcodeproj/project.pbxproj"
BACKUP="${PBXPROJ}.backup.$(date +%Y%m%d_%H%M%S)"

# Create backup
echo "📦 Creating backup: $BACKUP"
cp "$PBXPROJ" "$BACKUP"

# Remove XCRemoteSwiftPackageReference sections
echo "🔧 Removing XCRemoteSwiftPackageReference entries..."
perl -i -0pe 's/\t\t\t\t[A-Z0-9]+ \/\* XCRemoteSwiftPackageReference "[^"]*" \*\/,?\n//g' "$PBXPROJ"

# Remove XCRemoteSwiftPackageReference section entirely
perl -i -0pe 's/\/\* Begin XCRemoteSwiftPackageReference section \*\/.*?\/\* End XCRemoteSwiftPackageReference section \*\/\n//sg' "$PBXPROJ"

# Remove XCSwiftPackageProductDependency section entirely
perl -i -0pe 's/\/\* Begin XCSwiftPackageProductDependency section \*\/.*?\/\* End XCSwiftPackageProductDependency section \*\/\n//sg' "$PBXPROJ"

# Remove package references from target dependencies
perl -i -pe 's/\t\t\t\t[A-Z0-9]+ \/\* [^*]+ \*\/,? \/\* XCSwiftPackageProductDependency \*\/\n//g' "$PBXPROJ"

# Clean up any Package.resolved files
echo "🧹 Removing Package.resolved files..."
find iosApp -name "Package.resolved" -delete 2>/dev/null || true

# Clean up SwiftPM cache directories
echo "🧹 Removing SwiftPM cache directories..."
rm -rf iosApp/**/swiftpm 2>/dev/null || true
rm -rf iosApp/.swiftpm 2>/dev/null || true

echo "✅ Swift Package Manager dependencies removed successfully!"
echo "📝 Backup saved to: $BACKUP"
echo ""
echo "⚠️  IMPORTANT: Open the project in Xcode and verify:"
echo "   1. Go to Project Settings → Package Dependencies"
echo "   2. Ensure it shows 'No Package Dependencies'"
echo "   3. Go to Target → Build Phases → Link Binary With Libraries"
echo "   4. Remove any SPM frameworks (if present)"
echo ""
echo "Then run: bash scripts/check_no_spm_and_install_pods.sh"
