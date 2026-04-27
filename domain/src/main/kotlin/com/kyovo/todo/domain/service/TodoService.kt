package com.kyovo.todo.domain.service

import com.kyovo.todo.domain.annotation.DomainService
import com.kyovo.todo.domain.model.*
import com.kyovo.todo.domain.port.input.TodoUseCase
import com.kyovo.todo.domain.port.output.TodoRepositoryPort
import java.time.Clock
import java.time.LocalDateTime

@DomainService
class TodoService(
    private val repository: TodoRepositoryPort,
    private val clock: Clock
) : TodoUseCase {

    override fun createTodo(title: Title, description: Description?): Todo {
        return repository.create(
            NewTodo(
                title = title,
                description = description,
                completed = false,
                createdAt = LocalDateTime.now(clock)
            )
        )
    }

    override fun getTodoById(id: TodoId): Todo {
        return repository.findById(id) ?: throw NoSuchElementException("Todo ${id.value} introuvable")
    }

    override fun getAllTodos(): List<Todo> {
        return repository.findAll()
    }

    override fun updateTodo(id: TodoId, title: Title, description: Description?, completed: Boolean): Todo {
        val existing = getTodoById(id)
        return repository.update(existing.copy(title = title, description = description, completed = completed))
    }

    override fun deleteTodo(id: TodoId) {
        if (!repository.existsById(id)) throw NoSuchElementException("Todo ${id.value} introuvable")
        repository.deleteById(id)
    }
}
