package com.kyovo.todo.domain.port.input

import com.kyovo.todo.domain.model.Todo

interface TodoUseCase {
    fun createTodo(title: String, description: String?): Todo
    fun getTodoById(id: Long): Todo
    fun getAllTodos(): List<Todo>
    fun updateTodo(id: Long, title: String, description: String?, completed: Boolean): Todo
    fun deleteTodo(id: Long)
}
