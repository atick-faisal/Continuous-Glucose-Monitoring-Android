package dev.atick.compose

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import javax.inject.Inject

private const val BUFFER_LEN = 10
private const val UPDATE_INTERVAL = 1000L //... millis

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
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val data by viewModel.incomingMessage.observeAsState()
    val devices by viewModel.pairedDevicesList

    Column(Modifier.fillMaxSize()) {

        Text(text = data ?: "0.0")
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(devices) { device ->
                Button(onClick = { viewModel.connect(device) }) {
                    Text(text = device.name ?: "Unknown")
                }
            }
        }
    }
}


@HiltViewModel
class MainViewModel @Inject constructor(
    private val btManager: BtManager
) : BaseViewModel() {
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
}