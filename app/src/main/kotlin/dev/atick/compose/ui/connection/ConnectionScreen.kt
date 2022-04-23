package dev.atick.compose.ui.connection

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.atick.compose.ui.BtViewModel

@Composable
@SuppressLint("MissingPermission")
fun ConnectionScreen(
    onDeviceClick: () -> Unit,
    viewModel: BtViewModel = viewModel()
) {

    val devices by viewModel.pairedDevicesList

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            onClick = { viewModel.fetchPairedDevices() }
        ) {
            Text(text = "Refresh Device List")
        }

        Column(Modifier.fillMaxSize()) {
            Text(
                text = "Paired Devices",
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(devices) { device ->
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.connect(device)
                            onDeviceClick()
                        }
                    ) {
                        Text(text = device.name ?: "Unknown")
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}