package com.kyovo.todo.adapter.output.persistence

import com.kyovo.todo.domain.model.NewTodo
import com.kyovo.todo.domain.model.Todo
import com.kyovo.todo.domain.model.TodoId
import com.kyovo.todo.domain.port.output.TodoRepositoryPort
import org.springframework.stereotype.Component

@Component
class TodoPersistenceAdapter(
    private val jpaRepository: TodoJpaRepository
) : TodoRepositoryPort {

    override fun create(todo: NewTodo): Todo {
        val savedTodo = TodoEntity.from(todo)
        return jpaRepository.save(savedTodo).toDomain()
    }

    override fun update(todo: Todo): Todo {
        val updatedTodo = jpaRepository.save(TodoEntity.from(todo))
        return updatedTodo.toDomain()
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
}
