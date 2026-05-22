package com.example.studyfocus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.*
import com.example.studyfocus.data.AppDatabase
import com.example.studyfocus.notification.NotificationWorker
import java.util.concurrent.TimeUnit
import com.example.studyfocus.data.DataRepository
import com.example.studyfocus.data.SettingsManager
import com.example.studyfocus.theme.*
import com.example.studyfocus.ui.main.MainScreenViewModel
import com.example.studyfocus.ui.main.MainScreenViewModelFactory

class MainActivity : ComponentActivity() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { DataRepository(database.taskDao(), database.goalDao()) }
    private val viewModel: MainScreenViewModel by viewModels { MainScreenViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Schedule Notification Worker
        observeAndScheduleNotification()

        setContent {
            StudyFocusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel)
                }
            }
        }
    }

    private fun observeAndScheduleNotification() {
        val settingsManager = SettingsManager(applicationContext)
        lifecycleScope.launch {
            settingsManager.notificationHourFlow.collect { hour ->
                val minute = settingsManager.notificationMinuteFlow.first()
                scheduleNotification(hour, minute)
            }
        }
        lifecycleScope.launch {
            settingsManager.notificationMinuteFlow.collect { minute ->
                val hour = settingsManager.notificationHourFlow.first()
                scheduleNotification(hour, minute)
            }
        }
    }

    private fun scheduleNotification(hour: Int, minute: Int) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val request = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInitialDelay(calculateDelay(hour, minute), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "study_reminder",
            ExistingPeriodicWorkPolicy.REPLACE, // Replace when time changes
            request
        )
    }

    private fun calculateDelay(hourOfDay: Int, minuteOfDay: Int): Long {
        val calendar = java.util.Calendar.getInstance()
        val now = calendar.timeInMillis
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(java.util.Calendar.MINUTE, minuteOfDay)
        calendar.set(java.util.Calendar.SECOND, 0)
        
        if (calendar.timeInMillis <= now) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis - now
    }
}
