package dev.atick.compose.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {

    var age by remember { mutableStateOf("25") }
    var bmi by remember { mutableStateOf("25") }
    var dia by remember { mutableStateOf("80") }
    var sys by remember { mutableStateOf("120") }
    var type by remember { mutableStateOf("1") }
    var pulse by remember { mutableStateOf("90") }
    var gender by remember { mutableStateOf(1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter),
            onClick = { }
        ) {
            Text(text = "Start")
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Additional Information",
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            TextField(modifier = Modifier.fillMaxWidth(), value = age, label = {
                Text(text = "Age")
            }, onValueChange = { age = it })

            Spacer(modifier = Modifier.height(16.dp))
            TextField(modifier = Modifier.fillMaxWidth(), value = bmi, label = {
                Text(text = "BMI")
            }, onValueChange = { bmi = it })

            Spacer(modifier = Modifier.height(16.dp))
            TextField(modifier = Modifier.fillMaxWidth(), value = dia, label = {
                Text(text = "Diastolic Pressure")
            }, onValueChange = { dia = it })

            Spacer(modifier = Modifier.height(16.dp))
            TextField(modifier = Modifier.fillMaxWidth(), value = sys, label = {
                Text(text = "Systolic Pressure")
            }, onValueChange = { sys = it })

            Spacer(modifier = Modifier.height(16.dp))
            TextField(modifier = Modifier.fillMaxWidth(), value = type, label = {
                Text(text = "Diabetes Type")
            }, onValueChange = { type = it })

            Spacer(modifier = Modifier.height(16.dp))
            TextField(modifier = Modifier.fillMaxWidth(), value = pulse, label = {
                Text(text = "Pulse")
            }, onValueChange = { pulse = it })
        }
    }
}