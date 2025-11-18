package com.serhii.taskmanager.domain.service

import com.serhii.taskmanager.domain.model.InvalidStateTransitionException
import com.serhii.taskmanager.domain.model.Task
import com.serhii.taskmanager.domain.model.TaskAlreadyCompletedException
import com.serhii.taskmanager.domain.model.TaskStatus
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class TaskDomainService {
  fun canTransition(
    from: TaskStatus,
    to: TaskStatus,
  ): Boolean {
    if (from == to) return true
    return when (from) {
      TaskStatus.PENDING -> to == TaskStatus.IN_PROGRESS || to == TaskStatus.COMPLETED
      TaskStatus.IN_PROGRESS -> to == TaskStatus.COMPLETED || to == TaskStatus.PENDING
      TaskStatus.COMPLETED -> to == TaskStatus.IN_PROGRESS
    }
  }

  fun applyTransition(
    task: Task,
    to: TaskStatus,
  ) {
    if (!canTransition(task.status, to)) {
      throw InvalidStateTransitionException(task.status, to)
    }
    task.moveTo(to)
  }

  fun validateCanModify(task: Task) {
    if (task.status == TaskStatus.COMPLETED) {
      throw TaskAlreadyCompletedException(task.id.value.toString())
    }
  }
}
