package com.serhii.taskmanager.infrastructure.web

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import jakarta.ws.rs.core.MediaType
import org.hamcrest.Matchers.emptyString
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test

@QuarkusTest
class AuthResourceIT {
  @Test
  fun `login success returns token and expiry`() {
    given()
      .contentType(MediaType.APPLICATION_JSON)
      .body("""{"username":"serhii","password":"password"}""")
      .`when`()
      .post("/api/v1/auth/login")
      .then()
      .statusCode(200)
      .body("token", not(emptyString()))
      .body("expiresInSeconds", greaterThan(0))
  }

  @Test
  fun `login with invalid credentials returns 401`() {
    given()
      .contentType(MediaType.APPLICATION_JSON)
      .body("""{"username":"serhii","password":"wrong"}""")
      .`when`()
      .post("/api/v1/auth/login")
      .then()
      .statusCode(401)
  }
}
