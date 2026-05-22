package com.example.studyfocus.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.studyfocus.data.AppDatabase
import com.example.studyfocus.data.DataRepository
import kotlinx.coroutines.flow.first
import java.util.*

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = DataRepository(database.taskDao(), database.goalDao())
        
        val tasks = repository.getTodayTasks().first()
        if (tasks.isNotEmpty()) {
            val completedCount = tasks.count { it.isCompleted }
            val rate = (completedCount.toFloat() / tasks.size * 100).toInt()
            
            val notificationHelper = NotificationHelper(applicationContext)
            notificationHelper.createNotificationChannel()
            notificationHelper.showAchievementNotification(rate)
        }
        
        return Result.success()
    }
}
