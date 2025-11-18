package com.serhii.taskmanager.domain.model

import com.serhii.taskmanager.common.ConflictException

class InvalidStateTransitionException(
  val from: TaskStatus,
  val to: TaskStatus,
) : ConflictException("Invalid status transition. This action is not allowed for the current task status.") {
  override fun logDetails(correlationId: String): String =
    "Invalid state transition attempted: $from -> $to (correlationId: $correlationId)"
}

class TaskAlreadyCompletedException(
  val taskId: String,
) : ConflictException("This task is already completed and cannot be modified.") {
  override fun logDetails(correlationId: String): String =
    "Attempted to modify completed task: $taskId (correlationId: $correlationId)"
}
