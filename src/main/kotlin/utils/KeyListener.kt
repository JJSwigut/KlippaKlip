package utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener


private typealias KeyStroke = com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
@Composable
fun GlobalKeyListener(
    onShow: (Boolean) -> Unit
){

    DisposableEffect(Unit) {
        val keyListener = object : NativeKeyListener {
            private var ctrlPressed = false
            private var aPressed = false

            override fun nativeKeyPressed(e: KeyStroke?) {
                when (e?.keyCode) {
                    KeyStroke.VC_CONTROL -> ctrlPressed = true
                    KeyStroke.VC_A -> aPressed = true
                    KeyStroke.VC_ESCAPE -> onShow(false)
                }
                if (ctrlPressed && aPressed) {
                    onShow(true)
                }
            }

            override fun nativeKeyReleased(e: KeyStroke?) {
                when (e?.keyCode) {
                    KeyStroke.VC_CONTROL -> ctrlPressed = false
                    KeyStroke.VC_A -> aPressed = false
                }
            }
        }

        try {
            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeKeyListener(keyListener)
        } catch (ex: Exception) {
            println("Error registering native hook: ${ex.message}")
        }

        onDispose {
            GlobalScreen.removeNativeKeyListener(keyListener)
            try {
                GlobalScreen.unregisterNativeHook()
            } catch (ex: Exception) {
                println("Error unregistering native hook: ${ex.message}")
            }
        }
    }
}