package dev.atick.compose.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
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
import dev.atick.compose.ui.common.components.TopBar
import dev.atick.compose.ui.connection.components.DeviceList
import dev.atick.compose.ui.home.components.Form
import dev.atick.compose.ui.home.components.GlucoseCard
import dev.atick.compose.ui.home.components.LinePlot

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
    }
}