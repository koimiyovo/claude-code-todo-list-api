package com.kyovo.todo.adapter.output.persistence

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
)
