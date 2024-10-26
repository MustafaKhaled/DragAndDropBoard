package com.go.ddboard.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.go.ddboard.data.Type
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {


    private val _listOne = MutableStateFlow<List<BoardTicket>>(emptyList())
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

    fun add(boardTicket: BoardTicket) {
        _listOne.value += BoardTicket(text = boardTicket.text, estimation = boardTicket.estimation, tag = boardTicket.tag, type =  Type.TODO)
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
        val type: Type
    )


    data class SuccessState(
        val listOne: List<BoardTicket>,
        val listTwo: List<BoardTicket>,
        val listThree: List<BoardTicket>,
        val estimations: List<String> = listOf("1", "2", "3", "5", "8", "13", "21"),
        val tags: List<String> = listOf("Bug", "Task", "Improvement")
    )

    enum class CardType {
        ESTIMATION, TAG
    }

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val list: SuccessState) : UiState()

    }
}