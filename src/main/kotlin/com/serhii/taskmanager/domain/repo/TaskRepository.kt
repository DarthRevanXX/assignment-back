package com.serhii.taskmanager.domain.repo

import com.serhii.taskmanager.domain.model.Task
import com.serhii.taskmanager.domain.model.TaskId
import com.serhii.taskmanager.domain.model.TaskStatus

interface TaskRepository {
  fun create(task: Task): Task

  fun findById(id: TaskId): Task?

  fun findAllByOwner(
    ownerId: String,
    status: TaskStatus?,
    keyword: String?,
    offset: Int,
    limit: Int,
    sort: String = "-createdAt",
  ): Pair<List<Task>, Long>

  fun update(task: Task)

  fun delete(id: TaskId)
}
