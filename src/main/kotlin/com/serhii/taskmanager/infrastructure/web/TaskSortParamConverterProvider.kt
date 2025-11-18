package com.serhii.taskmanager.infrastructure.web

import com.serhii.taskmanager.domain.model.TaskSort
import jakarta.ws.rs.ext.ParamConverter
import jakarta.ws.rs.ext.ParamConverterProvider
import jakarta.ws.rs.ext.Provider
import java.lang.reflect.Type

@Provider
class TaskSortParamConverterProvider : ParamConverterProvider {
  @Suppress("UNCHECKED_CAST")
  override fun <T : Any?> getConverter(
    rawType: Class<T>,
    genericType: Type?,
    annotations: Array<out Annotation?>?,
  ): ParamConverter<T>? {
    if (rawType != TaskSort::class.java) return null
    return object : ParamConverter<TaskSort> {
      override fun fromString(value: String?): TaskSort = TaskSort.fromStringSafe(value)

      override fun toString(value: TaskSort?): String? = value?.name
    } as ParamConverter<T>
  }
}
