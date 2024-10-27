package com.go.ddboard

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.go.ddboard.viewmodel.MainViewModel
import com.go.ddboard.ui.compose.DragAndDropCompose
import com.go.ddboard.ui.theme.DDBoardTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContent {
            DDBoardTheme {
                val uiState = viewModel.uiState.collectAsState(MainViewModel.UiState.Loading)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DragAndDropCompose(
                        uiState = uiState.value,
                        modifier = Modifier.padding(paddingValues = innerPadding),
                        onTicketDropped = { boardTicket, type ->
                            viewModel.move(boardTicket = boardTicket, to = type)
                        },
                        onNewTicketSubmitted = { viewModel.add(it) },
                        onDeleteConfirmed = { viewModel.delete(it) }
                        )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DragAndDropPreview() {
    DDBoardTheme {
        DragAndDropCompose(uiState = MainViewModel.UiState.Loading, modifier = Modifier, onNewTicketSubmitted = {}, onDeleteConfirmed = {}) { _, _ ->  }
    }
}