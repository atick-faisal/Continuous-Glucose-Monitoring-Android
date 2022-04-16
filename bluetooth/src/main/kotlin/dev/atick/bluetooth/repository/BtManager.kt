package dev.atick.bluetooth.repository

import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData

interface BtManager {
    val incomingMessage: LiveData<String>
    fun isBluetoothAvailable(): Boolean
    fun enableBluetooth(resultLauncher: ActivityResultLauncher<Intent>)
    fun getPairedDevicesList(): List<BluetoothDevice>
    fun connect(bluetoothDevice: BluetoothDevice, onConnect: () -> Unit)
    fun send(message: String, onSuccess: () -> Unit)
    fun close(onSuccess: () -> Unit)
}