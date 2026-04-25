package com.kyovo.todo.adapter.output.security

import com.kyovo.todo.domain.model.Token
import com.kyovo.todo.domain.model.Username
import com.kyovo.todo.domain.port.output.TokenPort
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long
) : TokenPort {

    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)) }

    override fun generate(username: Username): Token {
        val token = Jwts.builder()
            .subject(username.value)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(key)
            .compact()

        return Token(token)
    }

    override fun extractUsername(token: Token): Username {
        val value = Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token.value).payload.subject

        return Username(value)
    }

    override fun isValid(token: Token): Boolean {
        return try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token.value)
            true
        } catch (e: JwtException) {
            false
        }
    }
}
