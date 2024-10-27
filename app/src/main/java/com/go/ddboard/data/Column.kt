package com.go.ddboard.data

sealed class Column(val name: String) {
    data object TODO : Column("TODO")
    data object IN_PROGRESS : Column("In Progress")

    data object DONE : Column("Done")

    fun String.toType() =
        when (this) {
            "TODO" -> TODO
            "IN_PROGRESS" -> IN_PROGRESS
            "DONE" -> DONE
            else -> throw IllegalArgumentException("Invalid type: $this")
        }

}

enum class BadgeType {
    ESTIMATION, TAG
}