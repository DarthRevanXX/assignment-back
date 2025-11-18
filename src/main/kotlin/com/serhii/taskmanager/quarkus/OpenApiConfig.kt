package com.serhii.taskmanager.quarkus

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType
import org.eclipse.microprofile.openapi.annotations.info.Info
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme

@OpenAPIDefinition(
  info = Info(title = "Task Manager API", version = "1.0.0", description = "Personal task management API"),
)
@SecurityScheme(
  securitySchemeName = "jwt",
  type = SecuritySchemeType.HTTP,
  scheme = "bearer",
  bearerFormat = "JWT",
)
class OpenApiConfig
