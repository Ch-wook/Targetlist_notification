package com.example.studyfocus.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.studyfocus.R

class NotificationHelper(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Study Progress",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily study achievement reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showAchievementNotification(rate: Int) {
        val message = when {
            rate >= 100 -> "Perfect! You've completed all your goals today! 🌟"
            rate >= 50 -> "Great job! You've achieved $rate% of your goals. Keep it up!"
            else -> "You're at $rate%. Let's finish those study goals! 💪"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Using system icon for now
            .setContentTitle("Today's Progress")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    companion object {
        const val CHANNEL_ID = "study_focus_channel"
    }
}
