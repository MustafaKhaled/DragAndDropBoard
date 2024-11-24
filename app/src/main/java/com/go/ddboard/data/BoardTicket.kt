package com.go.ddboard.data

data class BoardTicket(
    val text: String,
    val estimation: String? = null,
    val tag: String? = null,
    val column: Column
)