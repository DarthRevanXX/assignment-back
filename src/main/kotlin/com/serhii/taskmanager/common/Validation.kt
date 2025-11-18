package com.serhii.taskmanager.common

fun requireNonBlank(
  value: String?,
  field: String,
) {
  if (value == null || value.isBlank()) {
    val fieldName = field.replaceFirstChar { it.uppercase() }
    throw BadRequestException("$fieldName is required and cannot be empty.")
  }
}
