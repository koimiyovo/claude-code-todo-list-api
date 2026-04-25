package com.kyovo.todo.domain.port.input

interface AuthUseCase {
    fun login(username: String, password: String): String
    fun logout(token: String)
}
