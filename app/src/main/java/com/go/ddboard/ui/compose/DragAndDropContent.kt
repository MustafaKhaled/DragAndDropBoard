package com.go.ddboard.ui.compose

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.go.ddboard.R
import com.go.ddboard.data.BadgeType
import com.go.ddboard.data.BoardTicket
import com.go.ddboard.data.Column
import com.go.ddboard.ui.compose.Keys.ARG_TICKET
import com.go.ddboard.viewmodel.MainViewModel.UiState
import com.google.gson.Gson

private val gson by lazy { Gson() }

@Composable
fun DragAndDropCompose(
    uiState: UiState,
    modifier: Modifier,
    onNewTicketSubmitted: (BoardTicket) -> Unit,
    onDeleteConfirmed: (BoardTicket) -> Unit,
    onTicketDropped: (BoardTicket, Column) -> Unit
) {
    when (uiState) {
        is
        UiState.Success -> {
            var showAddTicketDialog by remember { mutableStateOf(false) }
            Box(modifier = modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)

                ) {

                    DragAndDropBox(
                        modifier = Modifier.weight(1f),
                        list = uiState.list.listOne,
                        column = Column.TODO,
                        onTicketDropped = onTicketDropped,
                    )
                    VerticalDivider()
                    DragAndDropBox(
                        modifier = Modifier.weight(1f),
                        list = uiState.list.listTwo,
                        column = Column.IN_PROGRESS,
                        onTicketDropped = onTicketDropped,
                    )
                    VerticalDivider()
                    DragAndDropBox(
                        modifier = Modifier
                            .weight(1f),
                        list = uiState.list.listThree,
                        column = Column.DONE,
                        onTicketDropped = onTicketDropped,
                        onDeleteConfirmed = onDeleteConfirmed
                    )
                }


                FloatingActionButton(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomEnd),
                    onClick = {
                        showAddTicketDialog = true
                    }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }


            InputDialog(
                showDialog = showAddTicketDialog,
                estimationsList = uiState.list.estimations,
                tagsList = uiState.list.tags,
                onNewTicketSubmitted = {
                    onNewTicketSubmitted(it)
                },
                onDismiss = { showAddTicketDialog = false }
            )
        }

        is
        UiState.Loading -> {


        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragAndDropBox(
    modifier: Modifier,
    list: List<BoardTicket>,
    column: Column,
    onTicketDropped: (BoardTicket, Column) -> Unit,
    onDeleteConfirmed: (BoardTicket) -> Unit = {},
) {
    var backgroundColor by remember { mutableStateOf(Color(0xffE5E4E2)) }
    val titleStyle = remember { mutableStateOf(FontWeight.Normal) }
    val scale by animateFloatAsState(
        if (titleStyle.value == FontWeight.Bold) 1.4f else 1f,
        label = "scale"
    )
    val dragAndDropTarget = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val data = event.toAndroidDragEvent().clipData.getItemAt(0).intent
                val ticket = gson.fromJson(data.getStringExtra(ARG_TICKET), BoardTicket::class.java)
                onTicketDropped(
                    ticket, column
                )
                return true
            }

            override fun onEntered(event: DragAndDropEvent) {
                super.onEntered(event)
                backgroundColor = Color(0xffD3D3D3)
                titleStyle.value = FontWeight.Bold
            }

            override fun onExited(event: DragAndDropEvent) {
                super.onExited(event)
                backgroundColor = Color(0xffE5E4E2)
                titleStyle.value = FontWeight.Normal
            }


            override fun onEnded(event: DragAndDropEvent) {
                super.onEnded(event)
                backgroundColor = Color(0xffE5E4E2)
                titleStyle.value = FontWeight.Normal
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = backgroundColor)
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    event
                        .mimeTypes()
                        .contains(ClipDescription.MIMETYPE_TEXT_INTENT)
                },
                target = dragAndDropTarget
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {

            stickyHeader {
                Text(
                    text = column.columnName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            transformOrigin = TransformOrigin.Center
                        },
                    textAlign = TextAlign.Center,
                    fontWeight = titleStyle.value,
                )
            }

            items(list) { boardTicket ->
                if (list.isNotEmpty()) {
                    TicketCard(
                        ticket = boardTicket,
                        onDeleteConfirmed = onDeleteConfirmed,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TicketCard(
    ticket:
    BoardTicket,
    onDeleteConfirmed: (
        BoardTicket
    ) -> Unit
) {
    val showDeleteTicketDialog = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .dragAndDropSource(block = {
                detectTapGestures(
                    onLongPress = {
                        startTransfer(
                            DragAndDropTransferData(
                                clipData = ClipData.newIntent("label", Intent().apply {
                                    putExtra(ARG_TICKET, gson.toJson(ticket))
                                })
                            )
                        )
                    })
            }),
        shape = CardDefaults.outlinedShape,
        colors = CardDefaults.cardColors(
            containerColor = when (ticket.column) {
                Column.IN_PROGRESS -> MaterialTheme.colorScheme.onSecondary
                Column.DONE -> MaterialTheme.colorScheme.onTertiary
                Column.TODO -> MaterialTheme.colorScheme.onPrimary
            }
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {

            Row {
                Text(
                    text = ticket.text,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
                if (ticket.column == Column.DONE) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                showDeleteTicketDialog.value = true
                            })
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.story_points_label))
                CardContainer(
                    text = ticket.estimation.toString(),
                    badgeType = BadgeType.ESTIMATION
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(text = stringResource(R.string.tag_label))
                CardContainer(text = ticket.tag.toString(), badgeType = BadgeType.TAG)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

    }

    if (showDeleteTicketDialog.value) {
        DeleteDialog(showDialog = showDeleteTicketDialog.value,
            onDeleteClicked = {
                onDeleteConfirmed(ticket)
                showDeleteTicketDialog.value = false
            },
            onDismiss = {
                showDeleteTicketDialog.value = false
            })
    }
}

@Composable
fun CardContainer(
    text: String,
    isSelected: Boolean? = null,
    badgeType: BadgeType,
    onClick: () -> Unit = {},
    hasError: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .clickable(
                indication = rememberRipple(bounded = false),
                interactionSource = interactionSource
            ) {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor =
            if (hasError)
                Color.Red
            else if (isSelected == true)
                Color.Gray
            else Color.LightGray
        ),
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            modifier = Modifier
                .adjustedSize(badgeType)
                .padding(horizontal = 12.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
    }

}

object Keys {
    const val ARG_TICKET = "ticket"
}

@Preview
@Composable
fun DragAndDropComposePreview() {
    DragAndDropCompose(
        uiState =
        UiState.Loading,
        modifier = Modifier,
        onNewTicketSubmitted = {},
        onTicketDropped = { _, _ -> },
        onDeleteConfirmed = {}
    )
}

@Preview
@Composable
fun TicketCardPreview() {
    TicketCard(
        ticket =
        BoardTicket(
            text = "this is just a test title for preview",
            column = Column.TODO
        ),
        onDeleteConfirmed = {}
    )
}
