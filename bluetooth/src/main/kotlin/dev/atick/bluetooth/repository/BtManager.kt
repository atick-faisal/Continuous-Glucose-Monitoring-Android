package dev.atick.bluetooth.repository

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import dev.atick.core.utils.Event

interface BtManager {
    val isConnected: LiveData<Event<Boolean>>
    val incomingMessage: LiveData<String>
    fun getPairedDevicesList(): List<BluetoothDevice>
    fun connect(
        bluetoothDevice: BluetoothDevice,
        onSuccess: () -> Unit
    )

    fun send(message: String, onSuccess: () -> Unit)
    fun close(onSuccess: () -> Unit)
}