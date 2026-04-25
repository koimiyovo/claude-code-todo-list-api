package com.kyovo.todo.domain.port.output

import com.kyovo.todo.domain.model.Todo

interface TodoRepositoryPort {
    fun save(todo: Todo): Todo
    fun findById(id: Long): Todo?
    fun findAll(): List<Todo>
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
}
