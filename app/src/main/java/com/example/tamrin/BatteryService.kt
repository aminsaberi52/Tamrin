package com.example.tamrin

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

class BatteryService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerReceiver(BatteryChangeReceiver(), IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(BatteryChangeReceiver())
        super.onDestroy()
    }

}