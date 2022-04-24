package dev.atick.compose.ui

import android.bluetooth.BluetoothDevice
import android.telephony.SmsManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
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
        private const val BUFFER_LEN = 2048
        private const val UPDATE_INTERVAL = 10000L //... millis
        private const val GLUCOSE_LOW = 70.0F
        private const val GLUCOSE_HIGH = 200.0F
    }

    private val buffer = MutableList(BUFFER_LEN) { 1.0F }
    private val entries = mutableListOf<Entry>()
    var ppgDataset by mutableStateOf(
        LineDataSet(mutableListOf<Entry>(), "PPG")
    )

    val pairedDevicesList =
        mutableStateOf<List<BluetoothDevice>>(listOf())

    private var counter = 0

    val isConnected = btManager.isConnected
    val incomingMessage = btManager.incomingMessage

    var age by mutableStateOf("25")
    var bmi by mutableStateOf("25")
    var dia by mutableStateOf("80")
    var sys by mutableStateOf("120")
    var type by mutableStateOf("1")
    var pulse by mutableStateOf("90")
    var gender by mutableStateOf("Male")
    var glucose by mutableStateOf(-1.0F)
    var phone by mutableStateOf("+974")

//    var glucoseWarning by mutableStateOf(
//        (glucose < GLUCOSE_LOW
//            || glucose > GLUCOSE_HIGH)
//            && glucose != -1.0F
//    )

    val glucoseWarning: MutableState<Boolean>
        get() = mutableStateOf(
            (glucose < GLUCOSE_LOW
                || glucose > GLUCOSE_HIGH)
                && glucose != -1.0F
        )

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
        viewModelScope.launch {
            try {
                buffer.removeFirst()
                buffer.add(data.toFloat())
            } catch (e: NumberFormatException) {
                Logger.e("PARSING ERROR")
            }
        }

        viewModelScope.launch {
            entries.clear()
            buffer.forEachIndexed { index, value ->
                entries.add(
                    Entry(index.toFloat(), value)
                )
            }
        }

        if (counter % 100 == 0) {
            ppgDataset = LineDataSet(entries, "PPG")
            counter = 0
        }

        counter++
    }

    fun sendDataToServer() {
        toastMessage.postValue(
            Event("Streaming Data to the Server")
        )
        viewModelScope.launch {
            while (isConnected.value?.peekContent() == true) {
                delay(UPDATE_INTERVAL)
                val ppgData = buffer.joinToString(",")
                val genderInt =
                    if (gender.lowercase() == "male") 0 else 1
                val metadata = "$age,$bmi,$dia,$type,$genderInt,$pulse,$sys"
                try {
                    val response = glucoseRepository.getGlucosePrediction(
                        Request(
                            ppgData = ppgData,
                            metadata = metadata
                        )
                    )
                    glucose = response?.glucosePredict?.toFloat() ?: 0.0F
                    if (glucoseWarning.value) {
                        sendText(phone, glucose)
                    }
                } catch (e: Exception) {
                    toastMessage.postValue(
                        Event("Server Error")
                    )
                }
                Logger.w(metadata)
                Logger.w(ppgData)
            }
        }
    }

    fun disconnect() {
        btManager.close {
            toastMessage.postValue(
                Event("Disconnected!")
            )
            Logger.w("CONNECTION CLOSED!")
        }
    }

    private fun sendText(phone: String, glucose: Float) {
        val smsManager = SmsManager.getDefault()
        smsManager?.let {
            it.sendTextMessage(
                phone,
                null,
                "Help! Glucose value in critical range." +
                    " Last recorded value $glucose mg/dL",
                null,
                null
            )
            toastMessage.postValue(
                Event("SMS Sent to Emergency Contact")
            )
            Logger.w("SMS SENT")
        }
    }


    override fun onCleared() {
        disconnect()
        super.onCleared()
    }
}