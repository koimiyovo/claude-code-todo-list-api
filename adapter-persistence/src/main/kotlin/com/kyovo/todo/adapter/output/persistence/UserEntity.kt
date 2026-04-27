package com.kyovo.todo.adapter.output.persistence

import com.kyovo.todo.domain.model.*
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users")
class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID?,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false)
    val password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: RoleEntity
) {
    companion object {
        fun from(user: NewUser): UserEntity {
            return UserEntity(
                id = null,
                username = user.username.value,
                password = user.password.value,
                role = RoleEntity.from(user.role)
            )
        }

        fun from(user: User): UserEntity {
            return UserEntity(
                id = user.id.value,
                username = user.username.value,
                password = user.password.value,
                role = RoleEntity.from(user.role)
            )
        }
    }

    fun toDomain() = User(
        id = UserId(id!!),
        username = Username(username),
        password = Password(password),
        role = role.toDomain
    )
}
