package com.kyovo.todo.adapter.output.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TodoJpaRepository : JpaRepository<TodoEntity, UUID>
