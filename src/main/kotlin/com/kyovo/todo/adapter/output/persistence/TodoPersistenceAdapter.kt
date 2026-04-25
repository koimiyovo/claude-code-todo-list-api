package com.kyovo.todo.adapter.output.persistence

import com.kyovo.todo.domain.model.Todo
import com.kyovo.todo.domain.port.output.TodoRepositoryPort
import org.springframework.stereotype.Component

@Component
class TodoPersistenceAdapter(
    private val jpaRepository: TodoJpaRepository
) : TodoRepositoryPort {

    override fun save(todo: Todo): Todo =
        jpaRepository.save(todo.toEntity()).toDomain()

    override fun findById(id: Long): Todo? =
        jpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findAll(): List<Todo> =
        jpaRepository.findAll().map { it.toDomain() }

    override fun deleteById(id: Long) =
        jpaRepository.deleteById(id)

    override fun existsById(id: Long): Boolean =
        jpaRepository.existsById(id)

    private fun Todo.toEntity() = TodoEntity(
        id = id,
        title = title,
        description = description,
        completed = completed,
        createdAt = createdAt
    )

    private fun TodoEntity.toDomain() = Todo(
        id = id,
        title = title,
        description = description,
        completed = completed,
        createdAt = createdAt
    )
}
