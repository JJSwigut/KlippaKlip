package feature.mainscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement.Floating
import androidx.compose.ui.window.WindowPosition.Aligned
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import feature.AppCoordinator
import kotlinx.coroutines.CoroutineDispatcher
import ui.MainView

data class MainScreen(
    val coordinator: AppCoordinator,
    val dispatcher: CoroutineDispatcher,
    val shouldShow: Boolean,
) : Screen {

    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel {
            MainViewModel(
                coordinator = coordinator,
                dispatcher = dispatcher
            )
        }

        val viewState = viewModel.state.collectAsState()

        Window(
            onCloseRequest = { },
            state = rememberWindowState(
                placement = Floating,
                position = Aligned(Alignment.TopEnd)
            ),
            undecorated = true,
            transparent = true,
            alwaysOnTop = true,
        ) {
            MainView(
                viewState = viewState.value,
                actionHandler = viewModel::handleAction
            )
        }
    }
}