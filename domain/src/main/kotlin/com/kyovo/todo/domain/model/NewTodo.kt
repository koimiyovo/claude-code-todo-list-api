package com.kyovo.todo.domain.model

import java.time.LocalDateTime

data class NewTodo(
    val title: Title,
    val description: Description?,
    val completed: Boolean,
    val createdAt: LocalDateTime
)
