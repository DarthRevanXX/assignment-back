package com.serhii.taskmanager.infrastructure.health

import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.health.HealthCheck
import org.eclipse.microprofile.health.HealthCheckResponse
import org.eclipse.microprofile.health.Liveness
import org.eclipse.microprofile.health.Readiness

@Liveness
@ApplicationScoped
class LivenessCheck : HealthCheck {
  override fun call(): HealthCheckResponse = HealthCheckResponse.up("liveness")
}

@Readiness
@ApplicationScoped
class ReadinessCheck : HealthCheck {
  override fun call(): HealthCheckResponse {
    return HealthCheckResponse.up("readiness-inmemory")
    // For DB mode, inject DataSource and ping .isValid(2)
  }
}
