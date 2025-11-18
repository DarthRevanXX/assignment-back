package com.serhii.taskmanager.application.usecase

import com.serhii.taskmanager.common.requireNonBlank
import com.serhii.taskmanager.domain.model.Task
import com.serhii.taskmanager.domain.repo.TaskRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CreateTaskUseCase(
  private val repo: TaskRepository,
) {
  fun execute(
    ownerId: String,
    title: String,
    description: String?,
  ): Task {
    requireNonBlank(title, "title")
    val task =
      Task(ownerId = ownerId, title = title.trim(), description = description?.takeIf { it.isNotBlank() }?.trim())
    return repo.create(task)
  }
}
