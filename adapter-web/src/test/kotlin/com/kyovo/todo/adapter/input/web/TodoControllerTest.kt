package com.kyovo.todo.adapter.input.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.kyovo.todo.adapter.input.web.dto.CreateTodoRequest
import com.kyovo.todo.adapter.input.web.dto.UpdateTodoRequest
import com.kyovo.todo.domain.model.Description
import com.kyovo.todo.domain.model.Title
import com.kyovo.todo.domain.model.Todo
import com.kyovo.todo.domain.model.TodoId
import com.kyovo.todo.domain.port.input.TodoUseCase
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(
    controllers = [TodoController::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
    excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = [SecurityConfig::class, JwtAuthenticationFilter::class]
    )]
)
class TodoControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var todoUseCase: TodoUseCase

    private val defaultId = UUID.fromString("23eb7bab-0df0-4a36-b5ed-bb35e93a5fec")
    private val otherId = UUID.fromString("c88b37f5-f622-45ff-ab10-a6dd957c246c")
    private fun todo(id: UUID = defaultId, title: String = "Test", completed: Boolean = false): Todo {
        return Todo(id = TodoId(id), title = Title(title), completed = completed)
    }

    // ── POST /api/todos ───────────────────────────────────────────────────────

    @Test
    fun `POST crée un todo et retourne 201`() {
        whenever(todoUseCase.createTodo(Title("Acheter du lait"), null))
            .thenReturn(todo(title = "Acheter du lait"))

        mockMvc.perform(
            post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateTodoRequest("Acheter du lait")))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(defaultId.toString()))
            .andExpect(jsonPath("$.title").value("Acheter du lait"))
            .andExpect(jsonPath("$.completed").value(false))
    }

    @Test
    fun `POST avec description la transmet au use case`() {
        whenever(todoUseCase.createTodo(Title("Tâche"), Description("Détail")))
            .thenReturn(todo(title = "Tâche").copy(description = Description("Détail")))

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
            .thenReturn(listOf(todo(defaultId, "Todo 1"), todo(otherId, "Todo 2")))

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
        whenever(todoUseCase.getTodoById(TodoId(defaultId))).thenReturn(todo(defaultId, "Ma tâche"))

        mockMvc.perform(get("/api/todos/$defaultId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(defaultId.toString()))
            .andExpect(jsonPath("$.title").value("Ma tâche"))
    }

    @Test
    fun `GET par id inexistant retourne 404 avec message`() {
        whenever(todoUseCase.getTodoById(TodoId(otherId))).thenThrow(NoSuchElementException("Todo $otherId introuvable"))

        mockMvc.perform(get("/api/todos/$otherId"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Todo $otherId introuvable"))
    }

    // ── PUT /api/todos/{id} ───────────────────────────────────────────────────

    @Test
    fun `PUT met à jour et retourne 200`() {
        whenever(todoUseCase.updateTodo(TodoId(defaultId), Title("Titre modifié"), null, true))
            .thenReturn(todo(defaultId, "Titre modifié", completed = true))

        mockMvc.perform(
            put("/api/todos/$defaultId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(UpdateTodoRequest("Titre modifié", completed = true)))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Titre modifié"))
            .andExpect(jsonPath("$.completed").value(true))
    }

    @Test
    fun `PUT sur id inexistant retourne 404`() {
        whenever(todoUseCase.updateTodo(TodoId(otherId), Title("X"), null, false))
            .thenThrow(NoSuchElementException("Todo $otherId introuvable"))

        mockMvc.perform(
            put("/api/todos/$otherId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"title":"X","completed":false}""")
        )
            .andExpect(status().isNotFound)
    }

    // ── DELETE /api/todos/{id} ────────────────────────────────────────────────

    @Test
    fun `DELETE retourne 204`() {
        mockMvc.perform(delete("/api/todos/$defaultId"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `DELETE sur id inexistant retourne 404`() {
        whenever(todoUseCase.deleteTodo(TodoId(otherId))).thenThrow(NoSuchElementException("Todo $otherId introuvable"))

        mockMvc.perform(delete("/api/todos/$otherId"))
            .andExpect(status().isNotFound)
    }
}
