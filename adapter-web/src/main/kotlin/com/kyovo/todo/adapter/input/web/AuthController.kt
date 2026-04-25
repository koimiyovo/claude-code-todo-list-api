package com.kyovo.todo.adapter.input.web

import com.kyovo.todo.adapter.input.web.dto.LoginRequest
import com.kyovo.todo.adapter.input.web.dto.LoginResponse
import com.kyovo.todo.domain.port.input.AuthUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authUseCase: AuthUseCase) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> =
        ResponseEntity.ok(LoginResponse(authUseCase.login(request.username, request.password)))

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") authorization: String): ResponseEntity<Void> {
        authUseCase.logout(authorization.removePrefix("Bearer ").trim())
        return ResponseEntity.noContent().build()
    }
}
