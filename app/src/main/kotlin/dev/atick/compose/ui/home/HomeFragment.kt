package dev.atick.compose.ui.home

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.ui.BtViewModel
import dev.atick.core.ui.BaseComposeFragment
import dev.atick.core.utils.extensions.observe
import dev.atick.core.utils.extensions.observeEvent

@AndroidEntryPoint
class HomeFragment : BaseComposeFragment() {

    private val viewModel: BtViewModel by viewModels()

    override fun initialize() {
        super.initialize()
        viewModel.connect()
    }

    @Composable
    override fun ComposeUi() {
        HomeScreen()
    }

    override fun observeStates() {
        super.observeStates()
        observe(viewModel.incomingMessage) {
            viewModel.updateBuffer(it)
        }
    }
}