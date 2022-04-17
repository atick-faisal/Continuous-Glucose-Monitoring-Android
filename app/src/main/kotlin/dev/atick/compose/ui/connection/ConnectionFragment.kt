package dev.atick.compose.ui.connection

import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.core.ui.BaseComposeFragment


@AndroidEntryPoint
class ConnectionFragment : BaseComposeFragment() {

    @Composable
    override fun ComposeUi() {
        ConnectionScreen()
    }
}