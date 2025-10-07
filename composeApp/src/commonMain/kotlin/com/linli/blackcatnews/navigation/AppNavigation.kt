package com.linli.blackcatnews.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.linli.blackcatnews.ui.components.AppBottomNavigation
import com.linli.blackcatnews.ui.screens.CategoriesScreen
import com.linli.blackcatnews.ui.screens.FavoritesScreen
import com.linli.blackcatnews.ui.screens.HomeScreen
import com.linli.blackcatnews.ui.screens.SettingsScreen

/**
 * 主導航結構
 * 使用統一的 Scaffold 管理頂部欄和底部導航
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            // 根據當前路由顯示不同的頂部欄
            when {
                shouldShowTopBar(currentRoute) -> {
                    AppTopBar(
                        title = getTopBarTitle(currentRoute),
                        onSearchClick = {
                            // TODO: 導航到搜尋頁面
                        },
                        onNotificationClick = {
                            // TODO: 顯示通知
                        },
                        showActions = isHomeRoute(currentRoute)
                    )
                }
            }
        },
        bottomBar = {
            // 只在主要頁面顯示底部導航
            if (shouldShowBottomBar(currentRoute)) {
                AppBottomNavigation(
                    currentRoute = getCurrentRouteObject(currentRoute),
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // 避免重複導航到同一個目的地
                            launchSingleTop = true
                            // 回到起始目的地時清除 back stack
                            if (route == HomeRoute) {
                                popUpTo(HomeRoute) { inclusive = false }
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 首頁 - 不再需要內部的 Scaffold
            composable<HomeRoute> {
                HomeScreen(
                    onNewsItemClick = { newsItem ->
                        // TODO: 導航到新聞詳情頁
                    }
                )
            }

            // 分類頁面
            composable<CategoriesRoute> {
                CategoriesScreen()
            }

            // 收藏頁面
            composable<FavoritesRoute> {
                FavoritesScreen()
            }

            // 設定頁面
            composable<SettingsRoute> {
                SettingsScreen()
            }
        }
    }
}

/**
 * 統一的頂部應用欄
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(
    title: String,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    showActions: Boolean
) {
    TopAppBar(
        title = { Text(title) },
        actions = {
            if (showActions) {
                IconButton(onClick = onSearchClick) {
                    Text("🔍", style = MaterialTheme.typography.titleLarge)
                }
                IconButton(onClick = onNotificationClick) {
                    Text("🔔", style = MaterialTheme.typography.titleLarge)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

/**
 * 判斷是否應該顯示頂部欄
 */
private fun shouldShowTopBar(route: String?): Boolean {
    return route in listOf(
        "com.linli.blackcatnews.navigation.HomeRoute",
        "com.linli.blackcatnews.navigation.CategoriesRoute",
        "com.linli.blackcatnews.navigation.FavoritesRoute",
        "com.linli.blackcatnews.navigation.SettingsRoute"
    )
}

/**
 * 判斷是否應該顯示底部導航欄
 */
private fun shouldShowBottomBar(route: String?): Boolean {
    return route in listOf(
        "com.linli.blackcatnews.navigation.HomeRoute",
        "com.linli.blackcatnews.navigation.CategoriesRoute",
        "com.linli.blackcatnews.navigation.FavoritesRoute",
        "com.linli.blackcatnews.navigation.SettingsRoute"
    )
}

/**
 * 判斷是否為首頁路由
 */
private fun isHomeRoute(route: String?): Boolean {
    return route == "com.linli.blackcatnews.navigation.HomeRoute"
}

/**
 * 根據路由獲取頂部欄標題
 */
private fun getTopBarTitle(route: String?): String {
    return when (route) {
        "com.linli.blackcatnews.navigation.HomeRoute" -> "黑貓新聞"
        "com.linli.blackcatnews.navigation.CategoriesRoute" -> "分類"
        "com.linli.blackcatnews.navigation.FavoritesRoute" -> "收藏"
        "com.linli.blackcatnews.navigation.SettingsRoute" -> "設定"
        else -> "黑貓新聞"
    }
}

/**
 * 從路由字符串獲取路由對象
 */
private fun getCurrentRouteObject(route: String?): Any? {
    return when (route) {
        "com.linli.blackcatnews.navigation.HomeRoute" -> HomeRoute
        "com.linli.blackcatnews.navigation.CategoriesRoute" -> CategoriesRoute
        "com.linli.blackcatnews.navigation.FavoritesRoute" -> FavoritesRoute
        "com.linli.blackcatnews.navigation.SettingsRoute" -> SettingsRoute
        else -> null
    }
}
