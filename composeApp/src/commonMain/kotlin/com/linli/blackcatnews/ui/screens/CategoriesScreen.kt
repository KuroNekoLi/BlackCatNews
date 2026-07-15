package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// TODO: Backend Data Models
// 這些數據結構應該來自後端 API，目前使用 Mock Data
private data class ProficiencyLevel(
    val id: String,
    val label: String,
    val subLabel: String,
    val color: Color
)

private data class FeaturedCollection(
    val id: String,
    val title: String,
    val subtitle: String,
    val colorStart: Color,
    val colorEnd: Color
)

private data class TopicCategory(
    val id: String,
    val name: String,
    val articleCount: Int,
    val icon: ImageVector,
    val color: Color
)

/**
 * 探索 (Categories) 屏幕
 * 轉型為興趣探索與專項加強中心
 */
@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit = {},
    onLevelClick: (String) -> Unit = {},
    onCollectionClick: (String) -> Unit = {}
) {
    // Mock Data
    val levels = listOf(
        ProficiencyLevel("A2", "初級", "Beginner", Color(0xFF4CAF50)),
        ProficiencyLevel("B2", "中級", "Intermediate", Color(0xFF2196F3)),
        ProficiencyLevel("C1", "高級", "Advanced", Color(0xFF9C27B0))
    )

    val collections = listOf(
        FeaturedCollection(
            "TOEIC",
            "多益必考單字",
            "商業 / 職場",
            Color(0xFFEF5350),
            Color(0xFFFF8A65)
        ),
        FeaturedCollection(
            "TECH",
            "AI 科技趨勢",
            "ChatGPT & More",
            Color(0xFF5C6BC0),
            Color(0xFF26C6DA)
        ),
        FeaturedCollection(
            "TRAVEL",
            "旅遊英語實戰",
            "機場 / 飯店",
            Color(0xFFFFA726),
            Color(0xFFFFEE58)
        )
    )

    val topics = listOf(
        TopicCategory("tech", "科技", 12, Icons.Filled.Computer, Color(0xFFE3F2FD)),
        TopicCategory("business", "商業", 8, Icons.Filled.Business, Color(0xFFE8F5E9)),
        TopicCategory("world", "國際", 24, Icons.Filled.Public, Color(0xFFFFF3E0)),
        TopicCategory("sports", "體育", 5, Icons.Filled.SportsBasketball, Color(0xFFFFEBEE)),
        TopicCategory("health", "健康", 3, Icons.Filled.HealthAndSafety, Color(0xFFF3E5F5)),
        TopicCategory("science", "科學", 7, Icons.Filled.Lightbulb, Color(0xFFE0F7FA)),
        TopicCategory("finance", "財經", 15, Icons.Filled.TrendingUp, Color(0xFFFFF8E1)),
        TopicCategory("entertainment", "娛樂", 9, Icons.Filled.Movie, Color(0xFFFCE4EC))
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Search Bar Placeholder (Visual Only)
        item(span = { GridItemSpan(2) }) {
            SearchBarPlaceholder()
        }

        // Section 2: Proficiency Levels
        item(span = { GridItemSpan(2) }) {
            SectionHeader(title = "選擇程度", subtitle = "找到適合你的文章難度")
        }
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                levels.forEach { level ->
                    LevelCard(
                        level = level,
                        modifier = Modifier.weight(1f),
                        onClick = { onLevelClick(level.id) }
                    )
                }
            }
        }

        // Section 3: Featured Collections
        item(span = { GridItemSpan(2) }) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(title = "精選專題", subtitle = "編輯推薦的學習特輯")
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp) // Adjust for parent padding
                ) {
                    items(collections.size) { index ->
                        CollectionCard(
                            collection = collections[index],
                            onClick = { onCollectionClick(collections[index].id) }
                        )
                    }
                }
            }
        }

        // Section 4: Topics Grid
        item(span = { GridItemSpan(2) }) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(title = "主題探索", subtitle = "挖掘你有興趣的領域")
            }
        }

        items(topics) { topic ->
            TopicCard(
                topic = topic,
                onClick = { onCategoryClick(topic.id) }
            )
        }

        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SearchBarPlaceholder() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "搜尋主題、單字或文章...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LevelCard(
    level: ProficiencyLevel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = level.label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = level.color
            )
            Text(
                text = level.subLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CollectionCard(
    collection: FeaturedCollection,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(100.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(collection.colorStart, collection.colorEnd)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = collection.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = collection.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun TopicCard(
    topic: TopicCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f) // Square-ish
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(topic.color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = topic.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = topic.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${topic.articleCount} 篇文章",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
