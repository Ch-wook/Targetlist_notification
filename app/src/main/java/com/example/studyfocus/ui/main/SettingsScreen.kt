package com.example.studyfocus.ui.main

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyfocus.R
import com.example.studyfocus.data.SettingsManager
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun SettingsScreen(settingsManager: SettingsManager) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val notificationHour by settingsManager.notificationHourFlow.collectAsState(initial = 21)
    val notificationMinute by settingsManager.notificationMinuteFlow.collectAsState(initial = 0)
    
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            coroutineScope.launch {
                settingsManager.saveNotificationTime(hourOfDay, minute)
            }
        },
        notificationHour,
        notificationMinute,
        false
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.tab_settings),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        // Notification Setting
        Text(
            text = stringResource(R.string.settings_notification),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { timePickerDialog.show() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "매일 알림 시간")
                val amPm = if (notificationHour < 12) "AM" else "PM"
                val displayHour = if (notificationHour % 12 == 0) 12 else notificationHour % 12
                val displayMinute = String.format("%02d", notificationMinute)
                Text(
                    text = "$amPm $displayHour:$displayMinute",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
