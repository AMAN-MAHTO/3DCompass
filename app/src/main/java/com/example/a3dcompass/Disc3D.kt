package com.example.a3dcompass

import android.app.PictureInPictureParams
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Paint
import android.graphics.RuntimeShader
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin


val TAG = "DISC3D"


@Preview
@Composable
fun Prev86798(modifier: Modifier = Modifier) {
    val state = remember {
        mutableStateOf(
            MainState(
                value = listOf(40f, 70f, 0f)
            )
        )
    }
    Disc3d(
        rotationX = 70f,
        rotationY = 0f,
        rotationZ = 50f,
        isInPictureInPictureMode = false
    )
}

internal fun Context.findActivity(): ComponentActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Picture in picture should be called in the context of an Activity")
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Disc3d(modifier: Modifier = Modifier, rotationX: Float, rotationY: Float, rotationZ: Float, isInPictureInPictureMode:Boolean) {

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
           ,
        contentAlignment = Alignment.Center
    ) {


        Log.d(TAG,"boxwithconstrain: maxWidth: ${maxWidth}  maxHeight: $maxHeight")

        val progressX = (rotationX / 80)
        val boxShape = CircleShape
        val height = (maxHeight.value*0.07 ).toInt() // 50
        val depth = height / 2

//        val infinitetransition = rememberInfiniteTransition(label = "")
//        val color by infinitetransition.animateColor(
//            initialValue = Color.White,
//            targetValue = Color.Blue,
//            animationSpec = infiniteRepeatable(
//                animation = tween(2000),
//                repeatMode = RepeatMode.Reverse
//            ), label = ""
//        )
        val color = Color.White
        if(!isInPictureInPictureMode)
            Column(
                modifier = Modifier
                    .padding(top = 64.dp)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val deg = round(-rotationZ)
                val direction = when {
                    deg == 0f -> "North"
                    deg in 1f..89f -> "North East"
                    deg == 90f -> "East"
                    deg in 91f..179f -> "South East"
                    deg == 180f -> "South"
                    deg in 181f..269f -> "South West"
                    deg == 270f -> "West"
                    deg in 271f..359f -> "North West"
                    deg == 360f -> "North"
                    else -> "Direction"
                }
                Text(text = direction, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                var r = round(abs(rotationZ) % 90)

                if(abs(rotationZ).toInt() in 90..180)  r = 90-r
                if(abs(rotationZ).toInt() in 270..360)  r = 90-r


                Text(
                    text = r.toString() + "°",
                    style = MaterialTheme.typography.titleLarge
                )
            }

        for (i in height downTo 0) {
            var c = (i / height.toFloat())

            BoxWithConstraints(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(boxShape)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding((maxWidth.value * 0.1).dp)
                    .aspectRatio(1f)
                    .graphicsLayer(
                        translationY = 2 * i * progressX,
                        rotationX = rotationX,
                        rotationY = 0f,
                        cameraDistance = 16f,
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
                    )
                    .background(Color.Transparent)
                    .border(
                        (maxWidth.value * 0.05).dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                color.copy(red = c, alpha = 1f - c),
//                                color.copy(blue = c, alpha = 1f - c),
                                color.copy(green = c, alpha = 1f - c)
                            )
                        ),
                        shape = boxShape
                    )

            ) {
                // background
                if (i == depth.toInt()) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .clip(boxShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .fillMaxSize(.92f)
                            .padding((maxWidth.value * 0.1).dp), contentAlignment = Alignment.Center
                    ) {
                        val textStyle = MaterialTheme.typography.titleLarge.copy(
                            fontSize = (maxWidth.value * 0.1).sp
                        )
                        val textColor = MaterialTheme.colorScheme.onSurfaceVariant
                        Text(
                            text = "N", modifier = Modifier
                                .align(Alignment.TopCenter), style = textStyle, color = textColor
                        )
                        Text(
                            text = "S", modifier = Modifier
                                .align(Alignment.BottomCenter), style = textStyle, color = textColor
                        )
                        Text(
                            text = "W", modifier = Modifier
                                .align(Alignment.CenterStart), style = textStyle, color = textColor
                        )
                        Text(
                            text = "E", modifier = Modifier
                                .align(Alignment.CenterEnd), style = textStyle, color = textColor
                        )

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val textPaint = Paint().apply {
                                textAlign = Paint.Align.CENTER
                                textSize = (size.minDimension * 0.05f)
                                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                            }
                            textPaint.color = textColor.toArgb()


                            val radius = maxWidth.value + maxWidth.value*0.1
                            val centerX = size.width / 2
                            val centerY = size.height / 2
                            // Draw angles like a clock
                            val angles = 12

                            for(i in 0..angles-1) {

                                val angleInDegrees = (360/angles) * (i + 1)
                                val angleInRadians = Math.toRadians(angleInDegrees.toDouble())

                                var humbalAngle = (angleInDegrees% 90)
                                if(angleInDegrees.toInt() in 90..180)  humbalAngle = 90-humbalAngle
                                if(angleInDegrees.toInt() in 270..360)  humbalAngle = 90-humbalAngle

                                val x =
                                    (centerX + (radius * 0.85) * cos(angleInRadians)).toFloat()
                                val y =
                                    (centerY + (radius * 0.85) * sin(angleInRadians)).toFloat()
                                // Measure the text width and height
                                val textWidth = textPaint.measureText(angleInDegrees.toString())
                                val textHeight = textPaint.descent() - textPaint.ascent()
                                    rotate(-90f) {
                                        drawContext.canvas.nativeCanvas.apply {
                                            save()
                                            rotate(angleInDegrees.toFloat(), x, y)
//                                            drawCircle(Color.Red,radius = 1f,center = Offset(x,y))
                                            if(angleInDegrees !in listOf(0,90,180,270,360))
                                                drawText(humbalAngle.toString()+"°", x - textWidth / 2, y + textHeight / 4, textPaint)
                                            restore()
                                        }
                                    }

                            }

                        }
                    }
                }

                // nedal
                if (i <= depth) {
                    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

                    val f = sin((i) / depth.toFloat() * 3 + 0.07).toFloat()
                    val heightProgress = f.pow(5)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val needleWidth = size.width * 0.06f  // 50f,
                        val needleHeight = size.height * 0.35f // 200f
                        val path = Path().apply {
                            moveTo(center.x - needleWidth * heightProgress, center.y)
                            lineTo(center.x + needleWidth * heightProgress, center.y)
                            lineTo(center.x, center.y + needleHeight * heightProgress)
                            close()
                        }
                        val rgb = (1f * heightProgress).coerceIn(0.5f, 1f)
                        rotate(-rotationZ) {
                            drawPath(
                                path,
                                color = Color.Gray.copy(
                                    red = rgb,
                                    blue = rgb,
                                    green = rgb
                                )
                            )
                            rotate(180f) {
                                drawPath(path, color = Color.Red.copy(red = rgb))

                            }
                        }

                    }

                }


            }

        }
    }
}
