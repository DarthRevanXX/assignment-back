package com.serhii.taskmanager.application.usecase

import com.serhii.taskmanager.application.usecase.CreateTaskUseCase
import com.serhii.taskmanager.domain.model.Task
import com.serhii.taskmanager.domain.repo.TaskRepository
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class CreateTaskUseCaseTest {
  private val repo = mockk<TaskRepository>()
  private val uc = CreateTaskUseCase(repo)

  @Test
  fun `creates task and trims fields`() {
    val owner = "u1"
    val title = "  Hello  "
    val desc = "  world "

    every { repo.create(any()) } answers { firstArg<Task>() }

    val task = uc.execute(owner, title, desc)

    Assertions.assertThat(task.ownerId).isEqualTo("u1")
    Assertions.assertThat(task.title).isEqualTo("Hello")
    Assertions.assertThat(task.description).isEqualTo("world")

    verify(exactly = 1) {
      repo.create(
        withArg { t ->
          Assertions.assertThat(t.ownerId).isEqualTo("u1")
          Assertions.assertThat(t.title).isEqualTo("Hello")
          Assertions.assertThat(t.description).isEqualTo("world")
        },
      )
    }
    confirmVerified(repo)
  }
}
