package feature.mainscreen

import Strings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import data.models.HistoryKlip
import data.models.Klip
import feature.mainscreen.MainAction.HandleCopy
import feature.mainscreen.MainAction.HandleCreateClicked
import feature.mainscreen.MainAction.HandleDelete
import feature.mainscreen.MainAction.HandlePin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ui.components.QuickMessage
import utils.onDoubleClick
import utils.onHover

@Composable
fun MainScreen(
    viewModel: MainViewModel,
) {
    val viewState by viewModel.state.collectAsState()
    val state = rememberWindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition.Aligned(Alignment.TopEnd)
    )

    Window(
        onCloseRequest = { },
        state = state,
        undecorated = true,
        transparent = true,
        alwaysOnTop = true,
    ) {
        WindowDraggableArea {
                MainContent(
                    viewState,
                    viewModel::handleAction
                )
        }
    }
}

@Composable
private fun MainContent(
    viewState: MainViewState,
    actionHandler: (MainAction) -> Unit,
) {
    Surface(
        color = Color.Black.copy(alpha = .7f), shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = CenterVertically
            ) {
                Column(modifier = Modifier.weight(3f)) {
                    if (viewState.pinnedKlips.isEmpty() && viewState.klips.isEmpty()) {
                        EmptyState(
                            modifier = Modifier.padding(8.dp),
                            type = Strings.pinnedItems
                        )
                    } else {
                        if (viewState.pinnedKlips.isNotEmpty()) {
                            PinnedKlipRow(
                                modifier = Modifier.weight(1f),
                                pinnedKlips = viewState.pinnedKlips,
                                actionHandler = actionHandler
                            )
                            Divider(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                thickness = 2.dp,
                                color = Color.Gray
                            )
                        }
                        KlipColumn(
                            modifier = Modifier.weight(3f),
                            klips = viewState.klips,
                            actionHandler = actionHandler,
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                HistoryColumn(
                    modifier = Modifier.weight(1f),
                    historyKlips = viewState.historyKlips,
                    actionHandler = actionHandler
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = CenterVertically,
                horizontalArrangement = SpaceBetween
            ) {
                Button(
                    onClick = { actionHandler(HandleCreateClicked) }
                ) {
                    Text(text = Strings.createKlip)
                }

                QuickMessage(
                    message = "Copied!",
                    show = viewState.showCopiedMessage,
                )

                Button(
                    onClick = { actionHandler(MainAction.HandleDeleteHistory) }
                ) {
                    Text(text = Strings.deleteHistory)
                }
            }
        }
    }
}




@Composable
private fun KlipColumn(
    modifier: Modifier = Modifier,
    klips: List<Klip>,
    actionHandler: (MainAction) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier,
        horizontalArrangement = spacedBy(4.dp),
        verticalArrangement = spacedBy(4.dp),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(horizontal = 4.dp),
        content = {
            items(klips) { klip ->
                KlipCard(
                    item = klip,
                    actionHandler = actionHandler
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryColumn(
    modifier: Modifier = Modifier,
    historyKlips: List<HistoryKlip>,
    actionHandler: (MainAction) -> Unit,
) {

    val listState = rememberLazyListState()

    LaunchedEffect(historyKlips) {
        if (historyKlips.isNotEmpty()) {
            listState.animateScrollToItem(historyKlips.lastIndex)
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxHeight()
    ) {
        if (historyKlips.isEmpty()) {
            EmptyState(
                modifier = Modifier.padding(8.dp),
                type = Strings.historyItems,
            )
        } else {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.Center,
                reverseLayout = true
            ) {
                items(historyKlips) { klip ->
                    TooltipArea(
                        delayMillis = 750,
                        tooltip = {
                            KlipTip(klip.text)
                        }
                    ) {
                        Column {
                            Box(
                                modifier = Modifier.background(
                                    color = MaterialTheme.colors.primary,
                                    shape = MaterialTheme.shapes.medium
                                ).onHover(
                                    onHovered = {
                                        //todo add delete single history item
                                    }
                                ).onDoubleClick { actionHandler(HandleCopy(klip)) },
                                contentAlignment = Center
                            ) {
                                Text(
                                    color = MaterialTheme.colors.onPrimary,
                                    modifier = Modifier.fillMaxWidth().padding(2.dp),
                                    text = klip.text,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.body2,
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KlipTip(
    text: String,
) {
    Box(
        modifier = Modifier.background(
            color = MaterialTheme.colors.secondary,
            shape = MaterialTheme.shapes.large
        ).scrollable(rememberScrollState(), Vertical), contentAlignment = Alignment.Center
    ) {
        Text(
            style = MaterialTheme.typography.body2,
            text = text,
            color = MaterialTheme.colors.onSecondary,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun PinnedKlipRow(
    modifier: Modifier = Modifier,
    pinnedKlips: List<Klip>,
    actionHandler: (MainAction) -> Unit,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = spacedBy(4.dp),
    ) {
        items(pinnedKlips) { klip ->
            KlipCard(
                item = klip,
                actionHandler = actionHandler,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KlipCard(
    item: Klip,
    actionHandler: (MainAction) -> Unit,
) {
    var showActions by remember { mutableStateOf(false) }
    var shouldShowToolTip by remember { mutableStateOf(true) }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Box(
        modifier = Modifier
            .onDoubleClick {
                actionHandler(HandleCopy(item))
            }
            .onHover {
                showActions = it
                shouldShowToolTip = it && (textLayoutResult?.hasVisualOverflow == true)
            }
            .background(MaterialTheme.colors.primary)
            .size(width = 200.dp, height = 150.dp)
    ) {
        TooltipArea(
            delayMillis = 750,
            tooltip = {
                if (shouldShowToolTip) {
                    KlipTip(item.itemText)
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                item.title?.let { title ->
                    Text(
                        color = MaterialTheme.colors.onPrimary,
                        maxLines = 1,
                        text = title,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.body2
                    )
                }

                Text(
                    color = MaterialTheme.colors.onPrimary,
                    text = item.itemText,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body2,
                    onTextLayout = { layoutResult ->
                        textLayoutResult = layoutResult
                    }
                )
            }
        }
        AnimatedVisibility(
            modifier = Modifier.align(CenterEnd),
            visible = showActions,
            enter = slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut()
        ) {
            CardActions(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(20.dp),
                pinIcon = if (item.isPinned) Icons.Outlined.PushPin else Icons.Filled.PushPin,
                onCopy = { actionHandler(HandleCopy(item)) },
                onPin = { actionHandler(HandlePin(item)) },
                onDelete = { actionHandler(HandleDelete(item)) },
                onEdit = { actionHandler(MainAction.HandleEdit(item)) }
            )
        }
    }
}
@Composable
private fun CardActions(
    modifier: Modifier = Modifier,
    pinIcon: ImageVector,
    onCopy: () -> Unit,
    onPin: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
) {
    Column(
        modifier.background(
            color = Color.Black.copy(alpha = .8f),
            shape = MaterialTheme.shapes.medium
        ), horizontalAlignment = CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(15.dp).weight(1f).padding(1.dp).clickable { onPin() },
            imageVector = pinIcon,
            contentDescription = null,
            tint = MaterialTheme.colors.onPrimary
        )
        Icon(
            modifier = Modifier.size(15.dp).weight(1f).padding(1.dp).clickable { onCopy() },
            imageVector = Icons.Default.ContentCopy,
            contentDescription = null,
            tint = MaterialTheme.colors.onPrimary
        )
        Icon(
            modifier = Modifier.size(15.dp).weight(1f).padding(1.dp).clickable { onEdit() },
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            tint = MaterialTheme.colors.onPrimary
        )
        Icon(
            modifier = Modifier.size(15.dp).weight(1f).padding(1.dp).clickable { onDelete() },
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    type: String,
) {
    Text(
        textAlign = TextAlign.Center,
        modifier = modifier,
        color = MaterialTheme.colors.onPrimary,
        text = Strings.emptyStateString(type), style = MaterialTheme.typography.body2
    )
}