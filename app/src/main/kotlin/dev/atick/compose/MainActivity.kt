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
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.bluetooth.repository.BtManager
import dev.atick.bluetooth.utils.BtUtils
import dev.atick.compose.ui.theme.JetpackComposeStarterTheme
import dev.atick.core.ui.BaseViewModel
import javax.inject.Inject
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.atick.core.utils.Event
import dev.atick.core.utils.extensions.observeEvent
import dev.atick.core.utils.extensions.showToast

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
    val data by viewModel.data.observeAsState()
    val devices by viewModel.pairedDevicesList

    Column(Modifier.fillMaxSize()) {
        LazyColumn {
            items(devices) { device ->
                Button(onClick = { viewModel.connect(device) }) {
                    Text(text = device.name ?: "Unknown")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = data ?: "0.0")
    }
}


@HiltViewModel
class MainViewModel @Inject constructor(
    private val btManager: BtManager
) : BaseViewModel() {
    val pairedDevicesList =
        mutableStateOf<List<BluetoothDevice>>(listOf())

    val data = btManager.incomingMessage

    init {
        fetchPairedDevices()
    }

    private fun fetchPairedDevices() {
        pairedDevicesList.value = btManager.getPairedDevicesList()
    }

    fun connect(device: BluetoothDevice) {
        btManager.connect(device) {
            toastMessage.postValue(Event("Connected"))
        }
    }
}