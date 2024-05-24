package ui

import Strings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import data.models.HistoryKlip
import data.models.Klippable
import data.models.Klip

@Composable
fun MainScreen(
    pinnedKlips: List<Klip>,
    historyKlips: List<HistoryKlip>,
    onCopyClick: (Klippable) -> Unit,
    onPinKlip: (Klip) -> Unit,
    onCreateKlip: () -> Unit,
) {
    Surface(color = MaterialTheme.colors.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = CenterVertically
            ) {
                PinnedColumn(
                    modifier = Modifier.weight(3f),
                    pinnedKlips = pinnedKlips,
                    onCopyClick = onCopyClick,
                    onPinClick = onPinKlip,
                )
                HistoryColumn(
                    modifier = Modifier.weight(1f),
                    historyKlips = historyKlips,
                    onClick = onCopyClick
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onCreateKlip
                ) {
                    Text(text = Strings.createKlip)
                }
            }
        }
    }
}


@Composable
private fun PinnedColumn(
    modifier: Modifier = Modifier,
    pinnedKlips: List<Klip>,
    onCopyClick: (Klip) -> Unit,
    onPinClick: (Klip) -> Unit,
) {
    Column(modifier) {
        if (pinnedKlips.isEmpty()) {
            EmptyState(Strings.pinnedItems)
        } else {
            LazyVerticalGrid(
                horizontalArrangement = spacedBy(4.dp),
                verticalArrangement = spacedBy(4.dp),
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(4.dp),
                content = {
                    items(pinnedKlips) { klip ->
                        PinnedCard(
                            item = klip,
                            onCopyClick = onCopyClick,
                            onPinClick = onPinClick,
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun HistoryColumn(
    modifier: Modifier = Modifier,
    historyKlips: List<HistoryKlip>,
    onClick: (HistoryKlip) -> Unit
) {
    Column(modifier) {
        if (historyKlips.isEmpty()) {
            EmptyState(Strings.historyItems)
        } else {
            LazyColumn {
                items(historyKlips) { klip ->
                    Text(
                        modifier = Modifier.clickable { onClick(klip) }.fillMaxWidth(),
                        text = klip.text,
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.body1,
                    )
                }
            }
        }
    }
}

@Composable
private fun PinnedCard(
    item: Klip,
    onCopyClick: (Klip) -> Unit,
    onPinClick: (Klip) -> Unit,
) {
    Card(
        backgroundColor = Color.Gray
    ) {
        Column(
            modifier = Modifier.size(100.dp).padding(8.dp)
                .scrollable(state = rememberScrollState(), orientation = Vertical),
            horizontalAlignment = CenterHorizontally
        ) {
            Row(verticalAlignment = CenterVertically) {
                item.title?.let { title ->
                    Text(
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        text = title,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.body1
                    )
                } ?: Spacer(Modifier.weight(1f))

                Icon(
                    modifier = Modifier.size(15.dp).clickable {
                        onPinClick(item)
                    },
                    imageVector = Icons.Default.PushPin,
                    contentDescription = null,
                )

                Spacer(Modifier.width(8.dp))

                Icon(
                    modifier = Modifier.size(15.dp).clickable {
                        onCopyClick(item)
                    },
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.itemText,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
private fun EmptyState(type: String) {
    Text(text = Strings.emptyStateString(type), style = MaterialTheme.typography.body2)
}