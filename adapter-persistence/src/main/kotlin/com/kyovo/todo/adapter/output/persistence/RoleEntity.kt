package com.kyovo.todo.adapter.output.persistence

import com.kyovo.todo.domain.model.Role

enum class RoleEntity(val toDomain: Role) {
    USER(Role.USER),
    ADMIN(Role.ADMIN);

    companion object {
        fun from(role: Role): RoleEntity {
            return when (role) {
                Role.USER -> USER
                Role.ADMIN -> ADMIN
            }
        }
    }
}