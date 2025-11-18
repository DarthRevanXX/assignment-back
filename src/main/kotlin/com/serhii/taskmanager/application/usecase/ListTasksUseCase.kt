package com.serhii.taskmanager.application.usecase

import com.serhii.taskmanager.common.Page
import com.serhii.taskmanager.common.PageRequest
import com.serhii.taskmanager.domain.model.Task
import com.serhii.taskmanager.domain.model.TaskSort
import com.serhii.taskmanager.domain.model.TaskStatus
import com.serhii.taskmanager.domain.repo.TaskRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ListTasksUseCase(
  private val repo: TaskRepository,
) {
  fun execute(
    ownerId: String,
    status: TaskStatus?,
    keyword: String?,
    sort: TaskSort,
    rawPage: PageRequest,
  ): Page<Task> {
    val page = PageRequest.fromQuery(rawPage.page, rawPage.size)
    val offsetLong = (page.page - 1).toLong() * page.size.toLong()
    val offset = if (offsetLong > Int.MAX_VALUE) Int.MAX_VALUE else offsetLong.toInt()

    val (items, total) = repo.findAllByOwner(ownerId, status, keyword, offset, page.size, sort.repoKey)
    if (offset >= total.coerceAtLeast(0).toInt()) {
      return Page(emptyList(), total, page.page, page.size)
    }
    return Page(items, total, page.page, page.size)
  }
}
