package com.example.tamrin

import LogData
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val batteryReceiver = BatteryChangeReceiver()
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        startService(Intent(this, BatteryService::class.java))

        /*
        Note: The minimum repeat interval that can be defined is 15 minutes (same as the JobScheduler API).
        https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work
         */
        val workRequest = PeriodicWorkRequestBuilder<BluetoothAirplaneModeWorker>(2, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)

        val file = File(this.applicationContext.filesDir.absolutePath, "log.txt")
        if (!file.exists())
            file.createNewFile()
        var listOfLogData = listOf<LogData>()
        if(file.readText().isNotEmpty()) {
            val gson = Gson()
            listOfLogData = gson.fromJson(file.readText(), object : TypeToken<List<LogData>>() {}.type)
        }
        Log.i("MainActivity", "onCreate: $listOfLogData")

        setContent {
            val batteryLevel = batteryReceiver.batteryLevelFlow.collectAsState()
            Column {
                Text(text = "Battery Level: ${batteryLevel.value}")
                LazyColumn(
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(listOfLogData.sortedByDescending { it.timestamp }) { logData ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                contentColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(text = "Time: ${logData.timestamp}", color = MaterialTheme.colorScheme.primary)
                                Text(text = "Bluetooth: ${logData.bluetoothState}", color = MaterialTheme.colorScheme.primary)
                                Text(text = "Airplane: ${logData.airplaneModeState}", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

    }

}
