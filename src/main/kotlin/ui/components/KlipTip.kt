package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun KlipTip(
    text: String,
) {
    Column(
        modifier = Modifier.background(
            color = MaterialTheme.colors.secondary,
            shape = MaterialTheme.shapes.large
        )
    ) {
        Text(
            style = MaterialTheme.typography.body2,
            text = text,
            color = MaterialTheme.colors.onSecondary,
            modifier = Modifier.padding(8.dp),
            overflow = TextOverflow.Ellipsis,
        )
    }
}