package base

import feature.Output
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class DesktopViewModel<T>(
    initialState: T,
    private val output: (Output) -> Unit = {},
) {
    private val viewModelJob = Job()
    protected val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<T> get() = _state.asStateFlow()

    protected suspend fun updateState(state: T.() -> T) {
        val newState = state.invoke(_state.value)
        _state.emit(newState)
    }

    protected fun sendOutput(output: Output) {
        output(output)
    }

    fun onDestroy() {
        viewModelScope.cancel()
        onCleared()
    }

    protected open fun onCleared() {
        // Override to clean up resources if needed
    }
}