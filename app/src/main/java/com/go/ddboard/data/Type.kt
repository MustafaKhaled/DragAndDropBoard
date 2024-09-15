package com.go.ddboard.data

sealed class Type {
    data object TODO : Type()
    data object IN_PROGRESS : Type()

    data object DONE : Type()

    fun String.toType() =
        when (this) {
            "TODO" -> TODO
            "IN_PROGRESS" -> IN_PROGRESS
            "DONE" -> DONE
            else -> throw IllegalArgumentException("Invalid type: $this")
        }

}