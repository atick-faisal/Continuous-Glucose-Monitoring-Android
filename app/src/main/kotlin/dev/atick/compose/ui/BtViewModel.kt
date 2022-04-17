package dev.atick.compose.ui

import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.bluetooth.repository.BtManager
import dev.atick.core.ui.BaseViewModel
import dev.atick.core.utils.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BtViewModel @Inject constructor(
    private val btManager: BtManager
): BaseViewModel() {

    companion object {
        private const val BUFFER_LEN = 10
        private const val UPDATE_INTERVAL = 1000L //... millis
    }

    private val buffer = MutableList(BUFFER_LEN) { 0.0F }
    private var isConnected: Boolean = false

    val pairedDevicesList =
        mutableStateOf<List<BluetoothDevice>>(listOf())

    val incomingMessage = btManager.incomingMessage

    init {
        fetchPairedDevices()
    }

    private fun fetchPairedDevices() {
        pairedDevicesList.value = btManager.getPairedDevicesList()
    }

    fun connect(device: BluetoothDevice) {
        btManager.connect(device) {
            isConnected = true
            sendDataToServer()
            toastMessage.postValue(Event("Connected"))
        }
    }

    fun updateBuffer(data: String) {
        try {
            buffer.add(data.toFloat())
            buffer.removeFirst()
        } catch (e: NumberFormatException) {
            Logger.w("Can not convert to float")
        }
    }

    private fun sendDataToServer() {
        viewModelScope.launch {
            while (isConnected) {
                Logger.e(buffer.toString())
                delay(UPDATE_INTERVAL)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        btManager.close {
            Logger.w("Connection closed!")
        }
    }
}