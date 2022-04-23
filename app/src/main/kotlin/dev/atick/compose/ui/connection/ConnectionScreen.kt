package dev.atick.compose.ui.connection

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.atick.compose.ui.BtViewModel
import dev.atick.compose.ui.connection.components.DeviceList

@Composable
@SuppressLint("MissingPermission")
fun ConnectionScreen(
    viewModel: BtViewModel = viewModel()
) {

    val isConnected by viewModel.isConnected.observeAsState()
    val devices by viewModel.pairedDevicesList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AnimatedVisibility(
            visible = isConnected?.peekContent() == false
        ) {
            DeviceList(
                deviceList = devices,
                onDeviceClick = { device ->
                    viewModel.connect(device)
                },
                onRefreshClick = {
                    viewModel.fetchPairedDevices()
                }
            )
        }
    }
}