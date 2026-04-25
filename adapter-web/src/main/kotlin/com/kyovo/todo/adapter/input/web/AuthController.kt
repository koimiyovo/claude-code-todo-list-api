package com.kyovo.todo.adapter.input.web

import com.kyovo.todo.adapter.input.web.dto.LoginRequest
import com.kyovo.todo.adapter.input.web.dto.LoginResponse
import com.kyovo.todo.domain.model.Password
import com.kyovo.todo.domain.model.Token
import com.kyovo.todo.domain.model.Username
import com.kyovo.todo.domain.port.input.AuthUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authUseCase: AuthUseCase) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val token = authUseCase.login(Username(request.username), Password(request.password))

        return ResponseEntity.ok(LoginResponse(token.value))
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") authorization: String): ResponseEntity<Void> {
        authUseCase.logout(Token(authorization.removePrefix("Bearer ").trim()))
        return ResponseEntity.noContent().build()
    }
}
