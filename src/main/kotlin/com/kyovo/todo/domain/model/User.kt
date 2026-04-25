package com.kyovo.todo.domain.model

data class User(
    val id: Long? = null,
    val username: String,
    val password: String,
    val role: String = "USER"
)
