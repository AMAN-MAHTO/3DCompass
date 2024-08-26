package com.example.a3dcompass

import androidx.lifecycle.ViewModel
import com.example.a3dcompass.di.MeasurableSensor
import com.example.a3dcompass.di.OrientationSensor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val orientationSensor: MeasurableSensor
):ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    init {
        orientationSensor.startListening()
        orientationSensor.setOnSensorValueChangedListener { value->
            _state.update {
                it.copy(
                    value = value
                )
            }
        }
    }
}

data class MainState(
    val value: List<Float> = emptyList()
)