package com.serhii.taskmanager.application.usecase

import com.serhii.taskmanager.common.NotFoundException
import com.serhii.taskmanager.domain.model.Task
import com.serhii.taskmanager.domain.model.TaskId
import com.serhii.taskmanager.domain.repo.TaskRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.UUID

class DeleteTaskUseCaseTest {
  private val repo: TaskRepository = mockk()
  private val uc = DeleteTaskUseCase(repo)

  @Test
  fun `deletes when task exists and is owned by requester`() {
    val id = UUID.randomUUID()
    val task = Task(ownerId = "u1", title = "Will delete")
    every { repo.findById(TaskId(id)) } returns task
    every { repo.delete(TaskId(id)) } just Runs

    uc.execute(ownerId = "u1", rawId = id)

    verify(exactly = 1) { repo.findById(TaskId(id)) }
    verify(exactly = 1) { repo.delete(TaskId(id)) }
  }

  @Test
  fun `throws NotFound when task does not exist`() {
    val id = UUID.randomUUID()
    every { repo.findById(TaskId(id)) } returns null

    assertThrows(NotFoundException::class.java) {
      uc.execute(ownerId = "u1", rawId = id)
    }

    verify(exactly = 1) { repo.findById(TaskId(id)) }
    verify(exactly = 0) { repo.delete(any()) }
  }

  @Test
  fun `throws NotFound and does not delete when owned by someone else`() {
    val id = UUID.randomUUID()
    val task = Task(ownerId = "other", title = "Nope")
    every { repo.findById(TaskId(id)) } returns task

    assertThrows(NotFoundException::class.java) {
      uc.execute(ownerId = "u1", rawId = id)
    }

    verify(exactly = 1) { repo.findById(TaskId(id)) }
    verify(exactly = 0) { repo.delete(any()) }
  }
}
