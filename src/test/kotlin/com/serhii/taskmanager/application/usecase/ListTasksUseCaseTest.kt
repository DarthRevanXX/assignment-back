package com.serhii.taskmanager.application.usecase

import com.serhii.taskmanager.application.usecase.ListTasksUseCase
import com.serhii.taskmanager.common.PageRequest
import com.serhii.taskmanager.domain.model.Task
import com.serhii.taskmanager.domain.model.TaskId
import com.serhii.taskmanager.domain.model.TaskSort
import com.serhii.taskmanager.domain.model.TaskStatus
import com.serhii.taskmanager.domain.repo.TaskRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.util.UUID

class ListTasksUseCaseTest {
  private val repo = mockk<TaskRepository>()
  private val uc = ListTasksUseCase(repo)

  @Test
  fun `delegates to repo with correct offset and limit`() {
    val page = PageRequest(page = 2, size = 10) // offset=10
    val tasks = (1..3).map { Task(id = TaskId(UUID.randomUUID()), ownerId = "u1", title = "t$it") }
    every { repo.findAllByOwner("u1", TaskStatus.PENDING, "q", 10, 10, TaskSort.CREATED_ASC.repoKey) } returns
      (tasks to 42L)

    val res = uc.execute("u1", TaskStatus.PENDING, "q", TaskSort.CREATED_ASC, page)

    Assertions.assertThat(res.items).hasSize(3)
    Assertions.assertThat(res.total).isEqualTo(42)
    Assertions.assertThat(res.page).isEqualTo(2)
    Assertions.assertThat(res.size).isEqualTo(10)

    verify(exactly = 1) { repo.findAllByOwner("u1", TaskStatus.PENDING, "q", 10, 10, TaskSort.CREATED_ASC.repoKey) }
  }
}
