package com.serhii.taskmanager.infrastructure.security

import com.serhii.taskmanager.common.UnauthorizedException
import com.serhii.taskmanager.domain.model.User
import jakarta.enterprise.context.ApplicationScoped

/**
 * Dev-only in-memory users. Replace with real IdP/OIDC in production.
 */
@ApplicationScoped
class SimpleIdentityProvider {
  private val users =
    mapOf(
      "serhii" to Pair("password", User(id = "u1", username = "serhii", roles = setOf(Roles.USER))),
      "bagdan" to Pair("password", User(id = "u2", username = "bagdan", roles = setOf(Roles.USER))),
    )

  fun authenticate(
    username: String,
    password: String,
  ): User =
    users[username]?.let { (pwd, user) -> if (pwd == password) user else null }
      ?: throw UnauthorizedException("Invalid credentials")
}
