package com.serhii.taskmanager.infrastructure.http

import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerResponseContext
import jakarta.ws.rs.container.ContainerResponseFilter
import jakarta.ws.rs.ext.Provider

/**
 * Add minimal security headers. In prod, use a proxy (NGINX/Envoy) for full headers.
 */
@Provider
class SecurityHeaders : ContainerResponseFilter {
  override fun filter(
    requestContext: ContainerRequestContext?,
    responseContext: ContainerResponseContext,
  ) {
    responseContext.headers["X-Content-Type-Options"] = listOf("nosniff")
    responseContext.headers["X-Frame-Options"] = listOf("DENY")
    responseContext.headers["Referrer-Policy"] = listOf("no-referrer")
  }
}
