package dev.atick.compose.ui.connection

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.ui.BtViewModel
import dev.atick.compose.ui.theme.ComposeTheme
import dev.atick.core.ui.BaseComposeFragment
import dev.atick.core.utils.extensions.observeEvent


@AndroidEntryPoint
class ConnectionFragment : BaseComposeFragment() {

    private val viewModel: BtViewModel by viewModels()

    @Composable
    override fun ComposeUi() {
        ComposeTheme {
            ConnectionScreen()
        }
    }

    override fun observeStates() {
        super.observeStates()
        observeEvent(viewModel.isConnected) {
            if (it) navigateToHomeFragment()
        }
    }

    private fun navigateToHomeFragment() {
        findNavController().navigate(
            ConnectionFragmentDirections
                .actionConnectionFragmentToHomeFragment()
        )
    }
}