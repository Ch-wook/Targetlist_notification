package com.example.studyfocus.data

import kotlinx.coroutines.flow.Flow
import java.util.*

class DataRepository(private val taskDao: TaskDao, private val goalDao: GoalDao) {
    
    // Task
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    fun getTodayTasks(): Flow<List<Task>> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return taskDao.getTasksForToday(calendar.timeInMillis)
    }

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    
    // Goal
    val allGoals: Flow<List<Goal>> = goalDao.getAllGoals()
    
    suspend fun insertGoal(goal: Goal) = goalDao.insertGoal(goal)
    suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal)
    suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal)
}
