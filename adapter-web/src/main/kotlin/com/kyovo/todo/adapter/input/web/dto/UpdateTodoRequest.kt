package com.kyovo.todo.adapter.input.web.dto

data class UpdateTodoRequest(
    val title: String,
    val description: String?,
    val completed: Boolean
)