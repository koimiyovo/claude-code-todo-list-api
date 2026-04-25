package com.kyovo.todo.adapter.input.web.dto

data class CreateTodoRequest(
    val title: String,
    val description: String? = null
)