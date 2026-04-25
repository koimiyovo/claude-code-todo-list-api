package com.kyovo.todo.domain.port.output

import com.kyovo.todo.domain.model.User

interface UserRepositoryPort {
    fun findByUsername(username: String): User?
    fun save(user: User): User
}
