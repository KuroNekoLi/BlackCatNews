package com.linli.blackcatnews.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import blackcatnews.composeapp.generated.resources.Res
import blackcatnews.composeapp.generated.resources.title_article_detail
import blackcatnews.composeapp.generated.resources.title_categories
import blackcatnews.composeapp.generated.resources.title_favorites
import blackcatnews.composeapp.generated.resources.title_home
import blackcatnews.composeapp.generated.resources.title_search
import blackcatnews.composeapp.generated.resources.title_settings
import com.linli.authentication.ProviderType
import com.linli.authentication.domain.SignInUIClient
import com.linli.authentication.domain.usecase.GetCurrentUserUseCase
import com.linli.blackcatnews.presentation.viewmodel.ArticleDetailViewModel
import com.linli.blackcatnews.presentation.viewmodel.FavoritesViewModel
import com.linli.blackcatnews.presentation.viewmodel.HomeViewModel
import com.linli.blackcatnews.presentation.viewmodel.SearchViewModel
import com.linli.blackcatnews.ui.components.AppBottomNavigation
import com.linli.blackcatnews.ui.screens.ArticleDetailScreen
import com.linli.blackcatnews.ui.screens.CategoriesScreen
import com.linli.blackcatnews.ui.screens.FavoritesScreen
import com.linli.blackcatnews.ui.screens.HomeScreen
import com.linli.blackcatnews.ui.screens.RegisterScreen
import com.linli.blackcatnews.ui.screens.SearchScreen
import com.linli.blackcatnews.ui.screens.SettingsScreen
import com.linli.blackcatnews.ui.screens.SignInScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * 平台特定函數：建立 SignIn UIClients
 *
 * Android: 使用 Activity 建立 GoogleUIClient
 * iOS: 使用 AuthProvider 建立 GoogleUIClient 和 AppleUIClient
 */
@Composable
expect fun rememberSignInUIClients(): Map<ProviderType, SignInUIClient>

/**
 * 主導航結構
 * 使用統一的 Scaffold 管理頂部欄和底部導航
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = currentBackStackEntry?.destination

    // Check if user is already authenticated
    val getCurrentUserUseCase: GetCurrentUserUseCase = koinInject()
    val isAuthenticated = getCurrentUserUseCase.isAuthenticated()

    // Determine start destination based on authentication state
    val startDestination = if (isAuthenticated) HomeRoute else SignInRoute

    Scaffold(
        topBar = {
            // 根據當前路由顯示不同的頂部欄
            when {
                shouldShowTopBar(currentDestination) -> {
                    AppTopBar(
                        title = getTopBarTitle(currentDestination),
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
                        showActions = isHomeDestination(currentDestination),
                        showBackButton = isDetailDestination(currentDestination),
                        onBackClick = {
                            navController.navigateUp()
                        }
                    )
                }
            }
        },
        bottomBar = {
            // 只在主要頁面顯示底部導航
            if (shouldShowBottomBar(currentDestination)) {
                AppBottomNavigation(
                    currentRoute = getCurrentRouteObject(currentDestination),
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
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 登入頁面
            composable<SignInRoute> {
                SignInScreen(
                    onNavigateToHome = {
                        navController.navigate(HomeRoute) {
                            popUpTo(SignInRoute) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(RegisterRoute) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            // 註冊頁面
            composable<RegisterRoute> {
                RegisterScreen(
                    onNavigateToHome = {
                        navController.navigate(HomeRoute) {
                            popUpTo(SignInRoute) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToSignIn = {
                        navController.navigateUp()
                    }
                )
            }

            // 首頁
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
                SettingsScreen(
                    viewModel = koinInject(),
                    onNavigateToSignIn = {
                        navController.navigate(SignInRoute) {
                            // Clear entire back stack when signing out
                            popUpTo(0) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
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
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (showActions) {
                IconButton(onClick = onSearchClick) {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
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
private fun shouldShowTopBar(destination: NavDestination?): Boolean {
    return destination?.hierarchy?.any {
        it.hasRoute<HomeRoute>() ||
                it.hasRoute<CategoriesRoute>() ||
                it.hasRoute<FavoritesRoute>() ||
                it.hasRoute<SettingsRoute>() ||
                it.hasRoute<SearchRoute>() ||
                it.hasRoute<ArticleDetailRoute>()
    } == true
}

/**
 * 判斷是否應該顯示底部導航欄
 */
private fun shouldShowBottomBar(destination: NavDestination?): Boolean {
    return destination?.hierarchy?.any {
        it.hasRoute<HomeRoute>() ||
                it.hasRoute<CategoriesRoute>() ||
                it.hasRoute<FavoritesRoute>() ||
                it.hasRoute<SettingsRoute>()
    } == true
}

/**
 * 判斷是否為首頁路由
 */
private fun isHomeDestination(destination: NavDestination?): Boolean {
    return destination?.hierarchy?.any { it.hasRoute<HomeRoute>() } == true
}

/**
 * 判斷是否為詳情頁路由（文章詳情、搜尋、通知）
 */
private fun isDetailDestination(destination: NavDestination?): Boolean {
    return destination?.hierarchy?.any {
        it.hasRoute<ArticleDetailRoute>() ||
                it.hasRoute<SearchRoute>()
    } == true
}

/**
 * 根據路由獲取頂部欄標題
 */
@Composable
private fun getTopBarTitle(destination: NavDestination?): String {
    return when {
        destination?.hierarchy?.any { it.hasRoute<HomeRoute>() } == true -> stringResource(Res.string.title_home)
        destination?.hierarchy?.any { it.hasRoute<CategoriesRoute>() } == true -> stringResource(Res.string.title_categories)
        destination?.hierarchy?.any { it.hasRoute<FavoritesRoute>() } == true -> stringResource(Res.string.title_favorites)
        destination?.hierarchy?.any { it.hasRoute<SettingsRoute>() } == true -> stringResource(Res.string.title_settings)
        destination?.hierarchy?.any { it.hasRoute<SearchRoute>() } == true -> stringResource(Res.string.title_search)
        destination?.hierarchy?.any { it.hasRoute<ArticleDetailRoute>() } == true -> stringResource(
            Res.string.title_article_detail
        )

        else -> stringResource(Res.string.title_home)
    }
}

/**
 * 從路由字符串獲取路由對象
 */
private fun getCurrentRouteObject(destination: NavDestination?): Any? {
    return when {
        destination?.hierarchy?.any { it.hasRoute<HomeRoute>() } == true -> HomeRoute
        destination?.hierarchy?.any { it.hasRoute<CategoriesRoute>() } == true -> CategoriesRoute
        destination?.hierarchy?.any { it.hasRoute<FavoritesRoute>() } == true -> FavoritesRoute
        destination?.hierarchy?.any { it.hasRoute<SettingsRoute>() } == true -> SettingsRoute
        else -> null
    }
}
