package com.serhii.taskmanager.application.usecase

import com.serhii.taskmanager.application.dto.LoginRequest
import com.serhii.taskmanager.application.dto.TokenResponse
import com.serhii.taskmanager.infrastructure.security.JwtConfig
import com.serhii.taskmanager.infrastructure.security.SimpleIdentityProvider
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class LoginUseCase(
  private val identities: SimpleIdentityProvider,
  private val jwt: JwtConfig,
) {
  fun execute(req: LoginRequest): TokenResponse {
    val user = identities.authenticate(req.username, req.password)
    val token = jwt.issue(user.id, user.username, user.roles)
    return TokenResponse(token = token, expiresInSeconds = jwt.expiresInSeconds())
  }
}
