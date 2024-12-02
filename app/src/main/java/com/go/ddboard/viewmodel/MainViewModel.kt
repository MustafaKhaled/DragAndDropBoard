package com.go.ddboard.viewmodel

import androidx.lifecycle.ViewModel
import com.go.ddboard.data.BoardTicket
import com.go.ddboard.data.Column
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {


    private val todos = MutableStateFlow<List<BoardTicket>>(emptyList())
    private val inProgress = MutableStateFlow<List<BoardTicket>>(emptyList())
    private val done = MutableStateFlow<List<BoardTicket>>(emptyList())

    private val _uiState = MutableStateFlow<UiState>(
        UiState.Success(
            SuccessState(
                todos.value,
                inProgress.value,
                done.value
            )
        )
    )
    val uiState = _uiState.asStateFlow()


    fun move(boardTicket: BoardTicket, target: Column) {
        when (boardTicket.column) {
            Column.DONE -> done.value -= boardTicket
            Column.IN_PROGRESS -> inProgress.value -= boardTicket
            Column.TODO -> todos.value -= boardTicket
        }
        when (target) {
            Column.DONE -> done.value += boardTicket.copy(column = target)
            Column.IN_PROGRESS -> inProgress.value += boardTicket.copy(column = target)
            Column.TODO -> todos.value += boardTicket.copy(column = target)
        }
        updateUiState()
    }

    fun add(boardTicket: BoardTicket) {
        todos.value += BoardTicket(text = boardTicket.text, estimation = boardTicket.estimation, tag = boardTicket.tag, column =  Column.TODO)
        updateUiState()
    }

    fun delete(boardTicket: BoardTicket) {
        done.value -= boardTicket
        updateUiState()
    }

    private fun updateUiState() {
        _uiState.value =
            UiState.Success(SuccessState(todos.value, inProgress.value, done.value))
    }




    data class SuccessState(
        val listOne: List<BoardTicket>,
        val listTwo: List<BoardTicket>,
        val listThree: List<BoardTicket>,
        val estimations: List<String> = listOf("1", "2", "3", "5", "8", "13", "21"),
        val tags: List<String> = listOf("Bug", "Task", "Improvement")
    )

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val list: SuccessState) : UiState()

    }
}