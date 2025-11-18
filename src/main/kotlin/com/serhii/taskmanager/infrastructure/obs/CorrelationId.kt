package com.serhii.taskmanager.infrastructure.obs

import jakarta.enterprise.context.RequestScoped

@RequestScoped
class CorrelationId {
  var value: String = ""
}
