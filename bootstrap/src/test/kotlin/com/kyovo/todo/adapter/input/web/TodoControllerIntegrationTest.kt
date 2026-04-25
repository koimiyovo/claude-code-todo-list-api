package com.kyovo.todo.adapter.input.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.kyovo.todo.adapter.output.persistence.TodoJpaRepository
import com.kyovo.todo.adapter.output.security.TokenBlacklistService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerIntegrationTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper
    @Autowired private lateinit var todoJpaRepository: TodoJpaRepository
    @Autowired private lateinit var tokenBlacklistService: TokenBlacklistService

    private var token: String = ""

    @BeforeEach
    fun setUp() {
        todoJpaRepository.deleteAll()
        tokenBlacklistService.clear()
        token = obtainToken()
    }

    // ── Authentification ──────────────────────────────────────────────────────

    @Test
    fun `requête sans token retourne 401`() {
        mockMvc.perform(get("/api/todos"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `token blacklisté après logout retourne 401`() {
        mockMvc.perform(get("/api/todos").bearer(token))
            .andExpect(status().isOk)

        mockMvc.perform(post("/api/auth/logout").bearer(token))
            .andExpect(status().isNoContent)

        mockMvc.perform(get("/api/todos").bearer(token))
            .andExpect(status().isUnauthorized)
    }

    // ── POST /api/todos ───────────────────────────────────────────────────────

    @Test
    fun `POST todo crée et retourne 201 avec les champs attendus`() {
        mockMvc.perform(
            post("/api/todos").bearer(token).json("""{"title":"Acheter du lait","description":"2 litres"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isNumber)
            .andExpect(jsonPath("$.title").value("Acheter du lait"))
            .andExpect(jsonPath("$.description").value("2 litres"))
            .andExpect(jsonPath("$.completed").value(false))
    }

    @Test
    fun `POST todo sans description crée avec description nulle`() {
        mockMvc.perform(
            post("/api/todos").bearer(token).json("""{"title":"Sans description"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.description").doesNotExist())
    }

    // ── GET /api/todos ────────────────────────────────────────────────────────

    @Test
    fun `GET todos retourne liste vide quand aucun todo`() {
        mockMvc.perform(get("/api/todos").bearer(token))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))
    }

    @Test
    fun `GET todos retourne tous les todos créés`() {
        createTodo("Todo 1")
        createTodo("Todo 2")

        mockMvc.perform(get("/api/todos").bearer(token))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }

    // ── GET /api/todos/{id} ───────────────────────────────────────────────────

    @Test
    fun `GET todo par id retourne le todo`() {
        val id = createTodo("Tâche importante")

        mockMvc.perform(get("/api/todos/$id").bearer(token))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.title").value("Tâche importante"))
    }

    @Test
    fun `GET todo inexistant retourne 404`() {
        mockMvc.perform(get("/api/todos/999").bearer(token))
            .andExpect(status().isNotFound)
    }

    // ── PUT /api/todos/{id} ───────────────────────────────────────────────────

    @Test
    fun `PUT todo met à jour titre, description et statut`() {
        val id = createTodo("Titre original")

        mockMvc.perform(
            put("/api/todos/$id").bearer(token)
                .json("""{"title":"Titre modifié","description":"Nouvelle desc","completed":true}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Titre modifié"))
            .andExpect(jsonPath("$.description").value("Nouvelle desc"))
            .andExpect(jsonPath("$.completed").value(true))
    }

    @Test
    fun `PUT todo inexistant retourne 404`() {
        mockMvc.perform(
            put("/api/todos/999").bearer(token).json("""{"title":"X","completed":false}""")
        )
            .andExpect(status().isNotFound)
    }

    // ── DELETE /api/todos/{id} ────────────────────────────────────────────────

    @Test
    fun `DELETE todo retourne 204 et supprime en base`() {
        val id = createTodo("A supprimer")

        mockMvc.perform(delete("/api/todos/$id").bearer(token))
            .andExpect(status().isNoContent)

        assertThat(todoJpaRepository.findById(id)).isEmpty
    }

    @Test
    fun `DELETE todo inexistant retourne 404`() {
        mockMvc.perform(delete("/api/todos/999").bearer(token))
            .andExpect(status().isNotFound)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun obtainToken(): String {
        val result = mockMvc.perform(
            post("/api/auth/login").json("""{"username":"admin","password":"admin123"}""")
        ).andReturn()

        return objectMapper.readTree(result.response.contentAsString).get("token").asText()
    }

    private fun createTodo(title: String): Long {
        val result = mockMvc.perform(
            post("/api/todos").bearer(token).json("""{"title":"$title"}""")
        ).andReturn()
        return objectMapper.readTree(result.response.contentAsString).get("id").asLong()
    }

    private fun org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder.bearer(token: String) =
        header("Authorization", "Bearer $token")

    private fun org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder.json(body: String) =
        contentType(MediaType.APPLICATION_JSON).content(body)
}
