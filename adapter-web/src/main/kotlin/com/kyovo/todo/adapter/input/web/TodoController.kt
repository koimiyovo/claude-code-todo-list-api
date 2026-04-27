package com.kyovo.todo.adapter.input.web

import com.kyovo.todo.adapter.input.web.dto.CreateTodoRequest
import com.kyovo.todo.adapter.input.web.dto.TodoResponse
import com.kyovo.todo.adapter.input.web.dto.UpdateTodoRequest
import com.kyovo.todo.domain.model.Description
import com.kyovo.todo.domain.model.Title
import com.kyovo.todo.domain.model.TodoId
import com.kyovo.todo.domain.port.input.TodoUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "Todos")
@RestController
@RequestMapping("/api/todos")
class TodoController(private val useCase: TodoUseCase) {

    @Operation(summary = "Créer un todo")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Todo créé"),
            ApiResponse(
                responseCode = "400", description = "Données invalides",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(example = """{"error": "Le titre ne peut pas être vide"}""")
                )]
            ),
            ApiResponse(responseCode = "401", description = "Token manquant ou invalide", content = [Content()]),
            ApiResponse(responseCode = "403", description = "Rôle ADMIN requis", content = [Content()])
        ]
    )
    @PostMapping
    fun create(@RequestBody request: CreateTodoRequest): ResponseEntity<TodoResponse> {
        val title = Title(request.title)
        val description = request.description?.let { Description(it) }
        val todo = useCase.createTodo(title, description)
        val body = TodoResponse.from(todo)

        return ResponseEntity.status(HttpStatus.CREATED).body(body)
    }

    @Operation(summary = "Lister tous les todos")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Liste des todos"),
            ApiResponse(responseCode = "401", description = "Token manquant ou invalide", content = [Content()])
        ]
    )
    @GetMapping
    fun getAll(): List<TodoResponse> {
        return useCase.getAllTodos().map { TodoResponse.from(it) }
    }

    @Operation(summary = "Obtenir un todo par son id")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Todo trouvé"),
            ApiResponse(responseCode = "401", description = "Token manquant ou invalide", content = [Content()]),
            ApiResponse(
                responseCode = "404", description = "Todo introuvable",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(example = """{"error": "Todo ... introuvable"}""")
                )]
            )
        ]
    )
    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): TodoResponse {
        return TodoResponse.from(useCase.getTodoById(TodoId(id)))
    }

    @Operation(summary = "Mettre à jour un todo")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Todo mis à jour"),
            ApiResponse(
                responseCode = "400", description = "Données invalides",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(example = """{"error": "Le titre ne peut pas être vide"}""")
                )]
            ),
            ApiResponse(responseCode = "401", description = "Token manquant ou invalide", content = [Content()]),
            ApiResponse(responseCode = "403", description = "Rôle ADMIN requis", content = [Content()]),
            ApiResponse(
                responseCode = "404", description = "Todo introuvable",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(example = """{"error": "Todo ... introuvable"}""")
                )]
            )
        ]
    )
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

    @Operation(summary = "Supprimer un todo")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Todo supprimé"),
            ApiResponse(responseCode = "401", description = "Token manquant ou invalide", content = [Content()]),
            ApiResponse(responseCode = "403", description = "Rôle ADMIN requis", content = [Content()]),
            ApiResponse(
                responseCode = "404", description = "Todo introuvable",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(example = """{"error": "Todo ... introuvable"}""")
                )]
            )
        ]
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) {
        useCase.deleteTodo(TodoId(id))
    }
}
