package com.kyovo.todo.adapter.output.persistence

import com.kyovo.todo.domain.model.Username
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserJpaRepository : JpaRepository<UserEntity, UUID> {
    fun findByUsername(username: Username): Optional<UserEntity>
}
