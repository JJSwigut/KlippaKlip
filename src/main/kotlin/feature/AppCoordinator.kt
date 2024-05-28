package feature

import cafe.adriel.voyager.navigator.Navigator
import feature.tray.MenuOutput
import feature.tray.MenuOutput.Exit
import feature.tray.MenuOutput.ShowCreate
import feature.tray.MenuOutput.ShowKlips
import feature.tray.MenuOutput.ShowSettings
import repository.KlipRepoImpl

class AppCoordinator(
    val onExit: () -> Unit,
    val repo: KlipRepoImpl
) {

    private lateinit var navigator: Navigator

    fun handleOutput(output: Output){

    }

    fun setNavigator(navigator: Navigator){
        this.navigator = navigator
    }

    fun handleMenuOutput(output: MenuOutput){
        when(output){
            Exit -> onExit()
            ShowCreate -> TODO()
            ShowKlips -> TODO()
            ShowSettings -> TODO()
        }
    }
}

interface Output