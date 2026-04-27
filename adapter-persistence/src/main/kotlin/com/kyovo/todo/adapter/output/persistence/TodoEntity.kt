package com.kyovo.todo.adapter.output.persistence

import com.kyovo.todo.domain.model.*
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "todos")
class TodoEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID?,

    @Column(nullable = false)
    val title: String,

    @Column
    val description: String?,

    @Column(nullable = false)
    val completed: Boolean,

    @Column(nullable = false)
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(todo: NewTodo): TodoEntity {
            return TodoEntity(
                id = null,
                title = todo.title.value,
                description = todo.description?.value,
                completed = todo.completed,
                createdAt = todo.createdAt
            )
        }

        fun from(todo: Todo): TodoEntity {
            return TodoEntity(
                id = todo.id.value,
                title = todo.title.value,
                description = todo.description?.value,
                completed = todo.completed,
                createdAt = todo.createdAt
            )
        }
    }

    fun toDomain(): Todo {
        return Todo(
            id = TodoId(id!!),
            title = Title(title),
            description = description?.let { Description(it) },
            completed = completed,
            createdAt = createdAt
        )
    }
}
