package com.linli.blackcatnews.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.linli.blackcatnews.navigation.CategoriesRoute
import com.linli.blackcatnews.navigation.FavoritesRoute
import com.linli.blackcatnews.navigation.HomeRoute
import com.linli.blackcatnews.navigation.SettingsRoute
import com.linli.blackcatnews.navigation.WordBankRoute

/**
 * 底部導航項目定義
 */
data class BottomNavItem(
    val route: Any,
    val iconText: String,
    val label: String
)

/**
 * 底部導航欄
 */
@Composable
fun AppBottomNavigation(
    currentRoute: Any?,
    onNavigate: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(HomeRoute, "🏠", "首頁"),
        BottomNavItem(WordBankRoute, "📚", "單字庫"),
        // BottomNavItem(CategoriesRoute, "📑", "分類"),  // 暫時註解，功能尚未完整實作
        // BottomNavItem(FavoritesRoute, "❤️", "收藏"),  // 暫時註解，需在文章詳情中實作收藏UI
        BottomNavItem(SettingsRoute, "⚙️", "設定")
    )

    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            val selected = currentRoute?.let { it::class == item.route::class } ?: false

            NavigationBarItem(
                icon = { Text(item.iconText, style = MaterialTheme.typography.titleLarge) },
                label = { Text(item.label) },
                selected = selected,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
