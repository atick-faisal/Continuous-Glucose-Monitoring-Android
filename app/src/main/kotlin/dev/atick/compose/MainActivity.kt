package dev.atick.compose

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

private const val BUFFER_LEN = 10
private const val UPDATE_INTERVAL = 1000L //... millis

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
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { viewModel.connect(device) }
                        ) {
                            Text(text = device.name ?: "Unknown")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

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
                text = "Glucose: ${viewModel.glucose}",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = Color.Blue
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
                label = {
                    Text(text = "Age")
                },
                onValueChange = { viewModel.age = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.bmi,
                label = {
                    Text(text = "BMI")
                },
                onValueChange = { viewModel.bmi = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.dia,
                label = {
                    Text(text = "Diastolic Pressure")
                },
                onValueChange = { viewModel.dia = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.sys,
                label = {
                    Text(text = "Systolic Pressure")
                },
                onValueChange = { viewModel.sys = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.type,
                label = {
                    Text(text = "Diabetes Type")
                },
                onValueChange = { viewModel.type = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.pulse,
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

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                onClick = { viewModel.sendDataToServer() }
            ) {
                Text(text = "Start")
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
    var glucose by mutableStateOf("0")

    init {
        fetchPairedDevices()
    }

    private fun fetchPairedDevices() {
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
        viewModelScope.launch {
            while (true) {
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
                Logger.w(buffer.toString())
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