package com.linli.blackcatnews.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.isDebug

/**
 * A badge that displays when the app is running in debug mode
 * Only visible in debug builds
 */
@Composable
fun DebugBadge(
    modifier: Modifier = Modifier,
    message: String = "DEBUG"
) {
    // Only show in debug mode
    if (!isDebug) return

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Red.copy(alpha = 0.8f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = message,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}