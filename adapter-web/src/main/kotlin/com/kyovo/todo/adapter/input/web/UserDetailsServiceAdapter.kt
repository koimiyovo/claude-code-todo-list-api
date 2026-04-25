package com.kyovo.todo.adapter.input.web

import com.kyovo.todo.domain.model.Username
import com.kyovo.todo.domain.port.output.UserRepositoryPort
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class UserDetailsServiceAdapter(
    private val userRepository: UserRepositoryPort
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(Username(username))
            ?: throw UsernameNotFoundException("Utilisateur '$username' introuvable")

        return User(
            user.username.value,
            user.password.value,
            listOf(SimpleGrantedAuthority("ROLE_${user.role}"))
        )
    }
}
