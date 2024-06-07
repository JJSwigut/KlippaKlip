package feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchColors
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.awtEventOrNull
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import data.models.KlipSettings
import data.models.SortOrder
import kotlinx.coroutines.flow.distinctUntilChanged
import utils.thenIf

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
) {
    val viewState by viewModel.state.collectAsState()

    Window(
        onCloseRequest = { viewModel.handleAction(SettingsAction.HandleExit) },
        title = SettingsStrings.WindowTitle,
        state = rememberWindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition(Alignment.Center),
            size = DpSize(width = 600.dp, height = 700.dp)
        ),
        alwaysOnTop = true,
    ) {
        SettingsContent(
            viewState,
            viewModel::handleAction
        )
    }
}

@Composable
private fun SettingsContent(
    viewState: SettingsViewState,
    actionHandler: (SettingsAction) -> Unit,
) {
    var settings by remember { mutableStateOf(viewState.settings) }

    fun updateSettings(update: KlipSettings.() -> KlipSettings) {
        settings = settings.update()
    }

    Surface(color = MaterialTheme.colors.primary) {
        Column(
            modifier = Modifier.padding(16.dp).wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            SettingsRow(
                title = SettingsStrings.TrackHistory,
                subtitle = SettingsStrings.TrackHistorySubtitle,
                endContent = {
                    SettingsSwitch(
                        checked = settings.shouldTrackHistory,
                        onCheckedChange = {
                            updateSettings { copy(shouldTrackHistory = it) }
                        }
                    )
                }
            )
            SettingsRow(
                title = SettingsStrings.KlipSortOrder,
                endContent = {
                    DropdownMenuSortOrder(
                        selectedSortOrder = settings.klipSortOrder,
                        onSelectSortOrder = { updateSettings { copy(klipSortOrder = it) } }
                    )
                }
            )
            SettingsRow(
                title = SettingsStrings.PinSortOrder,
                endContent = {
                    DropdownMenuSortOrder(
                        selectedSortOrder = settings.pinnedSortOrder,
                        onSelectSortOrder = { updateSettings { copy(pinnedSortOrder = it) } }
                    )
                }
            )
            val openHotKeysSubtitle = remember {
                SettingsStrings.CurrentKeys + settings.openHotKeys.toHotKeyString()
            }
            SettingsRow(
                title = SettingsStrings.OpenHotKeys,
                subtitle = openHotKeysSubtitle,
                endContent = {
                    HotKeyField(
                        currentHotkeys = remember { settings.openHotKeys },
                        onHotkeyChange = { updateSettings { copy(openHotKeys = it) } }
                    )
                }
            )

            val closeHotKeysSubtitle = remember {
                SettingsStrings.CurrentKeys + settings.closeHotKeys.toHotKeyString()
            }
            SettingsRow(
                title = SettingsStrings.CloseHotKeys,
                subtitle = closeHotKeysSubtitle,
                endContent = {
                    HotKeyField(
                        currentHotkeys = remember { settings.closeHotKeys },
                        onHotkeyChange = { updateSettings { copy(closeHotKeys = it) } }
                    )
                }
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { actionHandler(SettingsAction.HandleSave(settings)) },
                modifier = Modifier.align(CenterHorizontally).padding(16.dp).heightIn(48.dp).border(width = 1.dp, color = MaterialTheme.colors.onPrimary, shape = MaterialTheme.shapes.medium)
            ) {
                Text(SettingsStrings.SaveButton)
            }
        }
    }
}

private fun List<Int>.toHotKeyString(): String {
    return this.joinToString(" + ") {
        java.awt.event.KeyEvent.getKeyText(it)
    }
}

@Composable
private fun SettingsSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.Green,
            uncheckedThumbColor = Color.Red,
            )
    )
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String? = null,
    endContent: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(2f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(title, style = MaterialTheme.typography.h6, color = MaterialTheme.colors.onPrimary)
            subtitle?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            endContent()
        }
    }
}

@Composable
fun DropdownMenuSortOrder(selectedSortOrder: SortOrder, onSelectSortOrder: (SortOrder) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Text(text = selectedSortOrder.name, modifier = Modifier
            .clickable { expanded = true }, color = MaterialTheme.colors.onPrimary
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SortOrder.entries.forEach { sortOrder ->
                DropdownMenuItem(onClick = {
                    onSelectSortOrder(sortOrder)
                    expanded = false
                }) {
                    Text(text = sortOrder.name, color = MaterialTheme.colors.onPrimary)
                }
            }
        }
    }
}

@Composable
fun HotKeyField(currentHotkeys: List<Int>, onHotkeyChange: (List<Int>) -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val hotKeys = remember { mutableStateListOf<Int>() }
    var hotKeysTextValue by remember(hotKeys) {
        mutableStateOf(TextFieldValue(hotKeys.toHotKeyString()))
    }
    val borderColor = MaterialTheme.colors.onPrimary
    val borderShape = MaterialTheme.shapes.medium

    val borderModifier = remember(isFocused){
        if(isFocused){
            Modifier.border(1.dp, borderColor, borderShape)
        } else {
            Modifier
        }
    }

    TextField(
        value = hotKeysTextValue,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.onPrimary,
            backgroundColor = MaterialTheme.colors.secondary,
            focusedIndicatorColor = MaterialTheme.colors.onPrimary,
            unfocusedIndicatorColor = MaterialTheme.colors.onPrimary,
            cursorColor = MaterialTheme.colors.onSecondary
        ),
        trailingIcon = {
            if (hotKeys.isNotEmpty()) {
                TrailingCloseIcon {
                    hotKeys.clear()
                    onHotkeyChange(currentHotkeys)
                    hotKeysTextValue = TextFieldValue("")
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth().padding(8.dp)
            .focusRequester(remember { FocusRequester() })
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            }
            .onKeyEvent { event: KeyEvent ->
                if (isFocused && event.type == KeyEventType.KeyDown) {
                    val eventKey = event.awtEventOrNull?.keyCode ?: return@onKeyEvent false
                    hotKeys.addHotKey(eventKey)
                    hotKeysTextValue = TextFieldValue(hotKeys.toHotKeyString())
                    onHotkeyChange(hotKeys)
                    true
                } else {
                    false
                }
            }.then(borderModifier)
    )
}

private fun MutableList<Int>.addHotKey(key: Int) {
    if (key !in this) {
        add(key)
    }
    if (size > 3) {
        removeAt(0)
    }
}

@Composable
private fun TrailingCloseIcon(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colors.primary, CircleShape)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Center
    ) {
        Icon(imageVector = Icons.Filled.Close, contentDescription = null)
    }
}

object SettingsStrings {
    const val WindowTitle = "Settings"
    const val SaveButton = "Save"
    const val TrackHistory = "Track History"
    const val TrackHistorySubtitle = "when this is checked, any text added to the system clipboard will show up in the history column"
    const val KlipSortOrder = "Sort Klips By:"
    const val PinSortOrder = "Sort Pinned Klips By:"
    const val CloseHotKeys = "Close Hot Keys"
    const val OpenHotKeys = "Open Hot Keys"
    const val CurrentKeys = "Current Keys: "
}

