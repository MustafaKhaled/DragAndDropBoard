package com.go.ddboard.ui.compose

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.concurrent.Flow

@Composable
fun DragAndDropCompose() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        DragBox(modifier = Modifier.weight(1f))
        VerticalDivider()
        DropBox(modifier = Modifier.weight(1f))
        VerticalDivider()
        DropBox(modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun DropBox(modifier: Modifier) {
    val targetPhotoUrls = remember { mutableStateListOf<String>() }
    var backgroundColor by remember { mutableStateOf(Color(0xffE5E4E2)) }
    val vertScrollState = remember {ScrollState(0)}
    val dragAndDropTarget = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val data = event.toAndroidDragEvent().clipData.getItemAt(0).text
                targetPhotoUrls.add(data.toString())
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
        FlowRow(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(vertScrollState)) {
            targetPhotoUrls.forEach { url ->
                if (targetPhotoUrls.isNotEmpty()) {
                    Photo(url)
                }
            }

//            LazyVerticalGrid(
//                columns = GridCells.Adaptive(minSize = 20.dp),
//                verticalArrangement = Arrangement.spacedBy(10.dp),
//                horizontalArrangement = Arrangement.spacedBy(10.dp),
//                modifier = Modifier.fillMaxSize() // Ensure grid takes up space
//            ) {
//                // Render items in the grid
//                if (targetPhotoUrls.isNotEmpty()) {
//                    items(targetPhotoUrls.size) { index ->
//                        Photo(targetPhotoUrls[index])
//                    }
//                }
//            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun DragBox(modifier: Modifier) {
    val targetPhotoUrls = remember {
        mutableStateListOf<String>()
    }.apply {
        add("https10.jpeg")
        add("https://serg")
        add("https://services.go")
    }
    val dragAndDropTarget = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val data = event.toAndroidDragEvent().clipData.getItemAt(0).text
                targetPhotoUrls.add(data.toString())
                return true
            }

            override fun onStarted(event: DragAndDropEvent) {
                super.onStarted(event)
                Log.d("DragBox", "onStarted: ${event.toAndroidDragEvent().clipDescription}")
            }

            override fun onEntered(event: DragAndDropEvent) {
                super.onEntered(event)
//                backgroundColor = Color(0xffD3D3D3)
            }

            override fun onEnded(event: DragAndDropEvent) {
                super.onExited(event)
//                backgroundColor = Color(0xffE5E4E2)
            }

            override fun onExited(event: DragAndDropEvent) {
                super.onExited(event)
//                backgroundColor = Color(0xffE5E4E2)
            }
        }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    event
                        .mimeTypes()
                        .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                },
                target = dragAndDropTarget,
            ),
    ) {
//        LazyVerticalGrid(
//            columns = GridCells.Adaptive(minSize = 100.dp),
//            verticalArrangement = Arrangement.spacedBy(10.dp),
//            horizontalArrangement = Arrangement.spacedBy(10.dp),
//        ) {
//            items(targetPhotoUrls.size) { index ->
//                Photo(targetPhotoUrls[index])
//            }
//
//        }
        FlowRow {
            LazyHorizontalGrid(
                rows = GridCells.Adaptive(minSize = 100.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(targetPhotoUrls.size) { index ->
                    Photo(targetPhotoUrls[index])
                }

            }
        }
    }
}

@Composable
fun BoardTemplate() {

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Photo(urlStr: String) {
    val url by remember {
        mutableStateOf(urlStr)
    }
    Card(
        modifier = Modifier
            .dragAndDropSource(block = {
                detectTapGestures(
                    onLongPress = {
                        startTransfer(
                            DragAndDropTransferData(
                                ClipData(
                                    ClipDescription("photo", arrayOf(MIMETYPE_TEXT_PLAIN)),
                                    ClipData.Item(url)
                                )
                            )
                        )
                    })
            }),
        shape = CardDefaults.outlinedShape
    ) {
        Text(text = urlStr, modifier = Modifier.padding(all = 4.dp))
    }

}

@Preview
@Composable
fun DragAndDropComposePreview() {
    DragAndDropCompose()
}
