package com.linli.blackcatnews

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

/**
 * iOS-specific implementation using Platform.isDebugBinary
 * This determines if the binary was compiled in debug mode
 */
@OptIn(ExperimentalNativeApi::class)
actual val isDebug: Boolean = Platform.isDebugBinary