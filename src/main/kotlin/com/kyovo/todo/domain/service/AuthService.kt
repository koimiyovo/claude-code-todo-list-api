package com.kyovo.todo.domain.service

import com.kyovo.todo.domain.port.input.AuthUseCase
import com.kyovo.todo.domain.port.output.TokenBlacklistPort
import com.kyovo.todo.domain.port.output.TokenPort
import com.kyovo.todo.domain.port.output.UserRepositoryPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepositoryPort,
    private val tokenPort: TokenPort,
    private val tokenBlacklist: TokenBlacklistPort,
    private val passwordEncoder: PasswordEncoder
) : AuthUseCase {

    override fun login(username: String, password: String): String {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("Identifiants invalides")
        if (!passwordEncoder.matches(password, user.password))
            throw IllegalArgumentException("Identifiants invalides")
        return tokenPort.generate(username)
    }

    override fun logout(token: String) = tokenBlacklist.invalidate(token)
}
