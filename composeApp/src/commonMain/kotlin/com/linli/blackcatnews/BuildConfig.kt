package com.linli.blackcatnews

/**
 * Platform-specific build configuration
 * Uses expect/actual pattern to provide platform-specific implementations
 */

/**
 * Indicates whether the app is running in debug mode
 *
 * Android: Uses BuildConfig.DEBUG
 * iOS: Uses Platform.isDebugBinary
 */
expect val isDebug: Boolean