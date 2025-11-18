package com.serhii.taskmanager.application.usecase

import com.serhii.taskmanager.common.NotFoundException
import com.serhii.taskmanager.domain.model.TaskId
import com.serhii.taskmanager.domain.repo.TaskRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class DeleteTaskUseCase(
  private val repo: TaskRepository,
) {
  fun execute(
    ownerId: String,
    rawId: UUID,
  ) {
    val id = TaskId(rawId)
    val existing = repo.findById(id) ?: throw NotFoundException("Task not found")
    if (existing.ownerId != ownerId) throw NotFoundException("Task not found")
    repo.delete(id)
  }
}
