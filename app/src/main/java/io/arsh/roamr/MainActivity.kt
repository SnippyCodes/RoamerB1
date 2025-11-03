package io.arsh.roamr

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.runanywhere.sdk.public.RunAnywhere
import io.arsh.roamr.ui.ChatScreen
import io.arsh.roamr.ui.ChatViewModel
import io.arsh.roamr.ui.theme.RoamrTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoamrTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val chatViewModel: ChatViewModel = viewModel()
                    val coroutineScope = rememberCoroutineScope()

                    DisposableEffect(Unit) {
                        coroutineScope.launch {
                            try {
                                val modelName = "SmolLM2 360M Q8_0"

                                val availableModels = RunAnywhere.availableModels()
                                val targetModel = availableModels.find { it.name == modelName }

                                if (targetModel != null && targetModel.isDownloaded) {
                                    val success = RunAnywhere.loadModel(targetModel.id)
                                    Log.d("MainActivity", "Model load success: $success")
                                } else {
                                    Log.w("MainActivity", "Model '$modelName' not downloaded. Chat will fail until it is.")
                                }
                            } catch (e: Exception) {
                                Log.e("MainActivity", "Error during model setup: ${e.message}")
                            }
                        }
                        onDispose {}
                    }

                    ChatScreen(viewModel = chatViewModel)
                }
            }
        }
    }
}

