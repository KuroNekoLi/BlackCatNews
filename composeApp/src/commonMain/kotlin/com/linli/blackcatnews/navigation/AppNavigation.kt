package com.linli.blackcatnews.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import blackcatnews.composeapp.generated.resources.title_word_bank
import com.linli.authentication.ProviderType
import com.linli.authentication.domain.SignInUIClient
import com.linli.authentication.domain.usecase.GetCurrentUserUseCase
import com.linli.blackcatnews.presentation.viewmodel.ArticleDetailViewModel
import com.linli.blackcatnews.presentation.viewmodel.FavoritesViewModel
import com.linli.blackcatnews.presentation.viewmodel.HomeViewModel
import com.linli.blackcatnews.presentation.viewmodel.RatingViewModel
import com.linli.blackcatnews.presentation.viewmodel.SearchViewModel
import com.linli.blackcatnews.ui.common.BackIcon
import com.linli.blackcatnews.ui.components.AppBottomNavigation
import com.linli.blackcatnews.ui.screens.ArticleDetailScreen
import com.linli.blackcatnews.ui.screens.CategoriesScreen
import com.linli.blackcatnews.ui.screens.FavoritesScreen
import com.linli.blackcatnews.ui.screens.HomeScreen
import com.linli.blackcatnews.ui.screens.LearningHubScreen
import com.linli.blackcatnews.ui.screens.RegisterScreen
import com.linli.blackcatnews.ui.screens.SearchScreen
import com.linli.blackcatnews.ui.screens.SettingsScreen
import com.linli.blackcatnews.ui.screens.SignInScreen
import com.linli.blackcatnews.ui.screens.WordBankScreen
import com.linli.blackcatnews.ui.screens.WordReviewScreen
import com.linli.dictionary.presentation.wordbank.WordBankViewModel
import kotlinx.serialization.Serializable
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
 * 固定起點：SplashRoute（決策頁）
 */
