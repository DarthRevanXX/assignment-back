package com.serhii.taskmanager.infrastructure.persistence

import com.serhii.taskmanager.domain.model.Task
import com.serhii.taskmanager.domain.model.TaskId
import com.serhii.taskmanager.domain.model.TaskStatus
import com.serhii.taskmanager.domain.repo.TaskRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.concurrent.ConcurrentHashMap

@ApplicationScoped
class InMemoryTaskRepository : TaskRepository {
  private val data = ConcurrentHashMap<TaskId, Task>()

  override fun create(task: Task): Task {
    data[task.id] = task
    return task
  }

  override fun update(task: Task) {
    data.compute(task.id) { _, _ -> task }
  }

  override fun findById(id: TaskId): Task? = data[id]

  override fun findAllByOwner(
    ownerId: String,
    status: TaskStatus?,
    keyword: String?,
    offset: Int,
    limit: Int,
    sort: String,
  ): Pair<List<Task>, Long> {
    var seq = data.values.asSequence().filter { it.ownerId == ownerId }
    if (status != null) seq = seq.filter { it.status == status }
    if (!keyword.isNullOrBlank()) {
      val q = keyword.trim().lowercase()
      seq = seq.filter { it.title.lowercase().contains(q) || (it.description?.lowercase()?.contains(q) == true) }
    }
    val sorted =
      when (sort) {
        "createdAt" -> seq.sortedWith(compareBy<Task> { it.createdAt }.thenBy { it.id.value })
        "-createdAt" -> seq.sortedWith(compareByDescending<Task> { it.createdAt }.thenBy { it.id.value })
        "updatedAt" -> seq.sortedWith(compareBy<Task> { it.updatedAt }.thenBy { it.id.value })
        else -> seq.sortedWith(compareByDescending<Task> { it.updatedAt }.thenBy { it.id.value })
      }.toList()
    val total = sorted.size.toLong()
    val page = sorted.drop(offset).take(limit)
    return page to total
  }

  override fun delete(id: TaskId) {
    data.remove(id)
  }
}
