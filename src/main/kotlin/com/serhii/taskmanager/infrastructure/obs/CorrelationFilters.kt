package com.serhii.taskmanager.infrastructure.obs

import jakarta.annotation.Priority
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.Priorities
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.ContainerResponseContext
import jakarta.ws.rs.container.ContainerResponseFilter
import jakarta.ws.rs.ext.Provider
import org.jboss.logging.MDC
import java.util.UUID

private const val HDR = "X-Request-ID"

@Provider
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
class CorrelationRequestFilter
  @Inject
  constructor(
    private val corr: CorrelationId,
  ) : ContainerRequestFilter {
    override fun filter(requestContext: ContainerRequestContext) {
      val incoming = requestContext.headers.getFirst(HDR)
      val id = incoming ?: UUID.randomUUID().toString()
      corr.value = id
      MDC.put("requestId", id)
    }
  }

@Provider
@ApplicationScoped
class CorrelationResponseFilter
  @Inject
  constructor(
    private val corr: CorrelationId,
  ) : ContainerResponseFilter {
    override fun filter(
      requestContext: ContainerRequestContext,
      responseContext: ContainerResponseContext,
    ) {
      if (!responseContext.headers.containsKey(HDR)) {
        responseContext.headers.add(HDR, corr.value)
      }
      MDC.remove("requestId")
    }
  }
