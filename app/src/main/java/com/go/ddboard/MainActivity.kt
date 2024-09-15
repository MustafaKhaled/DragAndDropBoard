package com.go.ddboard

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.go.ddboard.ui.MainViewModel
import com.go.ddboard.ui.compose.DragAndDropCompose
import com.go.ddboard.ui.theme.DDBoardTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//        enableEdgeToEdge()
        setContent {
            DDBoardTheme {
                val todo = viewModel.uiState.collectAsState(MainViewModel.UiState.Loading)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DragAndDropCompose(todo.value, onTicketDropped = { boardTicket, type->
                        viewModel.move(boardTicket,type)
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun DragAndDropPreview() {
    DDBoardTheme {
        DragAndDropCompose(MainViewModel.UiState.Loading) { _, _ -> {} }
    }
}