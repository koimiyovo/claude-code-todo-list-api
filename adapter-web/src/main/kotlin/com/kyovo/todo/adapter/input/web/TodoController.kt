package com.kyovo.todo.adapter.input.web

import com.kyovo.todo.adapter.input.web.dto.CreateTodoRequest
import com.kyovo.todo.adapter.input.web.dto.TodoResponse
import com.kyovo.todo.adapter.input.web.dto.UpdateTodoRequest
import com.kyovo.todo.domain.model.Description
import com.kyovo.todo.domain.model.Title
import com.kyovo.todo.domain.model.TodoId
import com.kyovo.todo.domain.port.input.TodoUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/todos")
class TodoController(private val useCase: TodoUseCase) {

    @PostMapping
    fun create(@RequestBody request: CreateTodoRequest): ResponseEntity<TodoResponse> {
        val title = Title(request.title)
        val description = request.description?.let { Description(it) }
        val todo = useCase.createTodo(title, description)
        val body = TodoResponse.from(todo)

        return ResponseEntity.status(HttpStatus.CREATED).body(body)
    }

    @GetMapping
    fun getAll(): List<TodoResponse> {
        return useCase.getAllTodos().map { TodoResponse.from(it) }
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): TodoResponse {
        return TodoResponse.from(useCase.getTodoById(TodoId(id)))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody request: UpdateTodoRequest): TodoResponse {
        val todo = useCase.updateTodo(
            id = TodoId(id),
            title = Title(request.title),
            description = request.description?.let { Description(it) },
            completed = request.completed
        )
        return TodoResponse.from(todo)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) {
        useCase.deleteTodo(TodoId(id))
    }
}
