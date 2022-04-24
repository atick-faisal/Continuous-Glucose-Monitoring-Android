package dev.atick.compose.ui.connection

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.atick.compose.ui.BtViewModel
import dev.atick.compose.ui.common.components.TopBar
import dev.atick.compose.ui.connection.components.DeviceList

@Composable
@ExperimentalMaterialApi
@SuppressLint("MissingPermission")
fun ConnectionScreen(
    viewModel: BtViewModel = viewModel()
) {

    val isConnected by viewModel.isConnected.observeAsState()
    val devices by viewModel.pairedDevicesList

    return Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TopBar(
                title = "Paired Devices",
                onSearchClick = {},
                onMenuClick = {}
            )

            AnimatedVisibility(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp
                ),
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
}