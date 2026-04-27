package com.kyovo.todo.domain.service

import com.kyovo.todo.domain.annotation.DomainService
import com.kyovo.todo.domain.model.Description
import com.kyovo.todo.domain.model.Title
import com.kyovo.todo.domain.model.Todo
import com.kyovo.todo.domain.model.TodoId
import com.kyovo.todo.domain.port.input.TodoUseCase
import com.kyovo.todo.domain.port.output.TodoRepositoryPort
import java.time.LocalDateTime

@DomainService
class TodoService(
    private val repository: TodoRepositoryPort
) : TodoUseCase {

    override fun createTodo(title: Title, description: Description?): Todo {
        return repository.save(Todo(id = null, title = title, description = description, completed = false, createdAt = LocalDateTime.now()))
    }

    override fun getTodoById(id: TodoId): Todo {
        return repository.findById(id) ?: throw NoSuchElementException("Todo $id introuvable")
    }

    override fun getAllTodos(): List<Todo> {
        return repository.findAll()
    }

    override fun updateTodo(id: TodoId, title: Title, description: Description?, completed: Boolean): Todo {
        val existing = getTodoById(id)
        return repository.save(existing.copy(title = title, description = description, completed = completed))
    }

    override fun deleteTodo(id: TodoId) {
        if (!repository.existsById(id)) throw NoSuchElementException("Todo $id introuvable")
        repository.deleteById(id)
    }
}
