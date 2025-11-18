package com.serhii.taskmanager.domain.model

data class User(
  val id: String,
  val username: String,
  val roles: Set<String>,
)
