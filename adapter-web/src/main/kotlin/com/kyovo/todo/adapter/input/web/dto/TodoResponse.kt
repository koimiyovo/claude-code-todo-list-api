package com.kyovo.todo.adapter.input.web.dto

import com.kyovo.todo.domain.model.Todo
import java.time.LocalDateTime
import java.util.*

data class TodoResponse(
    val id: UUID,
    val title: String,
    val description: String?,
    val completed: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(todo: Todo): TodoResponse {
            return TodoResponse(
                id = todo.id!!.value,
                title = todo.title.value,
                description = todo.description?.value,
                completed = todo.completed,
                createdAt = todo.createdAt
            )
        }
    }
}
