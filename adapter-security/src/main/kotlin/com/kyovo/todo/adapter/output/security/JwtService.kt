package com.kyovo.todo.adapter.output.security

import com.kyovo.todo.domain.port.output.TokenPort
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long
) : TokenPort {

    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)) }

    override fun generate(username: String): String = Jwts.builder()
        .subject(username)
        .issuedAt(Date())
        .expiration(Date(System.currentTimeMillis() + expiration))
        .signWith(key)
        .compact()

    override fun extractUsername(token: String): String =
        Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token).payload.subject

    override fun isValid(token: String): Boolean = try {
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
        true
    } catch (e: JwtException) {
        false
    }
}
