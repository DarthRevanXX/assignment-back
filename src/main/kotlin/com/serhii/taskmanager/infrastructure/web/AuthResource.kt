package com.serhii.taskmanager.infrastructure.web

import com.serhii.taskmanager.application.dto.LoginRequest
import com.serhii.taskmanager.application.dto.TokenResponse
import com.serhii.taskmanager.application.usecase.LoginUseCase
import jakarta.annotation.security.PermitAll
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation

@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class AuthResource
  @Inject
  constructor(
    private val login: LoginUseCase,
  ) {
    @POST
    @Path("/login")
    @Operation(
      summary = "User login",
      description = "Authenticate user and return JWT token",
    )
    fun login(req: LoginRequest): Response {
      val tokenResponse: TokenResponse = login.execute(req)

      val cookie =
        NewCookie
          .Builder("auth_token")
          .value(tokenResponse.token)
          .path("/")
          .maxAge(tokenResponse.expiresInSeconds.toInt())
          .httpOnly(true)
          .secure(false)
          .sameSite(NewCookie.SameSite.LAX)
          .build()

      return Response
        .ok(tokenResponse)
        .cookie(cookie)
        .build()
    }

    @POST
    @Path("/logout")
    @PermitAll
    @Operation(
      summary = "User logout",
      description = "Clear auth cookie and allow client to forget JWT",
    )
    fun logout(): Response {
      val expiredCookie =
        NewCookie
          .Builder("auth_token")
          .value("")
          .path("/")
          .maxAge(0)
          .httpOnly(true)
          .secure(false)
          .sameSite(NewCookie.SameSite.LAX)
          .build()

      return Response
        .noContent()
        .cookie(expiredCookie)
        .build()
    }
  }
