package com.go.ddboard.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.go.ddboard.data.BadgeType
import com.go.ddboard.data.BoardTicket
import com.go.ddboard.data.Column
import com.go.ddboard.viewmodel.MainViewModel
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputDialog(
    showDialog: Boolean,
    estimationsList: List<String>,
    tagsList: List<String>,
    onNewTicketSubmitted: (BoardTicket) -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var text by remember(sheetState.isVisible) { mutableStateOf("") }
    val selectedEstimationIndex = remember(sheetState.isVisible) { mutableIntStateOf(-1) }
    val textError = remember(sheetState.isVisible)  { mutableStateOf(false) }
    val selectedTagIndex = remember(sheetState.isVisible) { mutableIntStateOf(-1) }
    var supportTextColor by remember { mutableStateOf(colorScheme.onSurface) }
    var estimationError by remember(sheetState.isVisible)  { mutableStateOf(false) }
    var tagError by remember(sheetState.isVisible)  { mutableStateOf(false) }
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
                    badgeType = BadgeType.ESTIMATION,
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
                    badgeType = BadgeType.TAG,
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
                        BoardTicket(
                            text = text,
                            estimation = estimationsList[selectedEstimationIndex.intValue],
                            tag = tagsList[selectedTagIndex.intValue],
                            column = Column.TODO
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

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun SingleSelectionSection(
    title: String,
    list: List<String>,
    selectedIndex: Int,
    badgeType: BadgeType,
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
                    badgeType = badgeType,
                    onClick = {
                        onItemSelected(index)
                    },
                    hasError = hasError
                )
            }
        }
    }
}