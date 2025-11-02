package io.arsh.roamr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import io.arsh.roamr.ui.home.RoamrHomeScreen
import io.arsh.roamr.ui.theme.RoamrTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoamrTheme {
                val lightGradient = Brush.linearGradient(
                    colors = listOf(Color.White, Color(0xFFA8D7F6)),
                    start = Offset(0f, 400f),
                    end = Offset(400f, 0f)
                )
                val darkGradient = Brush.verticalGradient(
                    colors = listOf(Color(0xFF00008B), Color(0xFF000000))
                )

                val gradient = if (isSystemInDarkTheme()) darkGradient else lightGradient

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient)
                ) {
                    RoamrHomeScreen()
                }
            }
        }
    }
}
