package com.serhii.taskmanager.application.usecase

import com.serhii.taskmanager.common.NotFoundException
import com.serhii.taskmanager.domain.model.Task
import com.serhii.taskmanager.domain.model.TaskId
import com.serhii.taskmanager.domain.repo.TaskRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.UUID

class GetTaskUseCaseTest {
  private val repo: TaskRepository = mockk()
  private val uc = GetTaskUseCase(repo)

  @Test
  fun `returns task when found and owned by requester`() {
    val id = UUID.randomUUID()
    val task = Task(ownerId = "u1", title = "T")
    every { repo.findById(TaskId(id)) } returns task

    val result = uc.execute(ownerId = "u1", rawId = id)

    assertThat(result).isSameAs(task)
    verify(exactly = 1) { repo.findById(TaskId(id)) }
  }

  @Test
  fun `throws NotFound when task does not exist`() {
    val id = UUID.randomUUID()
    every { repo.findById(TaskId(id)) } returns null

    assertThrows(NotFoundException::class.java) {
      uc.execute(ownerId = "u1", rawId = id)
    }

    verify(exactly = 1) { repo.findById(TaskId(id)) }
  }

  @Test
  fun `throws NotFound when task belongs to another user`() {
    val id = UUID.randomUUID()
    val task = Task(ownerId = "other", title = "X")
    every { repo.findById(TaskId(id)) } returns task

    assertThrows(NotFoundException::class.java) {
      uc.execute(ownerId = "u1", rawId = id)
    }

    verify(exactly = 1) { repo.findById(TaskId(id)) }
  }
}
