package com.kyovo.todo.domain.service

import com.kyovo.todo.domain.annotation.DomainService
import com.kyovo.todo.domain.model.Password
import com.kyovo.todo.domain.model.Token
import com.kyovo.todo.domain.model.Username
import com.kyovo.todo.domain.port.input.AuthUseCase
import com.kyovo.todo.domain.port.output.TokenBlacklistPort
import com.kyovo.todo.domain.port.output.TokenPort
import com.kyovo.todo.domain.port.output.UserRepositoryPort
import org.springframework.security.crypto.password.PasswordEncoder

@DomainService
class AuthService(
    private val userRepository: UserRepositoryPort,
    private val tokenPort: TokenPort,
    private val tokenBlacklist: TokenBlacklistPort,
    private val passwordEncoder: PasswordEncoder
) : AuthUseCase {

    override fun login(username: Username, password: Password): Token {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("Identifiants invalides")
        if (!passwordEncoder.matches(password.value, user.password.value))
            throw IllegalArgumentException("Identifiants invalides")
        return tokenPort.generate(username)
    }

    override fun logout(token: Token) {
        tokenBlacklist.invalidate(token)
    }
}
