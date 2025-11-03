package io.arsh.roamr.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.sdk.public.RunAnywhere
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isModelReady = MutableStateFlow(false)

    init {
        loadChatModel()
    }

    private fun loadChatModel() {
        _isLoading.value = true
        _messages.value = listOf(ChatMessage("Loading AI model...", isUser = false))

        viewModelScope.launch {
            try {
                val modelName = "SmolLM2 360M Q8_0"
                val availableModels = RunAnywhere.availableModels()
                val targetModel = availableModels.find { it.name == modelName }

                if (targetModel != null && targetModel.isDownloaded) {
                    val success = RunAnywhere.loadModel(targetModel.id)
                    if (success) {
                        _isModelReady.value = true
                        _messages.value = listOf(ChatMessage("Model ready! How can I help?", isUser = false))
                        Log.d("ChatViewModel", "Model load successful.")
                    } else {
                        throw Exception("Failed to load model from SDK.")
                    }
                } else {
                    Log.w("ChatViewModel", "Model '$modelName' not downloaded.")
                    _messages.value = listOf(ChatMessage("Error: Model '$modelName' is not downloaded.", isUser = false))
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error during model setup: ${e.message}")
                _messages.value = listOf(ChatMessage("Error setting up chat model: ${e.message}", isUser = false))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendMessage(text: String) {
        if (!_isModelReady.value) {
            _messages.value += ChatMessage("Please wait for the model to finish loading.", isUser = false)
            return
        }

        _messages.value += ChatMessage(text, isUser = true)
        viewModelScope.launch {
            _isLoading.value = true
            var assistantResponse = ""
            try {
                RunAnywhere.generateStream(text).collect { token ->
                    assistantResponse += token
                    val currentMessages = _messages.value.toMutableList()
                    if (currentMessages.lastOrNull()?.isUser == false) {
                        currentMessages[currentMessages.lastIndex] = ChatMessage(assistantResponse, isUser = false)
                    } else {
                        currentMessages.add(ChatMessage(assistantResponse, isUser = false))
                    }
                    _messages.value = currentMessages
                }
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message ?: "Unknown generation error"}"
                _messages.value += ChatMessage(errorMessage, isUser = false)
            }
            _isLoading.value = false
        }
    }
}