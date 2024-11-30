package com.go.ddboard.data

enum class Column(val columnName: String) {
    TODO("TODO"),
    IN_PROGRESS("In Progress"),
    DONE("Done")
}

enum class BadgeType {
    ESTIMATION, TAG
}