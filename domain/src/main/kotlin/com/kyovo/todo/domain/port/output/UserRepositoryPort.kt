package com.kyovo.todo.domain.port.output

import com.kyovo.todo.domain.model.User
import com.kyovo.todo.domain.model.Username

interface UserRepositoryPort {
    fun findByUsername(username: Username): User?
    fun save(user: User): User
}
