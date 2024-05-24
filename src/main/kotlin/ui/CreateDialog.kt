package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CreateDialog(
    onDismissRequest: () -> Unit,
    onCreateClicked: (String?, String) -> Unit,
) {
    var titleTextValue by remember { mutableStateOf(TextFieldValue()) }
    var textTextValue by remember { mutableStateOf(TextFieldValue()) }

    Dialog(
        properties = DialogProperties(),
        onDismissRequest = onDismissRequest,
    ) {
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
                    value = textTextValue,
                    onValueChange = {
                        textTextValue = it
                    },
                    label = { Text("Add your klip here") },
                    isError = textTextValue.text.isBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .weight(2f)
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    enabled = textTextValue.text.isNotBlank(),
                    onClick = {
                            onCreateClicked(titleTextValue.text.takeIf { it.isNotBlank() }, textTextValue.text)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Create")
                }
            }
        }
    }
}

@Composable
@Preview
fun CreateDialogPreview() {
    CreateDialog(
        onDismissRequest = {},
        onCreateClicked = { title, text -> }
    )
}