@Serializable
object SplashRoute

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

    // 創建全局的 RatingViewModel，用於處理評分邏輯
    val ratingViewModel: RatingViewModel = koinViewModel()

    // State for dynamic top bar actions
    var topBarActions by remember { mutableStateOf<(@Composable RowScope.() -> Unit)?>(null) }

    Scaffold(
        topBar = {
            // 根據當前路由顯示不同的頂部欄
            when {
                // 首頁使用自定義 Header，不顯示 AppTopBar
                isHomeDestination(currentDestination) -> {
                    // No TopAppBar for Home, handled internally
                }
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
                        showActions = false, // Remove default actions for non-home screens
                        showBackButton = isDetailDestination(currentDestination),
                        onBackClick = {
                            // 如果是文章詳情頁，觸發評分
                            if (currentDestination?.hierarchy?.any { it.hasRoute<ArticleDetailRoute>() } == true) {
                                ratingViewModel.onArticleRead()
                            }
                            navController.navigateUp()
                        },
                        actions = topBarActions
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
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SplashRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 啟動頁（決策頁）
            composable<SplashRoute> {
                LaunchedEffect(Unit) {
                    navController.navigate(HomeRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            // 登入頁面
            composable<SignInRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<SignInRoute>()
                SignInScreen(
                    onNavigateToHome = {
                        if (route.returnToSettings) {
                            navController.navigateUp()
                        } else {
                            navController.navigate(HomeRoute) {
                                popUpTo(SignInRoute()) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
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
                            popUpTo(SignInRoute()) {
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
                        navController.navigate(
                            LearningHubRoute(
                                articleId = newsItem.id,
                                title = newsItem.title
                            )
                        )
                    },
                    onNavigateToSettings = {
                        navController.navigate(SettingsRoute) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToSearch = {
                        navController.navigate(SearchRoute) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            // 學習總覽頁
            composable<LearningHubRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<LearningHubRoute>()
                LearningHubScreen(
                    articleId = route.articleId,
                    onNavigateToArticle = {
                        navController.navigate(
                            ArticleDetailRoute(
                                articleId = route.articleId,
                                title = route.title
                            )
                        )
                    },
                    onBackClick = {
                        navController.navigateUp()
                    },
                    onSetTopBarActions = { topBarActions = it }
                )
            }

            // 文章詳情頁
            composable<ArticleDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ArticleDetailRoute>()
                val getCurrentUserUseCase: GetCurrentUserUseCase = koinInject()
                val viewModel: ArticleDetailViewModel =
                    koinViewModel { parametersOf(route.articleId) }
                ArticleDetailScreen(
                    viewModel = viewModel,
                    onBackClick = {
                        // 使用者返回時觸發評分請求
                        ratingViewModel.onArticleRead()
                        navController.navigateUp()
                    },
                    onRequireSignIn = { getCurrentUserUseCase.isAuthenticated() },
                    onNavigateToSignIn = {
                        navController.navigate(SignInRoute()) {
                            launchSingleTop = true
                        }
                    },
                    onSetTopBarActions = { topBarActions = it }
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

            // 單字庫頁面
            composable<WordBankRoute> {
                val viewModel: WordBankViewModel = koinViewModel()
                WordBankScreen(
                    viewModel = viewModel,
                    onNavigateToReview = {
                        navController.navigate(WordReviewRoute) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            // 單字複習頁面
            composable<WordReviewRoute> {
                WordReviewScreen(
                    onBackClick = {
                        navController.navigateUp()
                    }
                )
            }

            // 設定頁面
            composable<SettingsRoute> {
                SettingsScreen(
                    viewModel = koinInject(),
                    onNavigateToSignIn = {
                        navController.navigate(SignInRoute(returnToSettings = true)) {
                            launchSingleTop = true
                        }
                    },
                    onBackClick = { navController.navigateUp() } // Pass back click handler
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
    onBackClick: () -> Unit = {},
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackButton) {
                BackIcon { onBackClick() }
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
            actions?.invoke(this)
        },
//        colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer,
//            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//        )
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
                it.hasRoute<WordBankRoute>() ||
                it.hasRoute<WordReviewRoute>() ||
                it.hasRoute<SettingsRoute>() ||
                it.hasRoute<SearchRoute>() ||
                it.hasRoute<ArticleDetailRoute>() ||
                it.hasRoute<LearningHubRoute>()
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
                it.hasRoute<WordBankRoute>()
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
                it.hasRoute<SearchRoute>() ||
                it.hasRoute<WordReviewRoute>() ||
                it.hasRoute<LearningHubRoute>()
    } == true
}

/**
 * 根據路由獲取頂部欄標題
 */
@Composable
private fun getTopBarTitle(destination: NavDestination?): String {
    return when {
        destination?.hierarchy?.any { it.hasRoute<CategoriesRoute>() } == true -> stringResource(Res.string.title_categories)
        destination?.hierarchy?.any { it.hasRoute<FavoritesRoute>() } == true -> stringResource(Res.string.title_favorites)
        destination?.hierarchy?.any { it.hasRoute<WordBankRoute>() } == true -> stringResource(Res.string.title_word_bank)
        destination?.hierarchy?.any { it.hasRoute<WordReviewRoute>() } == true -> "複習" // TODO: Add to resources
        destination?.hierarchy?.any { it.hasRoute<SettingsRoute>() } == true -> stringResource(Res.string.title_settings)
        destination?.hierarchy?.any { it.hasRoute<SearchRoute>() } == true -> stringResource(Res.string.title_search)
        destination?.hierarchy?.any { it.hasRoute<ArticleDetailRoute>() } == true -> stringResource(
            Res.string.title_article_detail
        )
        destination?.hierarchy?.any { it.hasRoute<LearningHubRoute>() } == true -> stringResource(
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
        destination?.hierarchy?.any { it.hasRoute<WordBankRoute>() } == true -> WordBankRoute
        destination?.hierarchy?.any { it.hasRoute<SettingsRoute>() } == true -> SettingsRoute
        else -> null
    }
}
