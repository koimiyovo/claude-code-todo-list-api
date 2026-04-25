package com.kyovo.todo.domain.model

import java.time.LocalDateTime

data class Todo(
    val id: TodoId? = null,
    val title: Title,
    val description: Description? = null,
    val completed: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
