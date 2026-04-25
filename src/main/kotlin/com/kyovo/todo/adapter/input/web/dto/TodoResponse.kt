package com.kyovo.todo.adapter.input.web.dto

import com.kyovo.todo.domain.model.Todo
import java.time.LocalDateTime

data class TodoResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val completed: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(todo: Todo) = TodoResponse(
            id = todo.id!!,
            title = todo.title,
            description = todo.description,
            completed = todo.completed,
            createdAt = todo.createdAt
        )
    }
}
