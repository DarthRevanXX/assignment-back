package com.serhii.taskmanager.application.dto

import com.serhii.taskmanager.domain.model.Task
import com.serhii.taskmanager.domain.model.TaskStatus
import jakarta.validation.constraints.NotBlank
import java.time.Instant
import java.util.UUID

data class CreateTaskRequest(
  @field:NotBlank
  val title: String,
  val description: String? = null,
)

data class UpdateTaskRequest(
  val title: String? = null,
  val description: String? = null,
  val status: TaskStatus? = null,
)

data class TaskResponse(
  val id: UUID,
  val title: String,
  val description: String?,
  val status: TaskStatus,
  val createdAt: Instant,
  val updatedAt: Instant,
)

data class PagedTasksResponse(
  val items: List<TaskResponse>,
  val total: Long,
  val page: Int,
  val size: Int,
)

fun Task.toResponse() =
  TaskResponse(
    id = this.id.value,
    title = this.title,
    description = this.description,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
  )
