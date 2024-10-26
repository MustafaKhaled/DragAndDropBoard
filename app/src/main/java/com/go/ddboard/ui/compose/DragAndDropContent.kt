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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.go.ddboard.data.Type
import com.go.ddboard.data.Type.IN_PROGRESS.toType
import com.go.ddboard.ui.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DragAndDropCompose(
    uiState: MainViewModel.UiState,
    modifier: Modifier,
    onNewTicketSubmitted: (MainViewModel.BoardTicket) -> Unit,
    onDeleteConfirmed: (MainViewModel.BoardTicket) -> Unit,
    onTicketDropped: (MainViewModel.BoardTicket, Type) -> Unit
) {
    when (uiState) {
        is MainViewModel.UiState.Success -> {

            var showAddTicketDialog by remember { mutableStateOf(false) }

            Box(modifier = modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)

                ) {

                    DropBox(
                        modifier = Modifier.weight(1f),
                        list = uiState.list.listOne,
                        type = Type.TODO,
                        columnTitle = stringResource(id = R.string.todo_column_title),
                        onTicketDropped = onTicketDropped,
                        onDeleteConfirmed = {}
                    )
                    VerticalDivider()
                    DropBox(
                        modifier = Modifier.weight(1f),
                        list = uiState.list.listTwo,
                        type = Type.IN_PROGRESS,
                        columnTitle = stringResource(id = R.string.in_progress_column_title),
                        onTicketDropped = onTicketDropped,
                        onDeleteConfirmed = {}
                    )
                    VerticalDivider()
                    DropBox(
                        modifier = Modifier
                            .weight(1f),
                        list = uiState.list.listThree,
                        type = Type.DONE,
                        columnTitle = stringResource(id = R.string.done_column_title),
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
                    },
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
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

        is MainViewModel.UiState.Loading -> {


        }
    }

}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun DropBox(
    modifier: Modifier,
    list: List<MainViewModel.BoardTicket>,
    type: Type,
    columnTitle: String = "",
    onTicketDropped: (MainViewModel.BoardTicket, Type) -> Unit,
    onDeleteConfirmed: (MainViewModel.BoardTicket) -> Unit,
) {
    var backgroundColor by remember { mutableStateOf(Color(0xffE5E4E2)) }
    val isTitleVisible = remember { mutableStateOf(false) }
    val titleStyle = remember { mutableStateOf(FontWeight.Normal) }
    val scale by animateFloatAsState(if (titleStyle.value == FontWeight.Bold) 1.4f else 1f,
        label = "scale"
    )
    val dragAndDropTarget = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val data = event.toAndroidDragEvent().clipData.getItemAt(0).intent
                onTicketDropped(
                    MainViewModel.BoardTicket(
                        text = data.getStringExtra("text") ?: "",
                        type = data.getStringExtra("type")?.toType() ?: Type.TODO,
                        estimation = data.getStringExtra("estimation"),
                        tag = data.getStringExtra("tag")
                    ), type
                )
                return true
            }

            override fun onEntered(event: DragAndDropEvent) {
                super.onEntered(event)
                backgroundColor = Color(0xffD3D3D3)
//                isTitleVisible.value = true
                titleStyle.value = FontWeight.Bold
            }

            override fun onExited(event: DragAndDropEvent) {
                super.onExited(event)
                backgroundColor = Color(0xffE5E4E2)
//                isTitleVisible.value = false
                titleStyle.value = FontWeight.Normal
            }


            override fun onEnded(event: DragAndDropEvent) {
                super.onEnded(event)
                backgroundColor = Color(0xffE5E4E2)
//                isTitleVisible.value = false
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
                    text = type.name,
                    modifier = Modifier.fillMaxWidth()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            transformOrigin = TransformOrigin.Center
                        }
                    ,
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
        if (isTitleVisible.value)
            Text(
                text = columnTitle,
                modifier = Modifier.align(Alignment.Center),

                )

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TicketCard(
    ticket: MainViewModel.BoardTicket,
    onDeleteConfirmed: (MainViewModel.BoardTicket) -> Unit
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
                                    putExtra("text", ticket.text)
                                    putExtra("type", ticket.type.toString())
                                    putExtra("estimation", ticket.estimation)
                                    putExtra("tag", ticket.tag)
                                })
                            )
                        )
                    })
            }),
        shape = CardDefaults.outlinedShape,
        colors = CardDefaults.cardColors(
            containerColor = when (ticket.type) {
                Type.IN_PROGRESS -> MaterialTheme.colorScheme.onSecondary
                Type.DONE -> MaterialTheme.colorScheme.onTertiary
                Type.TODO -> MaterialTheme.colorScheme.onPrimary
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
                if (ticket.type == Type.DONE) {
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
                Text(text = "Story points: ")
                CardContainer(
                    text = ticket.estimation.toString(),
                    cardType = MainViewModel.CardType.ESTIMATION
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(text = "Tag ")
                CardContainer(text = ticket.tag.toString(), cardType = MainViewModel.CardType.TAG)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputDialog(
    showDialog: Boolean,
    estimationsList: List<String>,
    tagsList: List<String>,
    onNewTicketSubmitted: (MainViewModel.BoardTicket) -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var text by remember(sheetState.isVisible) { mutableStateOf("") }
    val selectedEstimationIndex = remember(sheetState.isVisible) { mutableIntStateOf(-1) }
    val textError = remember { mutableStateOf(false) }
    val selectedTagIndex = remember(sheetState.isVisible) { mutableIntStateOf(-1) }
    var supportTextColor by remember { mutableStateOf(colorScheme.onSurface) }
    var estimationError by remember { mutableStateOf(false) }
    var tagError by remember { mutableStateOf(false) }
    if (showDialog) {

        ModalBottomSheet(
            onDismissRequest = {
                onDismiss()
            },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Text(text = "Add a new Ticket", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = text,
                    placeholder = {
                        Text(text = "Write your ticket title/details")
                    },
                    onValueChange = {
                        text = it
                        supportTextColor = if (text.length > 100) {
                            Color.Red
                        } else {
                            colorScheme.onSurface
                        }
                        if (textError.value && it.isNotEmpty()) {
                            textError.value = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Text(
                            text = "${text.length} / 100",
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth(),
                            color = supportTextColor
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    isError = textError.value
                )
                Spacer(modifier = Modifier.height(8.dp))
                SingleSelectionSection(
                    title = "Estimation",
                    list = estimationsList,
                    cardType = MainViewModel.CardType.ESTIMATION,
                    selectedIndex = selectedEstimationIndex.intValue,
                    onItemSelected = {
                        selectedEstimationIndex.intValue = it
                        estimationError = false
                    },
                    hasError = estimationError
                )
                Spacer(modifier = Modifier.height(8.dp))
                SingleSelectionSection(
                    title = "Tags",
                    list = tagsList,
                    cardType = MainViewModel.CardType.TAG,
                    selectedIndex = selectedTagIndex.intValue,
                    onItemSelected = {
                        selectedTagIndex.intValue = it
                        tagError = false
                    },
                    hasError = tagError
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val hasError =
                        text.isEmpty() || selectedTagIndex.intValue == -1 || selectedEstimationIndex.intValue == -1
                    if (hasError) {
                        textError.value = text.isEmpty()
                        estimationError = selectedEstimationIndex.intValue == -1
                        tagError = selectedTagIndex.intValue == -1
                        return@Button
                    }
                    scope.launch {
                        sheetState.hide()

                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onDismiss()
                        }
                    }
                    onNewTicketSubmitted(
                        MainViewModel.BoardTicket(
                            text = text,
                            estimation = estimationsList[selectedEstimationIndex.intValue],
                            tag = tagsList[selectedTagIndex.intValue],
                            type = Type.TODO
                        )
                    )


                }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Create")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteDialog(showDialog: Boolean, onDeleteClicked: () -> Unit, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    if (showDialog) {
        ModalBottomSheet(
            onDismissRequest = {
                onDismiss()
            },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = "Are you sure to delete this ticket?", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))
                Row {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text(text = "Cancel")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { onDeleteClicked() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(text = "Delete")
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun SingleSelectionSection(
    title: String,
    list: List<String>,
    selectedIndex: Int,
    cardType: MainViewModel.CardType,
    onItemSelected: (Int) -> Unit = {},
    hasError: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, modifier = Modifier.padding(end = 8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            list.forEachIndexed { index, text ->
                CardContainer(
                    text = text,
                    isSelected = selectedIndex == index,
                    cardType = cardType,
                    onClick = {
                        onItemSelected(index)
                    },
                    hasError = hasError
                )
            }
        }
    }
}


@Composable
fun CardContainer(
    text: String,
    isSelected: Boolean? = null,
    cardType: MainViewModel.CardType,
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
                .adjustedSize(cardType)
                .padding(horizontal = 12.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
    }

}

fun Modifier.adjustedSize(cardType: MainViewModel.CardType): Modifier {
    return when (cardType) {
        MainViewModel.CardType.ESTIMATION -> size(40.dp).wrapContentHeight()
        MainViewModel.CardType.TAG -> wrapContentSize()
    }
}

@Preview
@Composable
fun DragAndDropComposePreview() {
    DragAndDropCompose(
        uiState = MainViewModel.UiState.Loading,
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
        ticket = MainViewModel.BoardTicket(
            text = "this is just a test title for preview",
            type = Type.TODO
        ),
        onDeleteConfirmed = {}
    )
}
