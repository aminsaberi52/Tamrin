package com.example.tamrin

import LogData
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.JsonArray
import org.json.JSONArray
import java.io.File

class BluetoothAirplaneModeWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        // Check Bluetooth state
        val bluetoothManager: BluetoothManager =
            this.applicationContext.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
        val bluetoothState = bluetoothAdapter?.isEnabled ?: false

        // Check Airplane mode state
        val airplaneState = Settings.Global.getInt(
            this.applicationContext.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON,
            0
        )

        // Create a LogData object
        val logData = LogData(
            timestamp = System.currentTimeMillis(),
            bluetoothState = bluetoothState,
            airplaneModeState = airplaneState > 0
        )
        val file = File(this.applicationContext.filesDir.absolutePath, "log.txt")

        val gson = Gson()
        val jsonArray = gson.toJsonTree(listOf(logData)).asJsonArray
        if (file.readBytes().isNotEmpty()) {
            jsonArray.addAll(gson.fromJson(file.readText(), JsonArray::class.java))
        }
        file.writeText(gson.toJson(jsonArray))

        return Result.success()
    }

}