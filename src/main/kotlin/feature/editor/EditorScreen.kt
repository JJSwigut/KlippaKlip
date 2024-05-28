package feature.editor

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import data.models.Klip

data class EditorScreen(
    val klip: Klip? = null
) : Screen {
    @Composable
    override fun Content() {

    }
}