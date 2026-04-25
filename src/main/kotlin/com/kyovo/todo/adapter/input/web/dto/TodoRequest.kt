package com.kyovo.todo.adapter.input.web.dto

data class CreateTodoRequest(
    val title: String,
    val description: String? = null
)

data class UpdateTodoRequest(
    val title: String,
    val description: String? = null,
    val completed: Boolean = false
)
