package feature.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState

@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
) {
    val viewState by viewModel.state.collectAsState()

    Window(
        onCloseRequest = { viewModel.handleAction(EditorAction.HandleExit) },
        title = viewState.windowTitle,
        state = rememberWindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition(Alignment.Center),
            size = DpSize(width = 600.dp, height = 400.dp)
        ),
        alwaysOnTop = true,
    ) {
        EditorContent(
            viewState = viewState,
            actionHandler = viewModel::handleAction
        )
    }
}

@Composable
private fun EditorContent(
    viewState: EditorViewState,
    actionHandler: (EditorAction) -> Unit,
) {
    var titleTextValue by remember(viewState.klip) { mutableStateOf(TextFieldValue(viewState.klip?.title ?: "")) }
    var klipTextValue by remember(viewState.klip) { mutableStateOf(TextFieldValue(viewState.klip?.itemText ?: "")) }

    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = titleTextValue,
                onValueChange = { titleTextValue = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth().weight(1f)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = klipTextValue,
                onValueChange = {
                    klipTextValue = it
                },
                label = { Text("Add your klip here") },
                isError = klipTextValue.text.isBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .weight(2f)
            )

            Spacer(Modifier.height(16.dp))

            Button(
                enabled = klipTextValue.text.isNotBlank(),
                onClick = {
                    actionHandler(
                        EditorAction.HandleSave(
                            titleTextValue.text.takeIf { it.isNotBlank() },
                            klipTextValue.text
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Create")
            }
        }
    }
}
