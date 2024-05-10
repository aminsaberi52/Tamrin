package com.example.tamrin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.flow.MutableStateFlow

const val CHANNEL_ID = "battery_level_channel"
class BatteryChangeReceiver :  BroadcastReceiver() {

    val batteryLevelFlow = MutableStateFlow(0)

    override fun onReceive(context: Context?, intent: Intent?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Battery Level",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
            val batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            batteryLevelFlow.value = batteryLevel
            // Create or update the notification
            val notification = NotificationCompat.Builder(context!!, CHANNEL_ID)
                .setContentTitle("Battery Level")
                .setContentText("$batteryLevel%")
                .setSmallIcon(R.drawable.ic_battery)
                .build()

            // Show the notification
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1, notification)
        }
    }

}