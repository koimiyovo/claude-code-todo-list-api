package com.kyovo.todo.adapter.input.web.dto

data class LoginRequest(val username: String, val password: String)

data class LoginResponse(val token: String)
