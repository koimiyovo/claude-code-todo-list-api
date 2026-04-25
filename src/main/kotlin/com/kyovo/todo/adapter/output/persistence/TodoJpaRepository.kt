package com.kyovo.todo.adapter.output.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface TodoJpaRepository : JpaRepository<TodoEntity, Long>
