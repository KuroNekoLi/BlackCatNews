package com.linli.blackcatnews.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun LoginRequiredDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
    ) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("前往登入")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("稍後")
            }
        },
        title = { Text("需要登入") },
        text = { Text("您需要登入才能使用此功能喔") }
    )
}
