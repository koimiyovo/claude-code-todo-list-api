package com.kyovo.todo.domain.port.output

interface TokenPort {
    fun generate(username: String): String
    fun extractUsername(token: String): String
    fun isValid(token: String): Boolean
}
