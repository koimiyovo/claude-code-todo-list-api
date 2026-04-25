package com.kyovo.todo.adapter.output.persistence

import com.kyovo.todo.domain.model.Description
import com.kyovo.todo.domain.model.Title
import com.kyovo.todo.domain.model.Todo
import com.kyovo.todo.domain.model.TodoId
import com.kyovo.todo.domain.port.output.TodoRepositoryPort
import org.springframework.stereotype.Component

@Component
class TodoPersistenceAdapter(
    private val jpaRepository: TodoJpaRepository
) : TodoRepositoryPort {

    override fun save(todo: Todo): Todo {
        return jpaRepository.save(todo.toEntity()).toDomain()
    }

    override fun findById(id: TodoId): Todo? {
        return jpaRepository.findById(id.value).orElse(null)?.toDomain()
    }

    override fun findAll(): List<Todo> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun deleteById(id: TodoId) {
        jpaRepository.deleteById(id.value)
    }

    override fun existsById(id: TodoId): Boolean {
        return jpaRepository.existsById(id.value)
    }

    private fun Todo.toEntity(): TodoEntity {
        return TodoEntity(
            id = id?.value,
            title = title.value,
            description = description?.value,
            completed = completed,
            createdAt = createdAt
        )
    }

    private fun TodoEntity.toDomain() = Todo(
        id = id?.let { TodoId(it) },
        title = Title(title),
        description = description?.let { Description(it) },
        completed = completed,
        createdAt = createdAt
    )
}
