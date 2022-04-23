package dev.atick.bluetooth.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.orhanobut.logger.Logger
import dev.atick.core.utils.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BtManagerImpl @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter?
) : BtManager {

    companion object {
        private val BT_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private val _isConnected = MutableLiveData<Event<Boolean>>()
    override val isConnected: LiveData<Event<Boolean>>
        get() = _isConnected

    private val _incomingMessage = MutableLiveData<String>()
    override val incomingMessage: LiveData<String>
        get() = _incomingMessage

    private var mmSocket: BluetoothSocket? = null

    override fun getPairedDevicesList(): List<BluetoothDevice> {
        val bondedDevices = bluetoothAdapter?.bondedDevices
        val pairedDevices = mutableListOf<BluetoothDevice>()
        Logger.i("FOUND ${bondedDevices?.size} DEVICES")
        bondedDevices?.forEach { device ->
            pairedDevices.add(device)
        }
        return pairedDevices.toList()
    }

    override fun connect(
        bluetoothDevice: BluetoothDevice,
        onSuccess: () -> Unit
    ) {
        bluetoothAdapter?.cancelDiscovery()
        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                mmSocket = if (mmSocket == null) {
                    bluetoothDevice.createInsecureRfcommSocketToServiceRecord(BT_UUID)
                } else {
                    mmSocket?.close()
                    bluetoothDevice.createInsecureRfcommSocketToServiceRecord(BT_UUID)
                }
                mmSocket?.connect()
            }.onSuccess {
                _isConnected.postValue(Event(true))
                Logger.i("BLUETOOTH DEVICE CONNECTED")
                onSuccess.invoke()
                handleBluetoothClient()
            }.onFailure { throwable ->
                _isConnected.postValue(Event(false))
                when (throwable) {
                    is IOException -> Logger.i("CONNECTION FAILED")
                    else -> throwable.printStackTrace()
                }
            }
        }
    }

    override fun send(message: String, onSuccess: () -> Unit) {
        mmSocket?.let { socket ->
            CoroutineScope(Dispatchers.IO).launch {
                kotlin.runCatching {
                    socket.outputStream.write(message.toByteArray())
                }.onSuccess {
                    onSuccess.invoke()
                }.onFailure { throwable ->
                    when (throwable) {
                        is IOException -> Logger.i("SENDING FAILED")
                        else -> throwable.printStackTrace()
                    }
                }
            }
        }
    }

    override fun close(onSuccess: () -> Unit) {
        mmSocket?.let { socket ->
            CoroutineScope(Dispatchers.IO).launch {
                kotlin.runCatching {
                    socket.close()
                }.onSuccess {
                    _isConnected.postValue(Event(false))
                    onSuccess.invoke()
                }.onFailure { throwable ->
                    when (throwable) {
                        is IOException -> Logger.i("COULDN'T CLOSE SOCKET")
                        else -> throwable.printStackTrace()
                    }
                }
            }
        }
    }

    private fun handleBluetoothClient() {
        mmSocket?.let {
            CoroutineScope(Dispatchers.IO).launch {
                kotlin.runCatching {
                    val mmInStream: InputStream = it.inputStream
                    val bufferedReader = BufferedReader(
                        InputStreamReader(mmInStream)
                    )
                    while (_isConnected.value?.peekContent() == true) {
                        if (bufferedReader.ready()) {
                            val data = bufferedReader.readLine()
                            _incomingMessage.postValue(data)
                        }
                    }
                }.onFailure { throwable ->
                    _isConnected.postValue(Event(false))
                    when (throwable) {
                        is IOException -> Logger.i("SOCKET CLOSED")
                        else -> throwable.printStackTrace()
                    }
                }
            }
        }
    }
}