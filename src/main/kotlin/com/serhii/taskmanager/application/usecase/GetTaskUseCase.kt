package com.serhii.taskmanager.application.usecase

import com.serhii.taskmanager.common.NotFoundException
import com.serhii.taskmanager.domain.model.Task
import com.serhii.taskmanager.domain.model.TaskId
import com.serhii.taskmanager.domain.repo.TaskRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class GetTaskUseCase(
  private val repo: TaskRepository,
) {
  fun execute(
    ownerId: String,
    rawId: UUID,
  ): Task {
    val task = repo.findById(TaskId(rawId)) ?: throw NotFoundException("Task not found")
    if (task.ownerId != ownerId) throw NotFoundException("Task not found")
    return task
  }
}
