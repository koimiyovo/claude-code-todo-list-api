package com.kyovo.todo.adapter.output.persistence

import com.kyovo.todo.domain.model.User
import com.kyovo.todo.domain.port.output.UserRepositoryPort
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(
    private val jpaRepository: UserJpaRepository
) : UserRepositoryPort {

    override fun findByUsername(username: String): User? =
        jpaRepository.findByUsername(username).orElse(null)?.toDomain()

    override fun save(user: User): User =
        jpaRepository.save(user.toEntity()).toDomain()

    private fun User.toEntity() = UserEntity(id = id, username = username, password = password, role = role)

    private fun UserEntity.toDomain() = User(id = id, username = username, password = password, role = role)
}
