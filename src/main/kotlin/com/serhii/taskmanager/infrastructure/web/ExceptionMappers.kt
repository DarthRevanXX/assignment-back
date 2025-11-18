package com.serhii.taskmanager.infrastructure.web

import com.serhii.taskmanager.common.BadRequestException
import com.serhii.taskmanager.common.ConflictException
import com.serhii.taskmanager.common.ForbiddenException
import com.serhii.taskmanager.common.NotFoundException
import com.serhii.taskmanager.common.UnauthorizedException
import com.serhii.taskmanager.infrastructure.obs.CorrelationId
import io.quarkus.logging.Log
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import jakarta.ws.rs.NotFoundException as JaxRsNotFound

private fun problem(
  status: Response.Status,
  title: String,
  detail: String?,
  instance: String?,
  type: String = "about:blank",
): Response =
  Response
    .status(status)
    .type("application/problem+json")
    .entity(
      mapOf(
        "type" to type,
        "title" to title,
        "status" to status.statusCode,
        "detail" to (detail ?: title),
        "instance" to (instance ?: ""),
      ),
    ).build()

@Provider
class NotFoundMapper(
  private val corr: CorrelationId,
) : ExceptionMapper<NotFoundException> {
  override fun toResponse(e: NotFoundException): Response {
    Log.debugf(e.logDetails(corr.value))
    return problem(Response.Status.NOT_FOUND, "Not Found", e.message, "/problems/${corr.value}")
  }
}

@Provider
class ForbiddenMapper(
  private val corr: CorrelationId,
) : ExceptionMapper<ForbiddenException> {
  override fun toResponse(e: ForbiddenException): Response {
    Log.infof(e.logDetails(corr.value))
    return problem(Response.Status.FORBIDDEN, "Forbidden", e.message, "/problems/${corr.value}")
  }
}

@Provider
class BadRequestMapper(
  private val corr: CorrelationId,
) : ExceptionMapper<BadRequestException> {
  override fun toResponse(e: BadRequestException): Response {
    Log.debugf(e.logDetails(corr.value))
    return problem(Response.Status.BAD_REQUEST, "Bad Request", e.message, "/problems/${corr.value}")
  }
}

@Provider
class IllegalArgMapper(
  private val corr: CorrelationId,
) : ExceptionMapper<IllegalArgumentException> {
  override fun toResponse(e: IllegalArgumentException): Response {
    Log.debugf("IllegalArgumentException: %s (correlationId: %s)", e.message, corr.value)
    return problem(Response.Status.BAD_REQUEST, "Bad Request", e.message, "/problems/${corr.value}")
  }
}

@Provider
class UnauthorizedMapper(
  private val corr: CorrelationId,
) : ExceptionMapper<UnauthorizedException> {
  override fun toResponse(e: UnauthorizedException): Response {
    Log.warnf(e.logDetails(corr.value))
    return problem(Response.Status.UNAUTHORIZED, "Unauthorized", e.message, "/problems/${corr.value}")
  }
}

@Provider
class ConflictMapper(
  private val corr: CorrelationId,
) : ExceptionMapper<ConflictException> {
  override fun toResponse(e: ConflictException): Response {
    Log.warnf(e.logDetails(corr.value))
    return problem(Response.Status.CONFLICT, "Conflict", e.message, "/problems/${corr.value}")
  }
}

@Provider
class JaxRsNotFoundMapper(
  private val corr: CorrelationId,
) : ExceptionMapper<JaxRsNotFound> {
  override fun toResponse(e: JaxRsNotFound): Response =
    problem(
      status = Response.Status.NOT_FOUND,
      title = "Not Found",
      detail = "Resource not found",
      instance = "/problems/${corr.value}",
    )
}

@Provider
class ThrowableMapper(
  private val corr: CorrelationId,
) : ExceptionMapper<Throwable> {
  override fun toResponse(e: Throwable): Response {
    Log.errorf(e, "Unhandled exception (correlationId: %s)", corr.value)
    return problem(
      Response.Status.INTERNAL_SERVER_ERROR,
      "Internal Server Error",
      "An unexpected error occurred. Please try again later.",
      "/problems/${corr.value}",
    )
  }
}
