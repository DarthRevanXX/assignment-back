package com.serhii.taskmanager.domain.service

import com.serhii.taskmanager.domain.model.Task
import com.serhii.taskmanager.domain.model.TaskStatus
import com.serhii.taskmanager.domain.service.TaskDomainService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class TaskDomainServiceTest {
  private val service = TaskDomainService()

  @Test
  fun `canTransition valid paths`() {
    Assertions.assertThat(service.canTransition(TaskStatus.PENDING, TaskStatus.IN_PROGRESS)).isTrue()
    Assertions.assertThat(service.canTransition(TaskStatus.IN_PROGRESS, TaskStatus.COMPLETED)).isTrue()
    Assertions.assertThat(service.canTransition(TaskStatus.COMPLETED, TaskStatus.IN_PROGRESS)).isTrue()
  }

  @Test
  fun `applyTransition updates status`() {
    val t = Task(ownerId = "u1", title = "T1")
    service.applyTransition(t, TaskStatus.IN_PROGRESS)
    Assertions.assertThat(t.status).isEqualTo(TaskStatus.IN_PROGRESS)
  }

  @Test
  fun `applyTransition rejects illegal`() {
    val t = Task(ownerId = "u1", title = "T1")
    service.applyTransition(t, TaskStatus.PENDING)
    Assertions.assertThat(t.status).isEqualTo(TaskStatus.PENDING)
  }
}
