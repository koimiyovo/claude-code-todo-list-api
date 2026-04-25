package com.kyovo.todo.adapter.input.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.kyovo.todo.adapter.input.web.dto.CreateTodoRequest
import com.kyovo.todo.adapter.input.web.dto.UpdateTodoRequest
import com.kyovo.todo.domain.model.Todo
import com.kyovo.todo.domain.port.input.TodoUseCase
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(
    controllers = [TodoController::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
    excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = [SecurityConfig::class, JwtAuthenticationFilter::class]
    )]
)
class TodoControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockitoBean private lateinit var todoUseCase: TodoUseCase

    private fun todo(id: Long = 1L, title: String = "Test", completed: Boolean = false) =
        Todo(id = id, title = title, completed = completed)

    // ── POST /api/todos ───────────────────────────────────────────────────────

    @Test
    fun `POST crée un todo et retourne 201`() {
        whenever(todoUseCase.createTodo("Acheter du lait", null))
            .thenReturn(todo(title = "Acheter du lait"))

        mockMvc.perform(
            post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateTodoRequest("Acheter du lait")))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Acheter du lait"))
            .andExpect(jsonPath("$.completed").value(false))
    }

    @Test
    fun `POST avec description la transmet au use case`() {
        whenever(todoUseCase.createTodo("Tâche", "Détail"))
            .thenReturn(todo(title = "Tâche").copy(description = "Détail"))

        mockMvc.perform(
            post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"title":"Tâche","description":"Détail"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.description").value("Détail"))
    }

    // ── GET /api/todos ────────────────────────────────────────────────────────

    @Test
    fun `GET liste retourne 200 avec tous les todos`() {
        whenever(todoUseCase.getAllTodos())
            .thenReturn(listOf(todo(1L, "Todo 1"), todo(2L, "Todo 2")))

        mockMvc.perform(get("/api/todos"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Todo 1"))
            .andExpect(jsonPath("$[1].title").value("Todo 2"))
    }

    @Test
    fun `GET liste vide retourne 200 avec tableau vide`() {
        whenever(todoUseCase.getAllTodos()).thenReturn(emptyList())

        mockMvc.perform(get("/api/todos"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))
    }

    // ── GET /api/todos/{id} ───────────────────────────────────────────────────

    @Test
    fun `GET par id retourne 200 avec le todo`() {
        whenever(todoUseCase.getTodoById(1L)).thenReturn(todo(1L, "Ma tâche"))

        mockMvc.perform(get("/api/todos/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Ma tâche"))
    }

    @Test
    fun `GET par id inexistant retourne 404 avec message`() {
        whenever(todoUseCase.getTodoById(99L)).thenThrow(NoSuchElementException("Todo 99 introuvable"))

        mockMvc.perform(get("/api/todos/99"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Todo 99 introuvable"))
    }

    // ── PUT /api/todos/{id} ───────────────────────────────────────────────────

    @Test
    fun `PUT met à jour et retourne 200`() {
        whenever(todoUseCase.updateTodo(1L, "Titre modifié", null, true))
            .thenReturn(todo(1L, "Titre modifié", completed = true))

        mockMvc.perform(
            put("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(UpdateTodoRequest("Titre modifié", completed = true)))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Titre modifié"))
            .andExpect(jsonPath("$.completed").value(true))
    }

    @Test
    fun `PUT sur id inexistant retourne 404`() {
        whenever(todoUseCase.updateTodo(99L, "X", null, false))
            .thenThrow(NoSuchElementException("Todo 99 introuvable"))

        mockMvc.perform(
            put("/api/todos/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"title":"X","completed":false}""")
        )
            .andExpect(status().isNotFound)
    }

    // ── DELETE /api/todos/{id} ────────────────────────────────────────────────

    @Test
    fun `DELETE retourne 204`() {
        mockMvc.perform(delete("/api/todos/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `DELETE sur id inexistant retourne 404`() {
        whenever(todoUseCase.deleteTodo(99L)).thenThrow(NoSuchElementException("Todo 99 introuvable"))

        mockMvc.perform(delete("/api/todos/99"))
            .andExpect(status().isNotFound)
    }
}
