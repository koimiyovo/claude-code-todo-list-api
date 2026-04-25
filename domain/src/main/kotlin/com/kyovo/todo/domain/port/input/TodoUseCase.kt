package com.kyovo.todo.domain.port.input

import com.kyovo.todo.domain.model.Description
import com.kyovo.todo.domain.model.Title
import com.kyovo.todo.domain.model.Todo
import com.kyovo.todo.domain.model.TodoId

interface TodoUseCase {
    fun createTodo(title: Title, description: Description?): Todo
    fun getTodoById(id: TodoId): Todo
    fun getAllTodos(): List<Todo>
    fun updateTodo(id: TodoId, title: Title, description: Description?, completed: Boolean): Todo
    fun deleteTodo(id: TodoId)
}
