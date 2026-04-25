package com.kyovo.todo.domain.model

data class User(
    val id: UserId? = null,
    val username: Username,
    val password: Password,
    val role: Role = Role.USER
)
