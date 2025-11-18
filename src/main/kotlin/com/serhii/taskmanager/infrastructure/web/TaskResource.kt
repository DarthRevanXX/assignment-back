package com.serhii.taskmanager.infrastructure.web

import com.serhii.taskmanager.application.dto.CreateTaskRequest
import com.serhii.taskmanager.application.dto.PagedTasksResponse
import com.serhii.taskmanager.application.dto.TaskResponse
import com.serhii.taskmanager.application.dto.UpdateTaskRequest
import com.serhii.taskmanager.application.dto.toResponse
import com.serhii.taskmanager.application.usecase.CreateTaskUseCase
import com.serhii.taskmanager.application.usecase.DeleteTaskUseCase
import com.serhii.taskmanager.application.usecase.GetTaskUseCase
import com.serhii.taskmanager.application.usecase.ListTasksUseCase
import com.serhii.taskmanager.application.usecase.UpdateTaskUseCase
import com.serhii.taskmanager.common.PageRequest
import com.serhii.taskmanager.common.UnauthorizedException
import com.serhii.taskmanager.domain.model.TaskSort
import com.serhii.taskmanager.domain.model.TaskStatus
import jakarta.annotation.security.DenyAll
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.DefaultValue
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
import java.util.UUID

@Path("/api/v1/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@DenyAll
class TaskResource
  @Inject
  constructor(
    private val createTask: CreateTaskUseCase,
    private val updateTask: UpdateTaskUseCase,
    private val getTask: GetTaskUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val listTasks: ListTasksUseCase,
    private val jwt: JsonWebToken,
  ) {
    private fun ownerId(): String = jwt.subject ?: throw UnauthorizedException("Authentication required")

    @RolesAllowed("USER")
    @GET
    @Operation(
      summary = "List tasks",
      description =
        "Returns a paginated list of tasks owned by the current user." +
          " Supports status filtering, keyword search, and sorting.",
    )
    fun list(
      @QueryParam("status") status: TaskStatus?,
      @Parameter(
        description = "Case-insensitive substring search over title and description.",
        example = "milk",
      )
      @QueryParam("q") q: String?,
      @Parameter(
        description = "Sort order",
        schema =
          Schema(
            implementation = String::class,
            enumeration = ["CREATED_ASC", "CREATED_DESC", "UPDATED_ASC", "UPDATED_DESC"],
          ),
      )
      @QueryParam("sort")
      @DefaultValue("UPDATED_DESC") sort: TaskSort,
      @QueryParam("page") @DefaultValue("1") @Min(1) page: Int,
      @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) size: Int,
    ): PagedTasksResponse {
      val result = listTasks.execute(ownerId(), status, q, sort, PageRequest(page, size))
      return PagedTasksResponse(
        items = result.items.map { it.toResponse() },
        total = result.total,
        page = result.page,
        size = result.size,
      )
    }

    @RolesAllowed("USER")
    @POST
    @Operation(
      summary = "Create a task",
      description = "Creates a new task owned by the current user.",
    )
    fun create(
      @Valid
      req: CreateTaskRequest,
    ): Response {
      val task = createTask.execute(ownerId(), req.title, req.description)
      return Response.status(Response.Status.CREATED).entity(task.toResponse()).build()
    }

    @RolesAllowed("USER")
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a task by id")
    fun update(
      @PathParam("id") id: UUID,
      @Valid req: UpdateTaskRequest,
    ): Response {
      updateTask.execute(ownerId(), id, req.title, req.description, req.status)
      return Response.noContent().build()
    }

    @RolesAllowed("USER")
    @GET
    @Path("/{id}")
    @Operation(summary = "Get task by id")
    fun get(
      @PathParam("id") id: UUID,
    ): TaskResponse = getTask.execute(ownerId(), id).toResponse()

    @RolesAllowed("USER")
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete task by id")
    fun delete(
      @PathParam("id") id: UUID,
    ): Response {
      deleteTask.execute(ownerId(), id)
      return Response.noContent().build()
    }
  }
