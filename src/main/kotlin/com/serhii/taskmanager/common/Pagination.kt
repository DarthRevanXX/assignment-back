package com.serhii.taskmanager.common

data class PageRequest(
  val page: Int = 1,
  val size: Int = 20,
) {
  init {
    require(page >= 1) { "page must be >= 1" }
    require(size in 1..100) { "size must be in 1..100" }
  }

  companion object {
    fun fromQuery(
      page: Int?,
      size: Int?,
      maxSize: Int = 100,
    ): PageRequest {
      val p = (page ?: 1).coerceAtLeast(1)
      val s = (size ?: 20).coerceIn(1, maxSize)
      return PageRequest(p, s)
    }
  }
}

data class Page<T>(
  val items: List<T>,
  val total: Long,
  val page: Int,
  val size: Int,
)
