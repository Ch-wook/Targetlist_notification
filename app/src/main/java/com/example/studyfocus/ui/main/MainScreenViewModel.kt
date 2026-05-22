package com.example.studyfocus.ui.main

import androidx.lifecycle.*
import com.example.studyfocus.data.DataRepository
import com.example.studyfocus.data.Goal
import com.example.studyfocus.data.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainScreenViewModel(private val repository: DataRepository) : ViewModel() {

    val todayTasks: StateFlow<List<Task>> = repository.getTodayTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val allGoals: StateFlow<List<Goal>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievementRate: StateFlow<Float> = todayTasks.map { tasks ->
        if (tasks.isEmpty()) 0f
        else {
            val completedCount = tasks.count { it.isCompleted }
            completedCount.toFloat() / tasks.size
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    // Tasks
    fun addTask(title: String, goalId: Int? = null) {
        viewModelScope.launch {
            repository.insertTask(Task(title = title, goalId = goalId))
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
    
    // Goals
    fun addGoal(title: String) {
        viewModelScope.launch {
            repository.insertGoal(Goal(title = title))
        }
    }
    
    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }
}

class MainScreenViewModelFactory(private val repository: DataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainScreenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
