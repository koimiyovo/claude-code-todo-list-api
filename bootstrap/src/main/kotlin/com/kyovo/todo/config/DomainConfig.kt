package com.kyovo.todo.config

import com.kyovo.todo.domain.port.output.TodoRepositoryPort
import com.kyovo.todo.domain.port.output.TokenBlacklistPort
import com.kyovo.todo.domain.port.output.TokenPort
import com.kyovo.todo.domain.port.output.UserRepositoryPort
import com.kyovo.todo.domain.service.AuthService
import com.kyovo.todo.domain.service.TodoService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Clock

@Configuration
class DomainConfig {

    @Bean
    fun clock(): Clock {
        return Clock.systemDefaultZone()
    }

    @Bean
    fun todoService(todoRepository: TodoRepositoryPort, clock: Clock): TodoService {
        return TodoService(todoRepository, clock)
    }

    @Bean
    fun authService(
        userRepository: UserRepositoryPort,
        tokenPort: TokenPort,
        tokenBlacklist: TokenBlacklistPort,
        passwordEncoder: PasswordEncoder
    ): AuthService {
        return AuthService(userRepository, tokenPort, tokenBlacklist, passwordEncoder)
    }
}
