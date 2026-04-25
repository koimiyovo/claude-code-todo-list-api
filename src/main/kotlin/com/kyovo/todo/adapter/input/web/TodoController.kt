package com.kyovo.todo.adapter.input.web

import com.kyovo.todo.adapter.input.web.dto.CreateTodoRequest
import com.kyovo.todo.adapter.input.web.dto.TodoResponse
import com.kyovo.todo.adapter.input.web.dto.UpdateTodoRequest
import com.kyovo.todo.domain.port.input.TodoUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/todos")
class TodoController(private val useCase: TodoUseCase) {

    @PostMapping
    fun create(@RequestBody request: CreateTodoRequest): ResponseEntity<TodoResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(TodoResponse.from(useCase.createTodo(request.title, request.description)))

    @GetMapping
    fun getAll(): List<TodoResponse> =
        useCase.getAllTodos().map { TodoResponse.from(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): TodoResponse =
        TodoResponse.from(useCase.getTodoById(id))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UpdateTodoRequest): TodoResponse =
        TodoResponse.from(useCase.updateTodo(id, request.title, request.description, request.completed))

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = useCase.deleteTodo(id)
}
