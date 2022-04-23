package dev.atick.compose.ui.connection.components

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BluetoothDevice(
    modifier: Modifier = Modifier,
    bluetoothDevice: BluetoothDevice,
    onClick: (BluetoothDevice) -> Unit
) {
    return Card(
        modifier = modifier.then(
            Modifier.fillMaxWidth()
        ),
        elevation = if (isSystemInDarkTheme()) 0.dp else 2.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        DeviceInfo(
            bluetoothDevice = bluetoothDevice,
            isConnected = false,
            onClick = onClick
        )
    }
}