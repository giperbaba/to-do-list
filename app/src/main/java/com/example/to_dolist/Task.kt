package com.example.to_dolist

import java.util.UUID

class Task(
    var description: String,
    var isDone: Boolean = false,
    val id: String = UUID.randomUUID().toString()
)