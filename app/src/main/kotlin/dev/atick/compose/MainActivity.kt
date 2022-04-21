package dev.atick.compose

import ai.atick.material.MaterialColor
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.telephony.SmsManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.bluetooth.repository.BtManager
import dev.atick.bluetooth.utils.BtUtils
import dev.atick.compose.ui.theme.JetpackComposeStarterTheme
import dev.atick.core.ui.BaseViewModel
import dev.atick.core.utils.Event
import dev.atick.core.utils.extensions.observe
import dev.atick.core.utils.extensions.observeEvent
import dev.atick.core.utils.extensions.showToast
import dev.atick.network.data.Request
import dev.atick.network.repository.GlucoseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val BUFFER_LEN = 2048
private const val UPDATE_INTERVAL = 10000L //... millis

//@AndroidEntryPoint
//class MainActivity : AppCompatActivity() {
//
//    @Inject
//    lateinit var btUtils: BtUtils
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        btUtils.initialize(this) {
//            Logger.i("Bluetooth Setup Successful!")
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        btUtils.setupBluetooth(this)
//    }
//}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var btUtils: BtUtils
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeStarterTheme {
                MainScreen()
            }
        }

        btUtils.initialize(this) {
            Logger.i("Bluetooth Setup Successful!")
        }

        observeEvent(viewModel.toastMessage) {
            showToast(it)
        }

        observe(viewModel.incomingMessage) {
            viewModel.updateBuffer(it)
        }
    }

    override fun onResume() {
        super.onResume()
        btUtils.setupBluetooth(this)
    }
}


@Composable
@SuppressLint("MissingPermission")
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val ppg by viewModel.incomingMessage.observeAsState()
    val devices by viewModel.pairedDevicesList

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AnimatedVisibility(visible = !viewModel.isConnected) {
            Column {
                Text(
                    text = "Paired Devices",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(devices) { device ->
                        OutlinedButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            onClick = { viewModel.connect(device) }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = device.name ?: "Unknown")
                                Text(text = device.address ?: "NULL")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.fetchPairedDevices() }
                ) {
                    Text(text = "Refresh Device List")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        AnimatedVisibility(visible = viewModel.isConnected) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Additional Information",
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Glucose: ${viewModel.glucose} mg/dL",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    color = when (viewModel.glucose) {
                        in 0.0F..70.0F -> MaterialColor.Red400
                        in 70.0F..126.0F -> MaterialColor.Blue400
                        in 126.0F..180.0F -> MaterialColor.Teal400
                        in 126.0F..180.0F -> MaterialColor.Indigo400
                        in 180.0F..248.0F -> MaterialColor.Teal400
                        in 248.0F..999.0F -> MaterialColor.Yellow400
                        else -> MaterialColor.Red400
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = ppg ?: "0",
                    label = {
                        Text(text = "PPG")
                    },
                    onValueChange = { }
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.age,
                    keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text(text = "Age")
                    },
                    onValueChange = { viewModel.age = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.bmi,
                    keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text(text = "BMI")
                    },
                    onValueChange = { viewModel.bmi = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.dia,
                    keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text(text = "Diastolic Pressure")
                    },
                    onValueChange = { viewModel.dia = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.sys,
                    keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text(text = "Systolic Pressure")
                    },
                    onValueChange = { viewModel.sys = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.type,
                    keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text(text = "Diabetes Type")
                    },
                    onValueChange = { viewModel.type = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.pulse,
                    keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text(text = "Pulse")
                    },
                    onValueChange = { viewModel.pulse = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.gender,
                    label = {
                        Text(text = "Gender")
                    },
                    onValueChange = { viewModel.gender = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.phone,
                    keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Phone),
                    label = {
                        Text(text = "Emergency Contact")
                    },
                    onValueChange = { viewModel.phone = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { viewModel.sendDataToServer() }
                ) {
                    Text(text = "Start")
                }

                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { viewModel.disconnect() }
                ) {
                    Text(text = "Disconnect")
                }
            }
        }
    }
}


@HiltViewModel
class MainViewModel @Inject constructor(
    private val glucoseRepository: GlucoseRepository,
    private val btManager: BtManager
) : BaseViewModel() {
    private val buffer = MutableList(BUFFER_LEN) { 0.0F }
    var isConnected by mutableStateOf(false)

    val pairedDevicesList =
        mutableStateOf<List<BluetoothDevice>>(listOf())

    val incomingMessage = btManager.incomingMessage

    var age by mutableStateOf("25")
    var bmi by mutableStateOf("25")
    var dia by mutableStateOf("80")
    var sys by mutableStateOf("120")
    var type by mutableStateOf("1")
    var pulse by mutableStateOf("90")
    var gender by mutableStateOf("Male")
    var glucose by mutableStateOf(0.0F)
    var phone by mutableStateOf("+974")

    init {
        fetchPairedDevices()
    }

    fun fetchPairedDevices() {
        pairedDevicesList.value = btManager.getPairedDevicesList()
    }

    fun connect(device: BluetoothDevice) {
        btManager.connect(device) {
            isConnected = true
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

    fun sendDataToServer() {
        toastMessage.postValue(
            Event("Streaming Data to the Server ...")
        )
        viewModelScope.launch {
            while (isConnected) {
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
                    if (glucose < 70.0F || glucose > 200.0F) {
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
            isConnected = false
            toastMessage.postValue(
                Event("Disconnected!")
            )
            Logger.w("Connection closed!")
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
                Event("SMS sent")
            )
            Logger.w("SMS SENT")
        }
    }


    override fun onCleared() {
        disconnect()
        super.onCleared()
    }
}