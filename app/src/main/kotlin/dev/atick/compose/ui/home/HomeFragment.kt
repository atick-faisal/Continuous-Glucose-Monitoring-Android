package dev.atick.compose.ui.home

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.ui.BtViewModel
import dev.atick.compose.ui.theme.ComposeTheme
import dev.atick.core.ui.BaseComposeFragment
import dev.atick.core.utils.extensions.observe
import dev.atick.core.utils.extensions.observeEvent
import dev.atick.core.utils.extensions.showToast

@AndroidEntryPoint
class HomeFragment : BaseComposeFragment() {

    private val viewModel: BtViewModel by viewModels()

    @Composable
    override fun ComposeUi() {
        ComposeTheme {
            HomeScreen(::navigateToConnectionFragment)
        }
    }

    override fun observeStates() {
        super.observeStates()
        observe(viewModel.incomingMessage) {
            viewModel.updateBuffer(it)
        }

        observeEvent(viewModel.toastMessage) {
            context?.showToast(it)
        }
    }

    private fun navigateToConnectionFragment() {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToConnectionFragment()
        )
    }
}