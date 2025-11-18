package com.serhii.taskmanager.domain.model

import java.time.Instant
import java.util.UUID

data class TaskId(
  val value: UUID,
) {
  companion object {
    fun newId() = TaskId(UUID.randomUUID())
  }
}

enum class TaskStatus { PENDING, IN_PROGRESS, COMPLETED }

data class Task(
  val id: TaskId = TaskId.newId(),
  val ownerId: String,
  var title: String,
  var description: String? = null,
  var status: TaskStatus = TaskStatus.PENDING,
  val createdAt: Instant = Instant.now(),
  var updatedAt: Instant = Instant.now(),
) {
  fun rename(newTitle: String) {
    require(newTitle.isNotBlank()) { "title must not be blank" }
    title = newTitle
    touch()
  }

  fun changeDescription(desc: String?) {
    description = desc
    touch()
  }

  fun moveTo(status: TaskStatus) {
    this.status = status
    touch()
  }

  private fun touch() {
    updatedAt = Instant.now()
  }
}

enum class TaskSort(
  val repoKey: String,
) {
  CREATED_ASC("createdAt"),
  CREATED_DESC("-createdAt"),
  UPDATED_ASC("updatedAt"),
  UPDATED_DESC("-updatedAt"),
  ;

  companion object {
    fun fromStringSafe(value: String?): TaskSort =
      entries.firstOrNull { it.name.equals(value ?: "", ignoreCase = true) } ?: UPDATED_DESC
  }
}
