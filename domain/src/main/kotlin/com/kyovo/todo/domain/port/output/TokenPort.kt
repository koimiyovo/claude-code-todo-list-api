package com.kyovo.todo.domain.port.output

import com.kyovo.todo.domain.model.Token
import com.kyovo.todo.domain.model.Username

interface TokenPort {
    fun generate(username: Username): Token
    fun extractUsername(token: Token): Username
    fun isValid(token: Token): Boolean
}
