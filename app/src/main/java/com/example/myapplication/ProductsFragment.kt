package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.FragmentProductsBinding
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProductsFragment : Fragment() {

    private var _binding : FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductsViewModel by viewModels()
    private var param1: String? = null
    private var param2: String? = null
    
    private lateinit var taskAdapter: TaskAdapter
    private var taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        observeUiState()
        binding.btnRetry.setOnClickListener { viewModel.refresh() }
    }
    
    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClicked = { task ->
                updateTask(task)
            },
            onDeleteClicked = { task ->
                deleteTask(task)
            }
        )
        
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.buttonAddTask.setOnClickListener {
            addNewTask()
        }
    }
    
    private fun addNewTask() {
        val title = binding.editTextNewTask.text.toString().trim()
        if (title.isNotEmpty()) {
            val newTask = Task(title = title)
            taskList.add(newTask)
            taskAdapter.submitList(taskList.toList())
            binding.editTextNewTask.text.clear()
        }
    }
    
    private fun updateTask(updatedTask: Task) {
        val index = taskList.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            taskList[index] = updatedTask
            taskAdapter.submitList(taskList.toList())
        }
    }
    
    private fun deleteTask(taskToDelete: Task) {
        taskList.removeAll { it.id == taskToDelete.id }
        taskAdapter.submitList(taskList.toList())
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentGroup.visibility = View.GONE
        binding.errorGroup.visibility = View.GONE
    }

    private fun showContent(items: List<String>) {
        binding.progressBar.visibility = View.GONE
        binding.errorGroup.visibility = View.GONE
        binding.contentGroup.visibility = View.VISIBLE
        // Não precisamos mais mostrar items aqui, pois agora usamos RecyclerView
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.contentGroup.visibility = View.GONE
        binding.errorGroup.visibility = View.VISIBLE
        binding.tvError.text = message
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> showLoading()
                        is UiState.Success -> showContent(state.data)
                        is UiState.Error -> showError(state.message)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}