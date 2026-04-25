package com.kyovo.todo.adapter.output.security

import com.kyovo.todo.domain.port.output.TokenBlacklistPort
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class TokenBlacklistService : TokenBlacklistPort {

    private val blacklist: MutableSet<String> = ConcurrentHashMap.newKeySet()

    override fun invalidate(token: String) { blacklist.add(token) }

    override fun isBlacklisted(token: String): Boolean = token in blacklist

    fun clear() { blacklist.clear() }
}
