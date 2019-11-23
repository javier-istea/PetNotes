package com.example.petnotes

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.petnotes.broadcastreceivers.NotificationReceiver
import com.example.petnotes.model.Note

class NotificationHandler {
    companion object {
        private const val CHANNEL_ID = "${BuildConfig.APPLICATION_ID}.notification.channel"
        private const val CHANNEL_NAME = "${BuildConfig.APPLICATION_ID}.notification.channel"
        private const val CHANNEL_DESCRIPTION = "Pet Notes's channel"
        const val TITLE = "title"

        fun showNotification(context: Context, noteTitle: String) {

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(noteTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            createNotificationChannel(context)
            with(NotificationManagerCompat.from(context)) {
                notify(Math.random().toInt(), builder.build())
            }

        }

        private fun createNotificationChannel(context: Context) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)

            channel.description = CHANNEL_DESCRIPTION
            channel.enableLights(true)
            channel.lightColor = Color.MAGENTA
            channel.enableVibration(true)
            (context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager)
                .createNotificationChannel(channel)
        }

        fun scheduleNotification(
            context: Context,
            note: Note
        ) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, NotificationReceiver::class.java)
            intent.putExtra(TITLE, note.title)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.set(AlarmManager.RTC_WAKEUP, note.reminderDate!!, pendingIntent)
        }
    }
}
