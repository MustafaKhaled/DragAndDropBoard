package com.go.ddboard.ui.compose

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.go.ddboard.data.BadgeType

fun Modifier.adjustedSize(badgeType: BadgeType): Modifier {
    return when (badgeType) {
        BadgeType.ESTIMATION -> size(40.dp).wrapContentHeight()
        BadgeType.TAG -> wrapContentSize()
    }
}