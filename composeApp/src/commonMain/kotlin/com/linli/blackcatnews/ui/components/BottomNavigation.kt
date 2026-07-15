package com.linli.blackcatnews.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.linli.blackcatnews.navigation.CategoriesRoute
import com.linli.blackcatnews.navigation.FavoritesRoute
import com.linli.blackcatnews.navigation.HomeRoute
import com.linli.blackcatnews.navigation.WordBankRoute

/**
 * 底部導航項目定義
 */
data class BottomNavItem(
    val route: Any,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
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
        BottomNavItem(HomeRoute, Icons.Filled.Home, "首頁"),
        BottomNavItem(CategoriesRoute, Icons.Filled.Category, "分類"),
        BottomNavItem(WordBankRoute, Icons.Filled.School, "單字庫"),
        BottomNavItem(FavoritesRoute, Icons.Filled.Bookmark, "收藏")
    )

    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            val selected = currentRoute?.let { it::class == item.route::class } ?: false

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = selected,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
