package com.example.a3dcompass

import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.a3dcompass.ui.theme._3DCompassTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val TAG = "Sensor"



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            _3DCompassTheme {
                Log.d(TAG, "onCreate: $isInPictureInPictureMode")
                val context = LocalContext.current
                val builder = PictureInPictureParams.Builder()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    builder.setAspectRatio(Rational(1,1)).setAutoEnterEnabled(true)
                }
                val pipModifier = Modifier.onGloballyPositioned { layoutCoordinates ->

                    context.findActivity().setPictureInPictureParams(builder.build())
                }

                DisposableEffect(context) {
                    val onUserLeaveBehavior: () -> Unit = {
                        context.findActivity()
                            .enterPictureInPictureMode(builder.build())
                    }
                    context.findActivity().addOnUserLeaveHintListener(
                        onUserLeaveBehavior
                    )
                    onDispose {
                        context.findActivity().removeOnUserLeaveHintListener(
                            onUserLeaveBehavior
                        )
                    }
                }

                Greeting(name = "3D Compass",
                    modifier = pipModifier.fillMaxSize(),
                    isInPictureInPictureMode)
            }
        }
    }


}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, isInPictureInPictureMode:Boolean, viewModel: MainViewModel = hiltViewModel(),) {

    val state = viewModel.state.collectAsState().value

    if(!isInPictureInPictureMode)
    Scaffold(modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if(state.value.isNotEmpty()) {
                var rotationX = abs(state.value[1]).coerceIn(0f, 80f)
                //    var rotationY = abs(state.collectAsState().value.value[2]).coerceIn(0f, 80f)
                var rotationZ = -state.value[0]
                Disc3d(modifier = modifier,rotationX = rotationX, rotationY = 0f, rotationZ = rotationZ, isInPictureInPictureMode)
            }
        }
    }
    else
        if(state.value.isNotEmpty()) {
            var rotationX = abs(state.value[1]).coerceIn(0f, 80f)
            //    var rotationY = abs(state.collectAsState().value.value[2]).coerceIn(0f, 80f)
            var rotationZ = -state.value[0]
            Disc3d(modifier = modifier,rotationX = rotationX, rotationY = 0f, rotationZ = rotationZ, isInPictureInPictureMode = isInPictureInPictureMode)
        }


}

