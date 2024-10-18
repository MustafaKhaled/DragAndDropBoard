package com.go.ddboard.data

sealed class Type(val name: String) {
    data object TODO : Type("TODO")
    data object IN_PROGRESS : Type("In Progress")

    data object DONE : Type("Done")

    fun String.toType() =
        when (this) {
            "TODO" -> TODO
            "IN_PROGRESS" -> IN_PROGRESS
            "DONE" -> DONE
            else -> throw IllegalArgumentException("Invalid type: $this")
        }

}