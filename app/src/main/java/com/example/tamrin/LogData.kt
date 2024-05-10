import com.google.gson.annotations.SerializedName

data class LogData(
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("bluetoothState") val bluetoothState: Boolean,
    @SerializedName("airplaneModeState") val airplaneModeState: Boolean
)