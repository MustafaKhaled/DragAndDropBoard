package com.go.ddboard.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.go.ddboard.data.Type
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

class MainViewModel : ViewModel() {


    private val _listOne = MutableStateFlow<List<BoardTicket>>(listOf(BoardTicket("test ticket", Type.TODO)))
    private val _listTwo = MutableStateFlow<List<BoardTicket>>(emptyList())
    private val _listThree = MutableStateFlow<List<BoardTicket>>(emptyList())

    private val _uiState = MutableStateFlow<UiState>(UiState.Success(SuccessState(_listOne.value, _listTwo.value, _listThree.value)))
    val uiState = _uiState.asStateFlow()


    fun move(boardTicket: BoardTicket, to: Type) {
        when (boardTicket.type) {
            Type.DONE -> _listThree.value -= boardTicket
            Type.IN_PROGRESS -> _listTwo.value -= boardTicket
            Type.TODO -> _listOne.value -= boardTicket
        }
        when (to) {
            Type.DONE -> _listThree.value += boardTicket.copy(type = to)
            Type.IN_PROGRESS -> _listTwo.value += boardTicket.copy(type = to)
            Type.TODO -> _listOne.value += boardTicket.copy(type = to)
        }
        _uiState.value =
            UiState.Success(SuccessState(_listOne.value, _listTwo.value, _listThree.value))
    }

    data class BoardTicket(
        val text: String,
        val type: Type
    )


    data class SuccessState(
        val listOne: List<BoardTicket>,
        val listTwo: List<BoardTicket>,
        val listThree: List<BoardTicket>
    )

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val list: SuccessState) : UiState()

    }
}