package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun KlipTip(
    text: String,
) {
    Column(
        modifier = Modifier.background(
            color = MaterialTheme.colors.secondary,
            shape = MaterialTheme.shapes.large
        ).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.body2,
            text = text,
            color = MaterialTheme.colors.onSecondary,
            overflow = TextOverflow.Visible,
        )
    }
}