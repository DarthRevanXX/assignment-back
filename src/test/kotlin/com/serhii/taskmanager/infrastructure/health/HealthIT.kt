package com.serhii.taskmanager.infrastructure.health

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.junit.jupiter.api.Test

@QuarkusTest
class HealthIT {
  @Test
  fun `liveness and readiness are up`() {
    given()
      .`when`()
      .get("/q/health/live")
      .then()
      .statusCode(200)
    given()
      .`when`()
      .get("/q/health/ready")
      .then()
      .statusCode(200)
  }
}
