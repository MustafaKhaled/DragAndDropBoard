package com.go.ddboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.go.ddboard.data.Column
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {


    private val _listOne = MutableStateFlow<List<BoardTicket>>(emptyList())
    private val _listTwo = MutableStateFlow<List<BoardTicket>>(emptyList())
    private val _listThree = MutableStateFlow<List<BoardTicket>>(emptyList())

    private val _uiState = MutableStateFlow<UiState>(
        UiState.Success(
            SuccessState(
                _listOne.value,
                _listTwo.value,
                _listThree.value
            )
        )
    )
    val uiState = _uiState.asStateFlow()


    fun move(boardTicket: BoardTicket, to: Column) {
        when (boardTicket.column) {
            Column.DONE -> _listThree.value -= boardTicket
            Column.IN_PROGRESS -> _listTwo.value -= boardTicket
            Column.TODO -> _listOne.value -= boardTicket
        }
        when (to) {
            Column.DONE -> _listThree.value += boardTicket.copy(column = to)
            Column.IN_PROGRESS -> _listTwo.value += boardTicket.copy(column = to)
            Column.TODO -> _listOne.value += boardTicket.copy(column = to)
        }
        _uiState.value =
            UiState.Success(SuccessState(_listOne.value, _listTwo.value, _listThree.value))
    }

    fun add(boardTicket: BoardTicket) {
        _listOne.value += BoardTicket(text = boardTicket.text, estimation = boardTicket.estimation, tag = boardTicket.tag, column =  Column.TODO)
        updateUiState()
    }

    fun delete(boardTicket: BoardTicket) {
        _listThree.value -= boardTicket
        updateUiState()
    }

    private fun updateUiState() {
        _uiState.value =
            UiState.Success(SuccessState(_listOne.value, _listTwo.value, _listThree.value))
    }

    data class BoardTicket(
        val text: String,
        val estimation: String? = null,
        val tag: String? = null,
        val column: Column
    )


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