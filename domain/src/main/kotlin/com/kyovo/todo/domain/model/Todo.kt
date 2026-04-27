package com.kyovo.todo.domain.model

import java.time.LocalDateTime

data class Todo(
    val id: TodoId?,
    val title: Title,
    val description: Description?,
    val completed: Boolean,
    val createdAt: LocalDateTime
)
