package com.kyovo.todo.adapter.output.persistence

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "todos")
class TodoEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID?,

    @Column(nullable = false)
    var title: String,

    @Column
    var description: String?,

    @Column(nullable = false)
    var completed: Boolean,

    @Column(nullable = false)
    val createdAt: LocalDateTime
)
