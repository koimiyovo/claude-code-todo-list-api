package com.kyovo.todo.adapter.output.persistence

import com.kyovo.todo.domain.model.NewUser
import com.kyovo.todo.domain.model.User
import com.kyovo.todo.domain.model.Username
import com.kyovo.todo.domain.port.output.UserRepositoryPort
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(
    private val jpaRepository: UserJpaRepository
) : UserRepositoryPort {

    override fun findByUsername(username: Username): User? {
        return jpaRepository.findByUsername(username).orElse(null)?.toDomain()
    }

    override fun save(user: NewUser): User {
        val savedUser = jpaRepository.save(UserEntity.from(user))
        return savedUser.toDomain()
    }
}
