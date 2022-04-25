package dev.atick.compose.ui.connection

import android.os.Bundle
import android.view.View
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.bluetooth.utils.BtUtils
import dev.atick.compose.ui.BtViewModel
import dev.atick.compose.ui.theme.ComposeTheme
import dev.atick.core.ui.BaseComposeFragment
import dev.atick.core.utils.extensions.observeEvent
import javax.inject.Inject


@AndroidEntryPoint
@ExperimentalMaterialApi
class ConnectionFragment : BaseComposeFragment() {

    @Inject
    lateinit var bleUtils: BtUtils
    private val viewModel: BtViewModel by viewModels()

    @Composable
    override fun ComposeUi() {
        ComposeTheme {
            ConnectionScreen()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (bleUtils.isAllPermissionsProvided(requireActivity())) {
            viewModel.fetchPairedDevices()
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