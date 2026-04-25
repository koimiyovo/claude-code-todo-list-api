package com.kyovo.todo.domain.port.output

import com.kyovo.todo.domain.model.Token

interface TokenBlacklistPort {
    fun invalidate(token: Token)
    fun isBlacklisted(token: Token): Boolean
}
