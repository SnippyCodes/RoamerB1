package io.arsh.roamr.models

import android.app.Application
import android.util.Log
import com.runanywhere.sdk.data.models.SDKEnvironment
import com.runanywhere.sdk.llm.llamacpp.LlamaCppServiceProvider
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.addModelFromURL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RunAnywhereAI: Application() {

    @Override
    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch(Dispatchers.IO) {
            init(this as Application)
        }
    }

    suspend fun init(application: Application) {
        try {
            RunAnywhere.initialize(
                context = application,
                apiKey = "dev",
                environment = SDKEnvironment.DEVELOPMENT
            )

            LlamaCppServiceProvider.register()

            registerModels()

            RunAnywhere.scanForDownloadedModels()

            Log.i("MyApp", "SDK initialized successfully")

        } catch (e: Exception) {
            Log.e("MyApp", "SDK initialization failed: ${e.message}")
        }
    }

    private suspend fun registerModels() {
        // Smallest model - great for testing (119 MB)
        addModelFromURL(
            url = "https://huggingface.co/prithivMLmods/SmolLM2-360M-GGUF/resolve/main/SmolLM2-360M.Q8_0.gguf",
            name = "SmolLM2 360M Q8_0",
            type = "LLM"
        )
//        addModelFromURL(
//            url = "https://huggingface.co/bartowski/Llama-3.2-1B-Instruct-GGUF/resolve/main/Llama-3.2-1B-Instruct-Q6_K_L.gguf",
//            name = "Llama 3.2 1B Instruct Q6_K",
//            type = "LLM"
//        )
    }

    suspend fun chat(prompt: String): String {
        return RunAnywhere.generate(prompt)
    }

}