package com.kyovo.todo.domain.model

data class User(
    val id: UserId?,
    val username: Username,
    val password: Password,
    val role: Role
)
