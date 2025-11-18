package com.serhii.taskmanager.infrastructure.security

import io.smallrye.jwt.build.Jwt
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.Duration

@ApplicationScoped
class JwtConfig(
  @param:ConfigProperty(name = "mp.jwt.verify.issuer") private val issuer: String,
  @param:ConfigProperty(name = "jwt.token.exp.minutes", defaultValue = "60") private val expMinutes: Long,
  @param:ConfigProperty(name = "jwt.token.aud", defaultValue = "task-manager-web") private val aud: String,
) {
  fun issue(
    userId: String,
    username: String,
    roles: Set<String>,
  ): String =
    Jwt
      .issuer(issuer)
      .subject(userId)
      .upn(username)
      .groups(roles)
      .audience(aud)
      .expiresIn(Duration.ofMinutes(expMinutes))
      .sign()

  fun expiresInSeconds(): Long = Duration.ofMinutes(expMinutes).seconds
}
