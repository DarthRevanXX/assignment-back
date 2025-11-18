package com.serhii.taskmanager.infrastructure.security

import com.serhii.taskmanager.common.UnauthorizedException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SimpleIdentityProviderTest {
  private val idp = SimpleIdentityProvider()

  @ParameterizedTest
  @CsvSource(
    "serhii,password,u1,serhii",
    "bagdan,password,u2,bagdan",
  )
  fun `authenticate succeeds for known users with correct password`(
    username: String,
    password: String,
    expectedId: String,
    expectedName: String,
  ) {
    val user = idp.authenticate(username, password)

    assertThat(user.id).isEqualTo(expectedId)
    assertThat(user.username).isEqualTo(expectedName)
    assertThat(user.roles).containsExactly(Roles.USER)
  }

  @ParameterizedTest
  @CsvSource(
    "serhii,wrong",
    "bagdan,WRONG",
    "unknown,password",
    "'',password",
    "serhii,''",
  )
  fun `authenticate fails with UnauthorizedException for invalid credentials`(
    username: String,
    password: String,
  ) {
    val ex =
      assertThrows(UnauthorizedException::class.java) {
        idp.authenticate(username, password)
      }
    assertThat(ex.message).isEqualTo("Invalid credentials")
  }

  @Test
  fun `usernames are case sensitive`() {
    val ex =
      assertThrows(UnauthorizedException::class.java) {
        idp.authenticate("Serhii", "password")
      }
    assertThat(ex.message).isEqualTo("Invalid credentials")
  }
}
