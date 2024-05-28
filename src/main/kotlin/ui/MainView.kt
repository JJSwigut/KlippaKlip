package ui

import Strings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import data.models.HistoryKlip
import data.models.Klip
import feature.mainscreen.MainAction
import feature.mainscreen.MainAction.HandleCopy
import feature.mainscreen.MainAction.HandleCreateClicked
import feature.mainscreen.MainAction.HandleDelete
import feature.mainscreen.MainAction.HandlePin
import feature.mainscreen.MainViewState
import utils.onDoubleClick

@Composable
fun MainView(
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { actionHandler(HandleCreateClicked) }
                ) {
                    Text(text = Strings.createKlip)
                }
            }
        }
    }
}

@Composable
private fun KlipColumn(
    modifier: Modifier = Modifier,
    klips: List<Klip>,
    actionHandler: (MainAction) -> Unit
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
    actionHandler: (MainAction) -> Unit
) {

    val listState = rememberLazyListState()

    LaunchedEffect(historyKlips) {
        if (historyKlips.isNotEmpty()) {
            listState.animateScrollToItem(historyKlips.indexOf(historyKlips[0]))
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .background(color = MaterialTheme.colors.primary, shape = MaterialTheme.shapes.medium)
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
                modifier = Modifier.padding(8.dp),
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
                        Text(
                            color = MaterialTheme.colors.onPrimary,
                            modifier = Modifier.fillMaxWidth()
                                .onDoubleClick { actionHandler(HandleCopy(klip)) },
                            text = klip.text,
                            maxLines = 2,
                            style = MaterialTheme.typography.body2,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KlipTip(
    text: String
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
        contentPadding = PaddingValues(horizontal = 4.dp)
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
    actionHandler: (MainAction) -> Unit
) {
    TooltipArea(
        delayMillis = 750,
        tooltip = {
            KlipTip(item.itemText)
        }
    ) {
        Card(
            modifier = Modifier.onDoubleClick {
                actionHandler(HandleCopy(item))
            },
            backgroundColor = MaterialTheme.colors.primary,
        ) {
            Row(modifier = Modifier.size(width = 200.dp, height = 150.dp)) {
                Column(modifier = Modifier.weight(5f).padding(8.dp)) {
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
                        style = MaterialTheme.typography.body2
                    )
                }
                Box(
                    modifier = Modifier.width(1.dp)
                        .background(color = MaterialTheme.colors.onPrimary)
                        .fillMaxHeight()
                )

                val pinIcon = if (item.isPinned) Icons.Outlined.PushPin else Icons.Filled.PushPin

                CardActions(
                    modifier = Modifier.padding(8.dp).weight(1f, fill = false),
                    pinIcon = pinIcon,
                    onCopy = { actionHandler(HandleCopy(item)) },
                    onPin = { actionHandler(HandlePin(item)) },
                    onDelete = { actionHandler(HandleDelete(item)) }
                )
            }
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
) {
    Column(modifier) {
        Icon(
            modifier = Modifier.size(15.dp).weight(1f).padding(1.dp).clickable { onPin() },
            imageVector = pinIcon,
            contentDescription = null,
        )
        Icon(
            modifier = Modifier.size(15.dp).weight(1f).padding(1.dp).clickable { onCopy() },
            imageVector = Icons.Default.ContentCopy,
            contentDescription = null,
        )
        Icon(
            modifier = Modifier.size(15.dp).weight(1f).padding(1.dp).clickable { onDelete() },
            imageVector = Icons.Default.Delete,
            contentDescription = null,
        )
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    type: String
) {
    Text(
        modifier = modifier,
        color = MaterialTheme.colors.onPrimary,
        text = Strings.emptyStateString(type), style = MaterialTheme.typography.body2
    )
}