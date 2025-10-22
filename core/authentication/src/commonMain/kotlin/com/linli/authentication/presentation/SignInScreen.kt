package com.linli.authentication.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * 登入畫面
 * 使用 UDF/MVI 模式，支援單向資料流
 */
@Composable
fun SignInScreen(
    vm: SignInViewModel,
    onAppleClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onNavigateHome: () -> Unit = {}
) {
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 處理一次性副作用（Effect）
    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is SignInEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                SignInEffect.NavigateHome -> {
                    onNavigateHome()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "登入你的帳號",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                // 平台差異的社群登入按鈕
                SocialMediaButtonListPlatformSpecificUI(
                    onAppleClicked = onAppleClick,
                    onGoogleClicked = onGoogleClick,
                    onFacebookClicked = onFacebookClick
                )
            }

            // Loading 指示器
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )
            }
        }
    }
}
