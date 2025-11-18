package com.serhii.taskmanager.application.usecase

import com.serhii.taskmanager.application.usecase.UpdateTaskUseCase
import com.serhii.taskmanager.common.BadRequestException
import com.serhii.taskmanager.common.NotFoundException
import com.serhii.taskmanager.domain.model.Task
import com.serhii.taskmanager.domain.model.TaskAlreadyCompletedException
import com.serhii.taskmanager.domain.model.TaskId
import com.serhii.taskmanager.domain.model.TaskStatus
import com.serhii.taskmanager.domain.repo.TaskRepository
import com.serhii.taskmanager.domain.service.TaskDomainService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.UUID

class UpdateTaskUseCaseTest {
  private val repo: TaskRepository = mockk()
  private val domain: TaskDomainService = mockk()
  private val uc = UpdateTaskUseCase(repo, domain)

  @Test
  fun `throws NotFound when task does not exist`() {
    val id = UUID.randomUUID()
    every { repo.findById(TaskId(id)) } returns null

    assertThrows(NotFoundException::class.java) {
      uc.execute(ownerId = "u1", rawId = id, title = null, description = null, status = null)
    }

    verify(exactly = 1) { repo.findById(TaskId(id)) }
    verify(exactly = 0) { repo.update(any()) }
  }

  @Test
  fun `throws NotFound when task belongs to another owner`() {
    val id = UUID.randomUUID()
    val task = Task(ownerId = "other", title = "t")
    every { repo.findById(TaskId(id)) } returns task

    assertThrows(NotFoundException::class.java) {
      uc.execute(ownerId = "u1", rawId = id, title = null, description = null, status = null)
    }

    verify(exactly = 1) { repo.findById(TaskId(id)) }
    verify(exactly = 0) { repo.update(any()) }
  }

  @Test
  fun `valid update renames, trims, transitions and persists`() {
    val id = UUID.randomUUID()
    val task = Task(ownerId = "u1", title = "Old", description = "x", status = TaskStatus.PENDING)

    every { repo.findById(TaskId(id)) } returns task
    every { domain.validateCanModify(task) } just Runs
    every { domain.applyTransition(any(), any()) } answers {
      task.moveTo(TaskStatus.IN_PROGRESS)
    }

    every { repo.update(any()) } just Runs

    uc.execute(
      ownerId = "u1",
      rawId = id,
      title = "  New Title  ",
      description = "  new desc  ",
      status = TaskStatus.IN_PROGRESS,
    )

    assertThat(task.title).isEqualTo("New Title")
    assertThat(task.description).isEqualTo("new desc")
    assertThat(task.status).isEqualTo(TaskStatus.IN_PROGRESS)

    verifyOrder {
      repo.findById(TaskId(id))
      domain.validateCanModify(task)
      domain.applyTransition(task, TaskStatus.IN_PROGRESS)
      repo.update(
        match {
          it.ownerId == "u1" &&
            it.title == "New Title" &&
            it.description == "new desc" &&
            it.status == TaskStatus.IN_PROGRESS
        },
      )
    }
    verify(exactly = 0) { domain.applyTransition(task, TaskStatus.PENDING) }
  }

  @Test
  fun `blank title is rejected and no update is persisted`() {
    val id = UUID.randomUUID()
    val task = Task(ownerId = "u1", title = "Old")
    every { repo.findById(TaskId(id)) } returns task
    every { domain.validateCanModify(task) } just Runs

    assertThrows(BadRequestException::class.java) {
      uc.execute(ownerId = "u1", rawId = id, title = "   ", description = null, status = null)
    }

    verify(exactly = 1) { repo.findById(TaskId(id)) }
    verify(exactly = 1) { domain.validateCanModify(task) }
    verify(exactly = 0) { repo.update(any()) }
  }

  @Test
  fun `description blank becomes null`() {
    val id = UUID.randomUUID()
    val task = Task(ownerId = "u1", title = "Old", description = "keep")
    every { repo.findById(TaskId(id)) } returns task
    every { domain.validateCanModify(task) } just Runs
    every { repo.update(task) } just Runs

    uc.execute(ownerId = "u1", rawId = id, title = null, description = "   ", status = null)

    assertThat(task.description).isNull()
    verify { repo.update(task) }
  }

  @Test
  fun `throws TaskAlreadyCompletedException when trying to modify completed task title`() {
    val id = UUID.randomUUID()
    val task = Task(ownerId = "u1", title = "Old", status = TaskStatus.COMPLETED)
    every { repo.findById(TaskId(id)) } returns task
    every { domain.validateCanModify(task) } throws TaskAlreadyCompletedException(task.id.value.toString())

    assertThrows(TaskAlreadyCompletedException::class.java) {
      uc.execute(ownerId = "u1", rawId = id, title = "New Title", description = null, status = null)
    }

    verify(exactly = 1) { repo.findById(TaskId(id)) }
    verify(exactly = 1) { domain.validateCanModify(task) }
    verify(exactly = 0) { repo.update(any()) }
  }

  @Test
  fun `throws TaskAlreadyCompletedException when trying to modify completed task description`() {
    val id = UUID.randomUUID()
    val task = Task(ownerId = "u1", title = "Task", status = TaskStatus.COMPLETED)
    every { repo.findById(TaskId(id)) } returns task
    every { domain.validateCanModify(task) } throws TaskAlreadyCompletedException(task.id.value.toString())

    assertThrows(TaskAlreadyCompletedException::class.java) {
      uc.execute(ownerId = "u1", rawId = id, title = null, description = "New Desc", status = null)
    }

    verify(exactly = 1) { domain.validateCanModify(task) }
    verify(exactly = 0) { repo.update(any()) }
  }

  @Test
  fun `allows status change on completed task without validation`() {
    val id = UUID.randomUUID()
    val task = Task(ownerId = "u1", title = "Task", status = TaskStatus.COMPLETED)
    every { repo.findById(TaskId(id)) } returns task
    every { domain.applyTransition(task, TaskStatus.IN_PROGRESS) } just Runs
    every { repo.update(task) } just Runs

    uc.execute(ownerId = "u1", rawId = id, title = null, description = null, status = TaskStatus.IN_PROGRESS)

    verify(exactly = 0) { domain.validateCanModify(any()) }
    verify(exactly = 1) { domain.applyTransition(task, TaskStatus.IN_PROGRESS) }
    verify(exactly = 1) { repo.update(task) }
  }

  @Test
  fun `allows status change on completed task even when sending same title and description`() {
    val id = UUID.randomUUID()
    val task = Task(ownerId = "u1", title = "Task", description = "Desc", status = TaskStatus.COMPLETED)
    every { repo.findById(TaskId(id)) } returns task
    every { domain.applyTransition(task, TaskStatus.IN_PROGRESS) } just Runs
    every { repo.update(task) } just Runs

    uc.execute(
      ownerId = "u1",
      rawId = id,
      title = "Task",
      description = "Desc",
      status = TaskStatus.IN_PROGRESS,
    )

    verify(exactly = 0) { domain.validateCanModify(any()) }
    verify(exactly = 1) { domain.applyTransition(task, TaskStatus.IN_PROGRESS) }
    verify(exactly = 1) { repo.update(task) }
  }
}
