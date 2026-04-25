package com.kyovo.todo.domain.port.output

import com.kyovo.todo.domain.model.Todo
import com.kyovo.todo.domain.model.TodoId

interface TodoRepositoryPort {
    fun save(todo: Todo): Todo
    fun findById(id: TodoId): Todo?
    fun findAll(): List<Todo>
    fun deleteById(id: TodoId)
    fun existsById(id: TodoId): Boolean
}
