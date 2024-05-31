package utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.onClick
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.onDoubleClick(
    onDoubleClick: () -> Unit,
): Modifier {
    return onClick(onDoubleClick = onDoubleClick) {
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.onHover(
    showBorder: Boolean = true,
    onHovered: (Boolean) -> Unit = {},
): Modifier {
    var isHovered by remember { mutableStateOf(false) }
    val borderShape = MaterialTheme.shapes.medium

    val borderModifier = if (showBorder) {
        derivedStateOf {
            if (isHovered) {
                Modifier.border(border = BorderStroke(1.dp, Color.Gray), shape = borderShape)
            } else {
                Modifier
            }
        }.value
    } else {
        Modifier
    }

    return this then Modifier.onPointerEvent(PointerEventType.Enter) {
        isHovered = true
        onHovered(true)
    }.onPointerEvent(PointerEventType.Exit) {
        isHovered = false
        onHovered(false)
    }.thenIf(showBorder) {
        borderModifier
    }
}

inline fun Modifier.thenIf(
    condition: Boolean,
    crossinline other: Modifier.() -> Modifier,
) = if (condition) {
    this.then(other())
} else {
    this
}