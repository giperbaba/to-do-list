package com.example.to_dolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolist.databinding.ItemTaskBinding

class TaskAdapter(
    private var tasks: MutableList<Task>,
    private val onTextChanged: (Task) -> Unit,
    private val onDeleteTask: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.checkboxIsDone.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    tasks[position].isDone = isChecked
                    onTextChanged(tasks[position])
                }
            }

            binding.taskDescription.setOnClickListener {
                binding.taskDescription.isFocusableInTouchMode = true
                binding.taskDescription.requestFocus()
            }

            binding.taskDescription.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        tasks[position].description = binding.taskDescription.text.toString()
                        onTextChanged(tasks[position])

                        binding.taskDescription.isFocusable = false
                        binding.taskDescription.isFocusableInTouchMode = false
                    }
                }
            }

            binding.deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteTask(tasks[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.taskDescription.setText(task.description)
        holder.binding.checkboxIsDone.isChecked = task.isDone
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        val diffCallback = TaskDiffUtilCallback(tasks, newTasks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        tasks.clear()
        tasks.addAll(newTasks)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getTaskAtPosition(position: Int): Task {
        return tasks[position]
    }
}
