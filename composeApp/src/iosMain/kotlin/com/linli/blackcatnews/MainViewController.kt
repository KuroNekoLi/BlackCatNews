package com.linli.blackcatnews

import androidx.compose.ui.window.ComposeUIViewController
import com.linli.blackcatnews.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoin()
    return ComposeUIViewController { App() }
}