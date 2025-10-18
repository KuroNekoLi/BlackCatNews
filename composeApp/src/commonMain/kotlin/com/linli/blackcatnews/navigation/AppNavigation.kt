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
import androidx.navigation.toRoute
import com.linli.blackcatnews.ui.components.AppBottomNavigation
import com.linli.blackcatnews.ui.screens.ArticleDetailScreen
import com.linli.blackcatnews.ui.screens.CategoriesScreen
import com.linli.blackcatnews.ui.screens.FavoritesScreen
import com.linli.blackcatnews.ui.screens.HomeScreen
import com.linli.blackcatnews.ui.screens.NotificationsScreen
import com.linli.blackcatnews.ui.screens.SearchScreen
import com.linli.blackcatnews.ui.screens.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import com.linli.blackcatnews.presentation.viewmodel.ArticleDetailViewModel
import com.linli.blackcatnews.presentation.viewmodel.FavoritesViewModel
import com.linli.blackcatnews.presentation.viewmodel.HomeViewModel
import com.linli.blackcatnews.presentation.viewmodel.SearchViewModel
import com.linli.blackcatnews.presentation.viewmodel.SettingsViewModel
import org.koin.compose.koinInject

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
                            navController.navigate(SearchRoute) {
                                launchSingleTop = true
                            }
                        },
                        onNotificationClick = {
                            // TODO: Implement cross-platform notification functionality
                            // navController.navigate(NotificationsRoute) {
                            //     launchSingleTop = true
                            // }
                        },
                        showActions = isHomeRoute(currentRoute),
                        showBackButton = isDetailRoute(currentRoute),
                        onBackClick = {
                            navController.navigateUp()
                        }
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
                val viewModel: HomeViewModel = koinViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onNewsItemClick = { newsItem ->
                        // 導航到文章詳情頁
                        navController.navigate(
                            ArticleDetailRoute(
                                articleId = newsItem.id,
                                title = newsItem.title
                            )
                        )
                    }
                )
            }

            // 文章詳情頁
            composable<ArticleDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ArticleDetailRoute>()
                val viewModel: ArticleDetailViewModel =
                    koinViewModel { parametersOf(route.articleId) }
                ArticleDetailScreen(
                    viewModel = viewModel,
                    onBackClick = {
                        navController.navigateUp()
                    }
                )
            }

            // 分類頁面
            composable<CategoriesRoute> {
                CategoriesScreen()
            }

            // 收藏頁面
            composable<FavoritesRoute> {
                val viewModel: FavoritesViewModel = koinViewModel()
                FavoritesScreen(
                    viewModel = viewModel,
                    onNewsItemClick = { newsItem ->
                        navController.navigate(
                            ArticleDetailRoute(
                                articleId = newsItem.id,
                                title = newsItem.title
                            )
                        )
                    }
                )
            }

            // 設定頁面
            composable<SettingsRoute> {
                SettingsScreen(viewModel = koinInject())
            }

            // 搜尋頁面
            composable<SearchRoute> {
                val viewModel: SearchViewModel = koinViewModel()
                SearchScreen(
                    viewModel = viewModel,
                    onNewsItemClick = { newsItem ->
                        navController.navigate(
                            ArticleDetailRoute(
                                articleId = newsItem.id,
                                title = newsItem.title
                            )
                        )
                    }
                )
            }

            // 通知頁面 (暫時註解，等待跨平台通知功能完整實作)
            // composable<NotificationsRoute> {
            //     NotificationsScreen()
            // }
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
    showActions: Boolean,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Text("←", style = MaterialTheme.typography.titleLarge)
                }
            }
        },
        actions = {
            if (showActions) {
                IconButton(onClick = onSearchClick) {
                    Text("🔍", style = MaterialTheme.typography.titleLarge)
                }
                // IconButton(onClick = onNotificationClick) {
                //     Text("🔔", style = MaterialTheme.typography.titleLarge)
                // }
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
        "com.linli.blackcatnews.navigation.SettingsRoute",
        "com.linli.blackcatnews.navigation.SearchRoute",
        // "com.linli.blackcatnews.navigation.NotificationsRoute",  // 暫時註解
        "com.linli.blackcatnews.navigation.ArticleDetailRoute"
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
 * 判斷是否為詳情頁路由（文章詳情、搜尋、通知）
 */
private fun isDetailRoute(route: String?): Boolean {
    return route in listOf(
        "com.linli.blackcatnews.navigation.ArticleDetailRoute",
        "com.linli.blackcatnews.navigation.SearchRoute"
        // "com.linli.blackcatnews.navigation.NotificationsRoute"  // 暫時註解
    )
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
        "com.linli.blackcatnews.navigation.SearchRoute" -> "搜尋"
        // "com.linli.blackcatnews.navigation.NotificationsRoute" -> "通知"  // 暫時註解
        "com.linli.blackcatnews.navigation.ArticleDetailRoute" -> "文章詳情"
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
