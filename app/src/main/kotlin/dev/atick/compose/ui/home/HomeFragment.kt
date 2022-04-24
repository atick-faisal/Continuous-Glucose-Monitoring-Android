package dev.atick.compose.ui.home

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.ui.BtViewModel
import dev.atick.compose.ui.theme.ComposeTheme
import dev.atick.core.ui.BaseComposeFragment
import dev.atick.core.utils.extensions.observe

@AndroidEntryPoint
class HomeFragment : BaseComposeFragment() {

    private val viewModel: BtViewModel by viewModels()

    @Composable
    override fun ComposeUi() {
        ComposeTheme {
            HomeScreen()
        }
    }

    override fun observeStates() {
        super.observeStates()
        observe(viewModel.incomingMessage) {
            viewModel.updateBuffer(it)
        }
    }
}