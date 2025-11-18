package com.serhii.taskmanager.application.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
  @field:NotBlank
  val username: String,
  @field:NotBlank
  val password: String,
)

data class TokenResponse(
  val token: String,
  val expiresInSeconds: Long,
)
