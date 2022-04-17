package dev.atick.compose.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.atick.compose.ui.BtViewModel

@Composable
fun HomeScreen(
    viewModel: BtViewModel = viewModel()
) {
    val ppg by viewModel.incomingMessage.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
            
            Spacer(modifier = Modifier.height(128.dp))
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter),
            onClick = { viewModel.sendDataToServer() }
        ) {
            Text(text = "Start")
        }
    }
}