package com.serhii.taskmanager.infrastructure.web

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import jakarta.ws.rs.core.MediaType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TaskResourceIT {
  private fun loginAndGetToken(
    username: String = "serhii",
    password: String = "password",
  ): String =
    given()
      .contentType(MediaType.APPLICATION_JSON)
      .body("""{"username":"$username","password":"$password"}""")
      .`when`()
      .post("/api/v1/auth/login")
      .then()
      .statusCode(200)
      .extract()
      .jsonPath()
      .getString("token")

  @Test
  @Order(1)
  fun `unauthorized without token`() {
    given()
      .accept(ContentType.JSON)
      .`when`()
      .get("/api/v1/tasks")
      .then()
      .statusCode(401)
  }

  @Test
  @Order(2)
  fun `create list get update delete task end-to-end`() {
    val token = loginAndGetToken()

    val id: String =
      given()
        .auth()
        .oauth2(token)
        .contentType(ContentType.JSON)
        .body("""{"title":"Buy milk","description":"2L whole"}""")
        .`when`()
        .post("/api/v1/tasks")
        .then()
        .statusCode(201)
        .body("id", not(equalTo("")))
        .body("title", equalTo("Buy milk"))
        .body("status", equalTo("PENDING"))
        .extract()
        .jsonPath()
        .getString("id")

    val listJson =
      given()
        .auth()
        .oauth2(token)
        .accept(ContentType.JSON)
        .`when`()
        .get("/api/v1/tasks?page=1&size=20")
        .then()
        .statusCode(200)
        .body("total", greaterThanOrEqualTo(1))
        .extract()
        .jsonPath()

    val ids: List<String> = listJson.getList("items.id")
    Assertions.assertTrue(ids.contains(id))

    given()
      .auth()
      .oauth2(token)
      .accept(ContentType.JSON)
      .`when`()
      .get("/api/v1/tasks/$id")
      .then()
      .statusCode(200)
      .body("id", equalTo(id))
      .body("description", equalTo("2L whole"))

    given()
      .auth()
      .oauth2(token)
      .contentType(ContentType.JSON)
      .body("""{"title":"  Buy milk & bread  ","description":"  skim  ","status":"IN_PROGRESS"}""")
      .`when`()
      .put("/api/v1/tasks/$id")
      .then()
      .statusCode(204)

    given()
      .auth()
      .oauth2(token)
      .accept(ContentType.JSON)
      .`when`()
      .get("/api/v1/tasks/$id")
      .then()
      .statusCode(200)
      .body("title", equalTo("Buy milk & bread"))
      .body("description", equalTo("skim"))
      .body("status", equalTo("IN_PROGRESS"))

    given()
      .auth()
      .oauth2(token)
      .`when`()
      .delete("/api/v1/tasks/$id")
      .then()
      .statusCode(204)

    given()
      .auth()
      .oauth2(token)
      .accept(ContentType.JSON)
      .`when`()
      .get("/api/v1/tasks/$id")
      .then()
      .statusCode(404)
  }

  @Test
  @Order(3)
  fun `filter by status and keyword, sort, and pagination`() {
    val token = loginAndGetToken()

    fun create(
      title: String,
      desc: String? = null,
    ): String =
      given()
        .auth()
        .oauth2(token)
        .contentType(ContentType.JSON)
        .body("""{"title":"$title","description":${if (desc == null) "null" else "\"$desc\""}}""")
        .`when`()
        .post("/api/v1/tasks")
        .then()
        .statusCode(201)
        .extract()
        .jsonPath()
        .getString("id")

    create("alpha task", "first")
    Thread.sleep(5)
    val b = create("beta task", "milk inside")
    Thread.sleep(5)
    create("gamma task", "zzz")

    given()
      .auth()
      .oauth2(token)
      .contentType(ContentType.JSON)
      .body("""{"status":"COMPLETED"}""")
      .`when`()
      .put("/api/v1/tasks/$b")
      .then()
      .statusCode(204)

    val completedJson =
      given()
        .auth()
        .oauth2(token)
        .accept(ContentType.JSON)
        .`when`()
        .get("/api/v1/tasks?status=COMPLETED")
        .then()
        .statusCode(200)
        .extract()
        .jsonPath()

    val completedIds: List<String> = completedJson.getList("items.id")
    Assertions.assertTrue(completedIds.contains(b))

    val milkJson =
      given()
        .auth()
        .oauth2(token)
        .accept(ContentType.JSON)
        .`when`()
        .get("/api/v1/tasks?q=Milk")
        .then()
        .statusCode(200)
        .extract()
        .jsonPath()

    val milkIds: List<String> = milkJson.getList("items.id")
    Assertions.assertTrue(milkIds.contains(b))

    given()
      .auth()
      .oauth2(token)
      .accept(ContentType.JSON)
      .`when`()
      .get("/api/v1/tasks?sort=NOT_A_SORT") // should default to UPDATED_DESC
      .then()
      .statusCode(200)

    val page1 =
      given()
        .auth()
        .oauth2(token)
        .accept(ContentType.JSON)
        .`when`()
        .get("/api/v1/tasks?page=1&size=2&sort=CREATED_ASC")
        .then()
        .statusCode(200)
        .body("page", equalTo(1))
        .body("size", equalTo(2))
        .extract()
        .jsonPath()

    val page1Count: Int = page1.getList<Any>("items").size
    Assertions.assertTrue(page1Count <= 2)
  }

  @Test
  fun `missing or bad token returns 401`() {
    given()
      .auth()
      .oauth2("malformed-or-expired")
      .accept(ContentType.JSON)
      .`when`()
      .get("/api/v1/tasks")
      .then()
      .statusCode(401)
  }
}
