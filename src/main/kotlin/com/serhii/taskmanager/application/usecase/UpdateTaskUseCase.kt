package com.serhii.taskmanager.application.usecase

import com.serhii.taskmanager.common.BadRequestException
import com.serhii.taskmanager.common.NotFoundException
import com.serhii.taskmanager.domain.model.TaskId
import com.serhii.taskmanager.domain.model.TaskStatus
import com.serhii.taskmanager.domain.repo.TaskRepository
import com.serhii.taskmanager.domain.service.TaskDomainService
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class UpdateTaskUseCase(
  private val repo: TaskRepository,
  private val domain: TaskDomainService,
) {
  fun execute(
    ownerId: String,
    rawId: UUID,
    title: String?,
    description: String?,
    status: TaskStatus?,
  ) {
    val id = TaskId(rawId)
    val task = repo.findById(id) ?: throw NotFoundException("Task not found")
    if (task.ownerId != ownerId) throw NotFoundException("Task not found")

    val titleChanged = title != null && title.trim() != task.title
    val descriptionChanged =
      description != null &&
        description.takeIf { it.isNotBlank() }?.trim() != task.description

    if (titleChanged || descriptionChanged) {
      domain.validateCanModify(task)
    }

    if (title != null) {
      if (title.isBlank()) throw BadRequestException("Title is required and cannot be empty.")
      task.rename(title.trim())
    }
    if (description != null) task.changeDescription(description.takeIf { it.isNotBlank() }?.trim())
    if (status != null) domain.applyTransition(task, status)

    repo.update(task)
  }
}
