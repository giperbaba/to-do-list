package com.example.to_dolist

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_dolist.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.reflect.Type

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        taskAdapter = TaskAdapter(mutableListOf(),
            onTextChanged = { updatedTask ->
                taskViewModel.updateTask(updatedTask)
            },
            onDeleteTask = { taskToDelete ->
                taskViewModel.removeTask(taskToDelete)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }

        taskViewModel.tasks.observe(this, Observer { tasks ->
            taskAdapter.updateTasks(tasks)
        })

        binding.addTask.setOnClickListener {
            val taskDescription = binding.textInputLayout.text.toString()
            if (taskDescription.isNotEmpty()) {
                taskViewModel.addTask(Task(taskDescription))
                binding.textInputLayout.text?.clear()
            }
        }

        binding.save.setOnClickListener {
            val tasks = taskViewModel.tasks.value
            if (!tasks.isNullOrEmpty()) {
                val gson = Gson()
                val tasksJsonString = gson.toJson(tasks)
                saveJSONToStorage(tasksJsonString)
            }
        }

        binding.upload.setOnClickListener {
            openFilePicker()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/json"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(intent)
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    loadJSONFromUri(uri)
                }
            }
        }

    private fun loadJSONFromUri(uri: Uri) {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val reader = InputStreamReader(inputStream)
            val gson = Gson()
            val taskListType: Type = object : TypeToken<List<Task>>() {}.type
            val tasks: List<Task> = gson.fromJson(reader, taskListType)
            taskViewModel.setTasks(tasks)
        }
    }

    private fun saveJSONToStorage(jsonString: String) {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "data.json")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/")
        }

        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)

        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(jsonString)
                    Toast.makeText(this, "saved successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
