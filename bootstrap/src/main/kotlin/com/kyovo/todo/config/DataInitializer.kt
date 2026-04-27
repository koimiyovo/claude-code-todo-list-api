package com.kyovo.todo.config

import com.kyovo.todo.domain.model.NewUser
import com.kyovo.todo.domain.model.Password
import com.kyovo.todo.domain.model.Role
import com.kyovo.todo.domain.model.Username
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
        if (userRepository.findByUsername(Username("admin")) == null) {
            userRepository.save(
                NewUser(
                    username = Username("admin"),
                    password = Password(passwordEncoder.encode("admin123")),
                    role = Role.ADMIN
                )
            )
        }
        if (userRepository.findByUsername(Username("user")) == null) {
            userRepository.save(
                NewUser(
                    username = Username("user"),
                    password = Password(passwordEncoder.encode("user123")),
                    role = Role.USER
                )
            )
        }
    }
}
