package com.example.to_dolist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {
    val tasks = MutableLiveData<MutableList<Task>>(mutableListOf())

    fun addTask(task: Task) {
        val currentTasks = tasks.value ?: mutableListOf()
        currentTasks.add(task)
        tasks.value = currentTasks
    }

    fun updateTask(updatedTask: Task) {
        val currentTasks = tasks.value ?: mutableListOf()
        val taskIndex = currentTasks.indexOfFirst { it.id == updatedTask.id }
        if (taskIndex != -1) {
            currentTasks[taskIndex] = updatedTask
            tasks.value = currentTasks
        }
    }
    fun removeTask(task: Task) {
        val currentTasks = tasks.value ?: mutableListOf()
        currentTasks.remove(task)
        tasks.value = currentTasks
    }

    fun setTasks(newTasks: List<Task>) {
        tasks.value = newTasks.toMutableList()
    }
}
