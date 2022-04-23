package dev.atick.compose.ui.connection.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
@SuppressLint("MissingPermission")
fun DeviceList(
    deviceList: List<BluetoothDevice>,
    onDeviceClick: (BluetoothDevice) -> Unit,
    onRefreshClick: () -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(deviceList) { device ->
                BluetoothDevice(
                    bluetoothDevice = device,
                    onClick = onDeviceClick
                )
//                OutlinedButton(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(64.dp),
//                    onClick = { onDeviceClick(device) }
//                ) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Text(text = device.name ?: "Unknown")
//                        Text(text = device.address ?: "NULL")
//                    }
//                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            onClick = { onRefreshClick() }
        ) {
            Text(text = "Refresh Device List")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}