package com.kyovo.todo.domain.port.output

interface TokenBlacklistPort {
    fun invalidate(token: String)
    fun isBlacklisted(token: String): Boolean
}
