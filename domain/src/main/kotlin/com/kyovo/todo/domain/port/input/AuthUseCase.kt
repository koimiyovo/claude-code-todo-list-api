package com.kyovo.todo.domain.port.input

import com.kyovo.todo.domain.model.Password
import com.kyovo.todo.domain.model.Token
import com.kyovo.todo.domain.model.Username

interface AuthUseCase {
    fun login(username: Username, password: Password): Token
    fun logout(token: Token)
}
