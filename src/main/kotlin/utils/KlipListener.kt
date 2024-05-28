package utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import feature.Output
import kotlinx.coroutines.delay

@Composable
fun KlipListener(
    clipboardManager: ClipboardManager,
    onOutput: (KlipListenerOutput) -> Unit
) {
    LaunchedEffect(clipboardManager) {
        while (true) {
            val clipboardText = clipboardManager.getText()
            clipboardText?.let {
                onOutput(KlipListenerOutput.SaveHistoryKlip(it))
            }
            delay(2000) // Poll every 2 seconds
        }
    }
}

sealed interface KlipListenerOutput : Output {
    data class SaveHistoryKlip(val text: AnnotatedString) : KlipListenerOutput
}