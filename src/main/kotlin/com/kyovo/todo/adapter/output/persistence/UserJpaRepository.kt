package com.kyovo.todo.adapter.output.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun findByUsername(username: String): Optional<UserEntity>
}
