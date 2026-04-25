package com.kyovo.todo.adapter.output.security

import com.kyovo.todo.domain.model.Token
import com.kyovo.todo.domain.port.output.TokenBlacklistPort
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class TokenBlacklistService : TokenBlacklistPort {

    private val blacklist: MutableSet<Token> = ConcurrentHashMap.newKeySet()

    override fun invalidate(token: Token) {
        blacklist.add(token)
    }

    override fun isBlacklisted(token: Token): Boolean {
        return token in blacklist
    }

    fun clear() {
        blacklist.clear()
    }
}
