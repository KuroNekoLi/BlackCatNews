package com.linli.blackcatnews.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 加載中的提示內容。
 */
@Composable
fun LoadingTooltipContent(
    word: String,
    onDismiss: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.width(320.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 單字及操作按鈕
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // 關閉提示框
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "關閉提示框"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 加載中的提示
            Text(
                text = "正在載入字典定義...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
fun LoadingTooltipContentPreview() {
    MaterialTheme {
        LoadingTooltipContent(
            word = "example",
            onDismiss = {}
        )
    }
}

@Preview
@Composable
fun LongWordLoadingTooltipContentPreview() {
    MaterialTheme {
        LoadingTooltipContent(
            word = "internationalization",
            onDismiss = {}
        )
    }
}

@Preview
@Composable
fun ChineseWordLoadingTooltipContentPreview() {
    MaterialTheme {
        LoadingTooltipContent(
            word = "字典",
            onDismiss = {}
        )
    }
}