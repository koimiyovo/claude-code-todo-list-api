package com.kyovo.todo.domain.service

import com.kyovo.todo.domain.model.Todo
import com.kyovo.todo.domain.port.output.TodoRepositoryPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class TodoServiceTest {

    @Mock
    private lateinit var repository: TodoRepositoryPort

    @InjectMocks
    private lateinit var service: TodoService

    private val todo = Todo(id = 1L, title = "Faire les courses", description = "Lait, pain")

    @Test
    fun `createTodo sauvegarde avec les bons champs et retourne le todo`() {
        whenever(repository.save(any())).thenReturn(todo)

        val result = service.createTodo("Faire les courses", "Lait, pain")

        assertThat(result).isEqualTo(todo)
        val captor = argumentCaptor<Todo>()
        verify(repository).save(captor.capture())
        assertThat(captor.firstValue.title).isEqualTo("Faire les courses")
        assertThat(captor.firstValue.description).isEqualTo("Lait, pain")
        assertThat(captor.firstValue.completed).isFalse()
        assertThat(captor.firstValue.id).isNull()
    }

    @Test
    fun `createTodo accepte une description nulle`() {
        whenever(repository.save(any())).thenReturn(todo.copy(description = null))

        service.createTodo("Sans description", null)

        val captor = argumentCaptor<Todo>()
        verify(repository).save(captor.capture())
        assertThat(captor.firstValue.description).isNull()
    }

    @Test
    fun `getTodoById retourne le todo quand il existe`() {
        whenever(repository.findById(1L)).thenReturn(todo)
        assertThat(service.getTodoById(1L)).isEqualTo(todo)
    }

    @Test
    fun `getTodoById lève NoSuchElementException quand introuvable`() {
        whenever(repository.findById(99L)).thenReturn(null)
        assertThrows<NoSuchElementException> { service.getTodoById(99L) }
    }

    @Test
    fun `getAllTodos retourne la liste du repository`() {
        val todos = listOf(todo, todo.copy(id = 2L, title = "Autre tâche"))
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
        val updated = todo.copy(title = "Nouveau titre", description = null, completed = true)
        whenever(repository.findById(1L)).thenReturn(todo)
        whenever(repository.save(updated)).thenReturn(updated)

        val result = service.updateTodo(1L, "Nouveau titre", null, true)

        assertThat(result.title).isEqualTo("Nouveau titre")
        assertThat(result.description).isNull()
        assertThat(result.completed).isTrue()
        verify(repository).save(updated)
    }

    @Test
    fun `updateTodo lève NoSuchElementException et n'appelle pas save quand introuvable`() {
        whenever(repository.findById(99L)).thenReturn(null)

        assertThrows<NoSuchElementException> { service.updateTodo(99L, "X", null, false) }

        verify(repository, never()).save(any())
    }

    @Test
    fun `deleteTodo appelle deleteById quand le todo existe`() {
        whenever(repository.existsById(1L)).thenReturn(true)

        service.deleteTodo(1L)

        verify(repository).deleteById(1L)
    }

    @Test
    fun `deleteTodo lève NoSuchElementException et ne supprime rien quand introuvable`() {
        whenever(repository.existsById(99L)).thenReturn(false)

        assertThrows<NoSuchElementException> { service.deleteTodo(99L) }

        verify(repository, never()).deleteById(any())
    }
}
