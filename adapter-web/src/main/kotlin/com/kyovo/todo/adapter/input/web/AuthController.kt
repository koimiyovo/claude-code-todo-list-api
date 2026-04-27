package com.kyovo.todo.adapter.input.web

import com.kyovo.todo.adapter.input.web.dto.LoginRequest
import com.kyovo.todo.adapter.input.web.dto.LoginResponse
import com.kyovo.todo.domain.model.Password
import com.kyovo.todo.domain.model.Token
import com.kyovo.todo.domain.model.Username
import com.kyovo.todo.domain.port.input.AuthUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Authentification")
@RestController
@RequestMapping("/api/auth")
class AuthController(private val authUseCase: AuthUseCase) {

    @Operation(summary = "Se connecter", description = "Retourne un JWT à utiliser dans le header Authorization.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Connexion réussie"),
        ApiResponse(responseCode = "400", description = "Identifiants invalides",
            content = [Content(mediaType = "application/json",
                schema = Schema(example = """{"error": "Identifiants invalides"}"""))])
    ])
    @SecurityRequirements
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val token = authUseCase.login(Username(request.username), Password(request.password))

        return ResponseEntity.ok(LoginResponse(token.value))
    }

    @Operation(summary = "Se déconnecter", description = "Invalide le JWT courant (blacklist en mémoire).")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Déconnexion réussie"),
        ApiResponse(responseCode = "401", description = "Token manquant ou invalide", content = [Content()])
    ])
    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") authorization: String): ResponseEntity<Void> {
        authUseCase.logout(Token(authorization.removePrefix("Bearer ").trim()))
        return ResponseEntity.noContent().build()
    }
}
