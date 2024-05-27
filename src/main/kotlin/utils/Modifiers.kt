package utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.onDoubleClick(
    onDoubleClick: () -> Unit,
): Modifier {
    return onClick(onDoubleClick = onDoubleClick) {
    }
}