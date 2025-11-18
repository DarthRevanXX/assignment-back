package com.serhii.taskmanager.application.usecase

import com.serhii.taskmanager.application.dto.LoginRequest
import com.serhii.taskmanager.application.usecase.LoginUseCase
import com.serhii.taskmanager.domain.model.User
import com.serhii.taskmanager.infrastructure.security.JwtConfig
import com.serhii.taskmanager.infrastructure.security.Roles
import com.serhii.taskmanager.infrastructure.security.SimpleIdentityProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LoginUseCaseTest {
  private val idp = mockk<SimpleIdentityProvider>()
  private val jwt = mockk<JwtConfig>()
  private val uc = LoginUseCase(idp, jwt)

  @Test
  fun `issues token for valid credentials`() {
    val user = User(id = "u1", username = "alice", roles = setOf(Roles.USER))
    every { idp.authenticate("alice", "password") } returns user
    every { jwt.issue(user.id, user.username, user.roles) } returns "TOKEN"
    every { jwt.expiresInSeconds() } returns 600

    val res = uc.execute(LoginRequest("alice", "password"))

    assertThat(res.token).isEqualTo("TOKEN")
    assertThat(res.expiresInSeconds).isEqualTo(600)

    verify {
      idp.authenticate("alice", "password")
      jwt.issue("u1", "alice", setOf(Roles.USER))
      jwt.expiresInSeconds()
    }
  }
}
