package utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.WindowPosition.PlatformDefault.x
import androidx.compose.ui.window.WindowPosition.PlatformDefault.y
import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import data.models.KlipSettings
import data.models.listenerCloseKeys
import data.models.listenerOpenKeys
import feature.Output
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent
import repository.settings.Prefs.settings


@Composable
fun GlobalKeyListener(
    openKeys: List<Int>,
    closeKeys: List<Int>,
    onOutput: (KeyListenerOutput) -> Unit
) {
    val keyListenerManager = remember { KeyListenerManager(onOutput) }

    LaunchedEffect(openKeys, closeKeys) {
        keyListenerManager.updateKeys(openKeys, closeKeys)
    }

    DisposableEffect(Unit) {
        keyListenerManager.startListening()
        onDispose {
            keyListenerManager.stopListening()
        }
    }
}

sealed interface KeyListenerOutput: Output {
    data class ShowKlips(val shouldShow: Boolean) : KeyListenerOutput
}

class KeyListenerManager(
    private val onOutput: (KeyListenerOutput) -> Unit
) {
    private var openKeys = emptyList<Int>()
    private var closeKeys = emptyList<Int>()
    private val pressedKeys = mutableSetOf<Int>()

    private val keyListener = object : NativeKeyListener {
        override fun nativeKeyPressed(e: NativeKeyEvent?) {
            e?.keyCode?.let { keyCode ->
                if (pressedKeys.add(keyCode)) {
                    if (pressedKeys.containsAll(openKeys)) {
                        onOutput(KeyListenerOutput.ShowKlips(true))
                    }
                    if (pressedKeys.containsAll(closeKeys)) {
                        onOutput(KeyListenerOutput.ShowKlips(false))
                    }
                }
            }
        }

        override fun nativeKeyReleased(e: NativeKeyEvent?) {
            e?.keyCode?.let { keyCode ->
                pressedKeys.remove(keyCode)
            }
        }
    }

    fun updateKeys(newOpenKeys: List<Int>, newCloseKeys: List<Int>) {
        openKeys = newOpenKeys
        closeKeys = newCloseKeys
    }

    fun startListening() {
        try {
            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeKeyListener(keyListener)
        } catch (ex: Exception) {
            println("Error registering native hook: ${ex.message}")
        }
    }

    fun stopListening() {
        try {
            GlobalScreen.removeNativeKeyListener(keyListener)
            GlobalScreen.unregisterNativeHook()
        } catch (ex: Exception) {
            println("Error unregistering native hook: ${ex.message}")
        }
    }
}