package com.kyovo.todo.config

import com.kyovo.todo.domain.model.User
import com.kyovo.todo.domain.port.output.UserRepositoryPort
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val userRepository: UserRepositoryPort,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (userRepository.findByUsername("admin") == null) {
            userRepository.save(User(username = "admin", password = passwordEncoder.encode("admin123")))
        }
    }
}
