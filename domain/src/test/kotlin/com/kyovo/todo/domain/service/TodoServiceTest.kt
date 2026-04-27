package com.kyovo.todo.domain.service

import com.kyovo.todo.domain.model.Description
import com.kyovo.todo.domain.model.NewTodo
import com.kyovo.todo.domain.model.Title
import com.kyovo.todo.domain.model.Todo
import com.kyovo.todo.domain.model.TodoId
import com.kyovo.todo.domain.port.output.TodoRepositoryPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class TodoServiceTest {

    @Mock
    private lateinit var repository: TodoRepositoryPort

    @InjectMocks
    private lateinit var service: TodoService

    private val todoId = TodoId(UUID.fromString("d7adba75-aae4-48aa-b927-68b3220f4e7d"))
    private val todo = Todo(
        id = todoId,
        title = Title("Faire les courses"),
        description = Description("Lait, pain"),
        completed = false,
        createdAt = LocalDateTime.of(2026, 1, 1, 0, 0)
    )

    val unknownId = TodoId(UUID.fromString("20630804-f84c-466b-9203-9cd20cde2bfd"))


    @Test
    fun `createTodo sauvegarde avec les bons champs et retourne le todo`() {
        whenever(repository.create(any<NewTodo>())).thenReturn(todo)

        val result = service.createTodo(Title("Faire les courses"), Description("Lait, pain"))

        assertThat(result).isEqualTo(todo)
        val captor = argumentCaptor<NewTodo>()
        verify(repository).create(captor.capture())
        assertThat(captor.firstValue.title).isEqualTo(Title("Faire les courses"))
        assertThat(captor.firstValue.description).isEqualTo(Description("Lait, pain"))
        assertThat(captor.firstValue.completed).isFalse()
    }

    @Test
    fun `createTodo accepte une description nulle`() {
        whenever(repository.create(any<NewTodo>())).thenReturn(todo.copy(description = null))

        service.createTodo(Title("Sans description"), null)

        val captor = argumentCaptor<NewTodo>()
        verify(repository).create(captor.capture())
        assertThat(captor.firstValue.description).isNull()
    }

    @Test
    fun `getTodoById retourne le todo quand il existe`() {
        whenever(repository.findById(todoId)).thenReturn(todo)

        assertThat(service.getTodoById(todoId)).isEqualTo(todo)
    }

    @Test
    fun `getTodoById lève NoSuchElementException quand introuvable`() {
        whenever(repository.findById(unknownId)).thenReturn(null)
        assertThrows<NoSuchElementException> { service.getTodoById(unknownId) }
    }

    @Test
    fun `getAllTodos retourne la liste du repository`() {
        val todos = listOf(todo, todo.copy(id = TodoId(UUID.randomUUID()), title = Title("Autre tâche")))
        whenever(repository.findAll()).thenReturn(todos)
        assertThat(service.getAllTodos()).containsExactlyElementsOf(todos)
    }

    @Test
    fun `getAllTodos retourne une liste vide quand aucun todo`() {
        whenever(repository.findAll()).thenReturn(emptyList())
        assertThat(service.getAllTodos()).isEmpty()
    }

    @Test
    fun `updateTodo applique tous les changements et sauvegarde`() {
        val updated = todo.copy(title = Title("Nouveau titre"), description = null, completed = true)
        whenever(repository.findById(todoId)).thenReturn(todo)
        whenever(repository.update(updated)).thenReturn(updated)

        val result = service.updateTodo(
            id = todoId,
            title = Title("Nouveau titre"),
            description = null,
            completed = true
        )

        assertThat(result.title).isEqualTo(Title("Nouveau titre"))
        assertThat(result.description).isNull()
        assertThat(result.completed).isTrue()
        verify(repository).update(updated)
    }

    @Test
    fun `updateTodo lève NoSuchElementException et n'appelle pas update quand introuvable`() {

        whenever(repository.findById(unknownId)).thenReturn(null)

        assertThrows<NoSuchElementException> {
            service.updateTodo(
                id = unknownId,
                title = Title("X"),
                description = null,
                completed = false
            )
        }

        verify(repository, never()).update(any<Todo>())
    }

    @Test
    fun `deleteTodo appelle deleteById quand le todo existe`() {
        whenever(repository.existsById(todoId)).thenReturn(true)

        service.deleteTodo(todoId)

        verify(repository).deleteById(todoId)
    }

    @Test
    fun `deleteTodo lève NoSuchElementException et ne supprime rien quand introuvable`() {
        whenever(repository.existsById(unknownId)).thenReturn(false)

        assertThrows<NoSuchElementException> { service.deleteTodo(unknownId) }

        verify(repository, never()).deleteById(any())
    }
}
