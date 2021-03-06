package dev.atick.compose.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.atick.compose.ui.BtViewModel
import dev.atick.compose.ui.common.components.TopBar
import dev.atick.compose.ui.home.components.Form
import dev.atick.compose.ui.home.components.GlucoseCard

@Composable
fun HomeScreen(
    onDisconnect: () -> Unit,
    viewModel: BtViewModel = viewModel()
) {
    val isConnected by viewModel.isConnected.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            TopBar(
                title = "Additional Information",
                onExitClick = {
                    onDisconnect.invoke()
                    viewModel.disconnect()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp
                ),
                visible = isConnected?.peekContent() == true
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    GlucoseCard(glucose = viewModel.glucose)

                    Spacer(modifier = Modifier.height(16.dp))

                    Form(onDisconnect)
                }
            }
        }

        if (viewModel.glucoseWarning.value) {
            Snackbar(
                action = null,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                backgroundColor = MaterialTheme.colors.error

            ) { Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "Glucose Level is Critical",
                textAlign = TextAlign.Center
            ) }
        }
    }
}