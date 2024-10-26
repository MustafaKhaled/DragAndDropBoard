package com.go.ddboard.ui.compose

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.go.ddboard.ui.MainViewModel

fun Modifier.adjustedSize(cardType: MainViewModel.CardType): Modifier {
    return when (cardType) {
        MainViewModel.CardType.ESTIMATION -> size(40.dp).wrapContentHeight()
        MainViewModel.CardType.TAG -> wrapContentSize()
    }
}