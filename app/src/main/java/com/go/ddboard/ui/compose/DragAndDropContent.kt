package com.go.ddboard.ui.compose

import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import com.go.ddboard.R
import com.go.ddboard.data.Type
import com.go.ddboard.data.Type.IN_PROGRESS.toType
import com.go.ddboard.ui.MainViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragAndDropCompose(
    todo: MainViewModel.UiState,
    onNewTicketSubmitted: (String) -> Unit,
    onTicketDropped: (MainViewModel.BoardTicket, Type) -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }
    when (todo) {
        is MainViewModel.UiState.Success -> {
            var icon by remember { mutableStateOf(Icons.Filled.Add) }

            val dragAndDropTarget2 = remember {
                object : DragAndDropTarget {
                    override fun onDrop(event: DragAndDropEvent): Boolean {
                        val data = event.toAndroidDragEvent().clipData.getItemAt(0).text
                        val from = event.toAndroidDragEvent().clipDescription.label.toString()
                        icon = Icons.Filled.Add
                        return true
                    }

                    override fun onEntered(event: DragAndDropEvent) {
                        super.onEntered(event)
                        icon = Icons.Filled.Delete
                    }

                    override fun onMoved(event: DragAndDropEvent) {
                        super.onMoved(event)
                        icon = Icons.Filled.Delete
                    }

                    override fun onExited(event: DragAndDropEvent) {
                        super.onExited(event)
                        icon = Icons.Filled.Add
                    }

                    override fun onEnded(event: DragAndDropEvent) {
                        super.onEnded(event)
                        icon = Icons.Filled.Add
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                // ConstraintLayout for positioning the Row and Button
                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                    val (rowRef, fabRef) = createRefs() // Create references for the Row and Button

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .constrainAs(rowRef) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    ) {

                        DropBox(
                            modifier = Modifier.weight(1f),
                            list = todo.list.listOne,
                            type = Type.TODO,
                            columnTitle = stringResource(id = R.string.todo_column_title),
                            onTicketDropped = onTicketDropped
                        )
                        VerticalDivider()
                        DropBox(
                            modifier = Modifier.weight(1f),
                            list = todo.list.listTwo,
                            type = Type.IN_PROGRESS,
                            columnTitle = stringResource(id = R.string.in_progress_column_title),
                            onTicketDropped = onTicketDropped
                        )
                        VerticalDivider()
                        DropBox(
                            modifier = Modifier
                                .weight(1f),
                            list = todo.list.listThree,
                            type = Type.DONE,
                            columnTitle = stringResource(id = R.string.done_column_title),
                            onTicketDropped = onTicketDropped
                        )
                    }


                    FloatingActionButton(
                        modifier = Modifier
//                            .fillMaxSize()
                            .padding(8.dp)
                            .constrainAs(fabRef) {
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end)
                            }
                            .dragAndDropTarget(
                                shouldStartDragAndDrop = { event ->
                                    event
                                        .mimeTypes()
                                        .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                                },
                                target = dragAndDropTarget2
                            ),
                        onClick = { /* Show dialog or other action */ },
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                    ) {
                        Icon(icon, contentDescription = null)
                    }
                }


                // FloatingActionButton as a smaller drop target


            }


            InputDialog(
                showDialog = showDialog.value,
                onDismiss = { showDialog.value = false },
                onNewTicketSubmitted = onNewTicketSubmitted
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
    onTicketDropped: (MainViewModel.BoardTicket, Type) -> Unit
) {
    var backgroundColor by remember { mutableStateOf(Color(0xffE5E4E2)) }
    val vertScrollState = remember { ScrollState(0) }
    val isTitleVisible = remember { mutableStateOf(false) }
    val dragAndDropTarget = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val data = event.toAndroidDragEvent().clipData.getItemAt(0).text
                val from = event.toAndroidDragEvent().clipDescription.label.toString()
                onTicketDropped(MainViewModel.BoardTicket(data.toString(), from.toType()), type)
                return true
            }

            override fun onEntered(event: DragAndDropEvent) {
                super.onEntered(event)
                backgroundColor = Color(0xffD3D3D3)
                isTitleVisible.value = true
            }

            override fun onExited(event: DragAndDropEvent) {
                super.onExited(event)
                backgroundColor = Color(0xffE5E4E2)
                isTitleVisible.value = false
            }


            override fun onEnded(event: DragAndDropEvent) {
                super.onEnded(event)
                backgroundColor = Color(0xffE5E4E2)
                isTitleVisible.value = false
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
                        .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                },
                target = dragAndDropTarget
            )
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(vertScrollState),
            maxItemsInEachRow = 2
        ) {
            list.forEach { boardTicket ->
                if (list.isNotEmpty()) {
                    Photo(boardTicket.text, type)
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
fun Photo(text: String, type: Type) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .dragAndDropSource(block = {
                detectTapGestures(
                    onLongPress = {
                        startTransfer(
                            DragAndDropTransferData(
                                clipData = ClipData.newPlainText(type.toString(), text)
                            )
                        )
                    })
            }),
        shape = CardDefaults.outlinedShape
    ) {
        Text(text = text, modifier = Modifier.padding(all = 8.dp))
    }

}

@Composable
fun InputDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onNewTicketSubmitted: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var text by remember { mutableStateOf("") }
    var supportTextColor by remember { mutableStateOf(colorScheme.onSurface) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Enter Input") },
            text = {
                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                        supportTextColor = if (text.length > 100) {
                            Color.Red
                        } else {
                            colorScheme.onSurface
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
                    }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onNewTicketSubmitted(text)
                    onDismiss()
                }, enabled = text.length <= 100) {
                    Text(text = "Submit")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }

}

@Composable
fun DragAndDropComposePreview() {
    DragAndDropCompose(
        todo = MainViewModel.UiState.Loading,
        onNewTicketSubmitted = {},
        onTicketDropped = { _, _ -> })
}

