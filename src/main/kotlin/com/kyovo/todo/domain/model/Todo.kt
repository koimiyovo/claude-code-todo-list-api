package com.kyovo.todo.domain.model

import java.time.LocalDateTime

data class Todo(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val completed: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
