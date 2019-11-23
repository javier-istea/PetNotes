package com.example.petnotes.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.petnotes.NotificationHandler

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationHandler.showNotification(
            context,
            intent.getStringExtra(NotificationHandler.TITLE)
        )
    }
}
