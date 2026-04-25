package com.kyovo.todo.adapter.output.persistence

import com.kyovo.todo.domain.model.Password
import com.kyovo.todo.domain.model.User
import com.kyovo.todo.domain.model.UserId
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

    override fun save(user: User): User {
        return jpaRepository.save(user.toEntity()).toDomain()
    }

    private fun User.toEntity(): UserEntity {
        return UserEntity(
            id = id?.value,
            username = username.value,
            password = password.value,
            role = RoleEntity.from(role)
        )
    }

    private fun UserEntity.toDomain(): User {
        return User(
            id = id?.let { UserId(it) },
            username = Username(username),
            password = Password(password),
            role = role.toDomain
        )
    }
}
