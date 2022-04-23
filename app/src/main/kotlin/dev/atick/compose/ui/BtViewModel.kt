package dev.atick.compose.ui

import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.bluetooth.repository.BtManager
import dev.atick.core.ui.BaseViewModel
import dev.atick.core.utils.Event
import dev.atick.network.data.Request
import dev.atick.network.repository.GlucoseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BtViewModel @Inject constructor(
    private val btManager: BtManager,
    private val glucoseRepository: GlucoseRepository
) : BaseViewModel() {

    companion object {
        private const val BUFFER_LEN = 10
        private const val UPDATE_INTERVAL = 1000L //... millis
    }

    private val buffer = MutableList(BUFFER_LEN) { 0.0F }

    val pairedDevicesList =
        mutableStateOf<List<BluetoothDevice>>(listOf())

    val isConnected = btManager.isConnected
    val incomingMessage = btManager.incomingMessage

    var age by mutableStateOf("25")
    var bmi by mutableStateOf("25")
    var dia by mutableStateOf("80")
    var sys by mutableStateOf("120")
    var type by mutableStateOf("1")
    var pulse by mutableStateOf("90")
    var gender by mutableStateOf("Male")
    var glucose by mutableStateOf("0")

    init {
        fetchPairedDevices()
    }

    fun fetchPairedDevices() {
        pairedDevicesList.value = btManager.getPairedDevicesList()
    }

    fun connect(device: BluetoothDevice) {
        btManager.connect(device) {
            toastMessage.postValue(Event("Connected"))
        }
    }

    fun updateBuffer(data: String) {
        Logger.w(data)
        try {
            buffer.add(data.toFloat())
            buffer.removeFirst()
        } catch (e: NumberFormatException) {
            Logger.e("PARSING ERROR")
        }
    }

    fun sendDataToServer() {
        viewModelScope.launch {
            while (isConnected.value?.peekContent() == true) {
                val ppgData = buffer.joinToString(",")
                val genderInt =
                    if (gender.lowercase() == "male") 0 else 1
                val metadata = "$age,$bmi,$dia,$sys,$type,$pulse,$genderInt"
                val response = glucoseRepository.getGlucosePrediction(
                    Request(
                        ppgData = ppgData,
                        metadata = metadata
                    )
                )
                glucose = response?.glucosePredict ?: "NULL"
                Logger.e(buffer.toString())
                delay(UPDATE_INTERVAL)
            }
        }
    }

    override fun onCleared() {
        btManager.close {
            Logger.w("CONNECTION CLOSED!")
        }
        super.onCleared()
    }
}