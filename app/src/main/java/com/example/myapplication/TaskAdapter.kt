package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val onTaskClicked: (Task) -> Unit,
    private val onDeleteClicked: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkbox_task)
        val textView: TextView = view.findViewById(R.id.text_task_title)
        val deleteButton: ImageButton = view.findViewById(R.id.button_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        
        holder.textView.text = task.title
        holder.checkBox.isChecked = task.isCompleted
        
        // Listener para o CheckBox
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            val updatedTask = task.copy(isCompleted = isChecked)
            onTaskClicked(updatedTask)
        }
        
        // Listener para o botão de deletar
        holder.deleteButton.setOnClickListener {
            onDeleteClicked(task)
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
