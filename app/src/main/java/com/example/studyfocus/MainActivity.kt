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
import com.example.studyfocus.data.Task
import com.example.studyfocus.data.TaskRepository
import com.example.studyfocus.theme.*
import com.example.studyfocus.ui.TaskViewModel
import com.example.studyfocus.ui.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { TaskRepository(database.taskDao()) }
    private val viewModel: TaskViewModel by viewModels { TaskViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Schedule Notification Worker
        scheduleNotification()

        setContent {
            StudyFocusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel)
                }
            }
        }
    }

    private fun scheduleNotification() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val request = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInitialDelay(calculateDelayToNinePM(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "study_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun calculateDelayToNinePM(): Long {
        val calendar = java.util.Calendar.getInstance()
        val now = calendar.timeInMillis
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 21)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        
        if (calendar.timeInMillis <= now) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis - now
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.todayTasks.collectAsState()
    val achievementRate by viewModel.achievementRate.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "StudyFocus",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Achievement Dashboard
            AchievementCard(achievementRate)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Today's Goals",
                style = MaterialTheme.typography.titleLarge,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onToggle = { viewModel.toggleTask(task) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp),
            containerColor = PrimaryCyan,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Goal")
        }

        if (showAddDialog) {
            AddTaskDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { title ->
                    viewModel.addTask(title)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AchievementCard(rate: Float) {
    val animatedRate by animateFloatAsState(targetValue = rate, label = "Achievement Rate")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(GradientStart.copy(alpha = 0.1f), GradientEnd.copy(alpha = 0.05f))
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { animatedRate },
                        modifier = Modifier.size(100.dp),
                        color = PrimaryCyan,
                        strokeWidth = 12.dp,
                        trackColor = SurfaceColor.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "${(rate * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                Column {
                    Text(
                        text = "Daily Progress",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = if (rate >= 1f) "Excellent Work!" else "Keep going!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) SurfaceColor.copy(alpha = 0.5f) else SurfaceColor
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) SuccessGreen else Color.Transparent)
                    .border(2.dp, if (task.isCompleted) SuccessGreen else TextSecondary, CircleShape)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = task.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                ),
                color = if (task.isCompleted) TextSecondary else TextPrimary
            )

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondary.copy(alpha = 0.5f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { if (text.isNotBlank()) onAdd(text) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        title = { Text("New Study Goal") },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("What are you studying today?") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceColor,
                    unfocusedContainerColor = SurfaceColor,
                    focusedIndicatorColor = PrimaryCyan,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = SurfaceColor,
        shape = RoundedCornerShape(24.dp)
    )
}
