package dev.atick.compose.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.atick.compose.ui.BtViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.atick.compose.R

@Composable
fun Form(
    viewModel: BtViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(
                rememberScrollState()
            )
    ) {
        CustomTextFiled(
            textFieldValue = viewModel.age,
            labelResourceId = R.string.age,
            hintResourceId = R.string.hint_age,
            onValueChange = { viewModel.age = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Age"
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextFiled(
            textFieldValue = viewModel.bmi,
            labelResourceId = R.string.bmi,
            hintResourceId = R.string.hint_bmi,
            onValueChange = { viewModel.bmi = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Height,
                    contentDescription = "Age"
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextFiled(
            textFieldValue = viewModel.gender,
            labelResourceId = R.string.gender,
            hintResourceId = R.string.hint_gender,
            onValueChange = { viewModel.gender = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Male,
                    contentDescription = "Age"
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextFiled(
            textFieldValue = viewModel.type,
            labelResourceId = R.string.type,
            hintResourceId = R.string.diabetes_type,
            onValueChange = { viewModel.type = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Bloodtype,
                    contentDescription = "Age"
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextFiled(
            textFieldValue = viewModel.dia,
            labelResourceId = R.string.dia,
            hintResourceId = R.string.dia_hint,
            onValueChange = { viewModel.dia = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Air,
                    contentDescription = "Age"
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextFiled(
            textFieldValue = viewModel.sys,
            labelResourceId = R.string.sys,
            hintResourceId = R.string.hint_sys,
            onValueChange = { viewModel.sys = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Air,
                    contentDescription = "Age"
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextFiled(
            textFieldValue = viewModel.pulse,
            labelResourceId = R.string.pulse,
            hintResourceId = R.string.hint_pulse,
            onValueChange = { viewModel.pulse = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Age"
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextFiled(
            textFieldValue = viewModel.phone,
            labelResourceId = R.string.phone,
            hintResourceId = R.string.hint_phone,
            onValueChange = { viewModel.phone = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Age"
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            onClick = { viewModel.sendDataToServer() }
        ) {
            Text(text = "Start Streaming Data")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            onClick = { viewModel.disconnect() }
        ) {
            Text(
                text = "Disconnect",
                color = MaterialTheme.colors.error
            )
        }
    }
}