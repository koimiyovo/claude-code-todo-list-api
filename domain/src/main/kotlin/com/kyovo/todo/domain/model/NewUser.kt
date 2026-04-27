package com.kyovo.todo.domain.model

data class NewUser(
    val username: Username,
    val password: Password,
    val role: Role
)
