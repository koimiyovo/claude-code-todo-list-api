package com.kyovo.todo.domain.service

import com.kyovo.todo.domain.model.Todo
import com.kyovo.todo.domain.annotation.DomainService
import com.kyovo.todo.domain.port.input.TodoUseCase
import com.kyovo.todo.domain.port.output.TodoRepositoryPort

@DomainService
class TodoService(
    private val repository: TodoRepositoryPort
) : TodoUseCase {

    override fun createTodo(title: String, description: String?): Todo =
        repository.save(Todo(title = title, description = description))

    override fun getTodoById(id: Long): Todo =
        repository.findById(id) ?: throw NoSuchElementException("Todo $id introuvable")

    override fun getAllTodos(): List<Todo> = repository.findAll()

    override fun updateTodo(id: Long, title: String, description: String?, completed: Boolean): Todo {
        val existing = getTodoById(id)
        return repository.save(existing.copy(title = title, description = description, completed = completed))
    }

    override fun deleteTodo(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("Todo $id introuvable")
        repository.deleteById(id)
    }
}
