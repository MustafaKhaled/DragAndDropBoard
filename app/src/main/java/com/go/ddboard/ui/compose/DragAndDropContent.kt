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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.go.ddboard.data.Type
import com.go.ddboard.data.Type.IN_PROGRESS.toType
import com.go.ddboard.ui.MainViewModel

@Composable
fun DragAndDropCompose(
    todo: MainViewModel.UiState,
    onTicketDropped: (MainViewModel.BoardTicket, Type) -> Unit
) {
    when (todo) {
        is MainViewModel.UiState.Success -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            ) {
                DropBox(
                    modifier = Modifier.weight(1f),
                    list = todo.list.listOne,
                    type = Type.TODO,
                    onTicketDropped = onTicketDropped
                )
                VerticalDivider()
                DropBox(
                    modifier = Modifier.weight(1f),
                    list = todo.list.listTwo,
                    type = Type.IN_PROGRESS,
                    onTicketDropped = onTicketDropped
                )
                VerticalDivider()
                DropBox(
                    modifier = Modifier.weight(1f),
                    list = todo.list.listThree,
                    type = Type.DONE,
                    onTicketDropped = onTicketDropped
                )
            }
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
    onTicketDropped: (MainViewModel.BoardTicket, Type) -> Unit
) {
    var backgroundColor by remember { mutableStateOf(Color(0xffE5E4E2)) }
    val vertScrollState = remember { ScrollState(0) }
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
            }

            override fun onExited(event: DragAndDropEvent) {
                super.onExited(event)
                backgroundColor = Color(0xffE5E4E2)
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
                .verticalScroll(vertScrollState)
        ) {
            list.forEach { boardTicket  ->
                if (list.isNotEmpty()) {
                    Photo(boardTicket.text, type)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Photo(text: String, type: Type) {
    Card(
        modifier = Modifier
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
        Text(text = text, modifier = Modifier.padding(all = 4.dp))
    }

}

@Preview(uiMode = 1)
@Composable
fun DragAndDropComposePreview() {
    DragAndDropCompose(todo = MainViewModel.UiState.Loading, onTicketDropped = { _, _ -> })
}
