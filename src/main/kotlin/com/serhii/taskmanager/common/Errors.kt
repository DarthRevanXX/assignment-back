package com.serhii.taskmanager.common

sealed class AppException(
  message: String,
) : RuntimeException(message) {
  open fun logDetails(correlationId: String): String = message ?: "No details"
}

class NotFoundException(
  message: String,
) : AppException(message)

class ForbiddenException(
  message: String,
) : AppException(message)

class BadRequestException(
  message: String,
) : AppException(message)

class UnauthorizedException(
  message: String,
) : AppException(message)

open class ConflictException(
  message: String,
) : AppException(message)
