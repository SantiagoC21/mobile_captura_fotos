package com.facialdataset.frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facialdataset.frontend.api.ApiClient
import com.facialdataset.frontend.api.models.PersonaCreate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

data class CameraUiState(
    val fotos: List<ByteArray> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val uploadSuccess: Boolean = false,
    val personaId: Int = -1
)

class CameraViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun agregarFoto(bytes: ByteArray) {
        _uiState.value = _uiState.value.copy(
            fotos = _uiState.value.fotos + bytes
        )
    }

    fun eliminarFoto(index: Int) {
        val nuevaLista = _uiState.value.fotos.toMutableList()
        nuevaLista.removeAt(index)
        _uiState.value = _uiState.value.copy(fotos = nuevaLista)
    }

    fun limpiarFotos() {
        _uiState.value = CameraUiState()
    }

    fun setLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = loading)
    }

    fun setError(message: String?) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }

    fun setUploadSuccess(success: Boolean) {
        _uiState.value = _uiState.value.copy(uploadSuccess = success)
    }

    // Verifica si existe y crea si no, retorna el personaId
    fun verificarOCrearPersona(
        nombre: String,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                setLoading(true)
                val verificar = ApiClient.instance.verificarPersona(nombre)

                if (verificar.isSuccessful && verificar.body() != null) {
                    val body = verificar.body()!!
                    if (body.existe && body.persona_id != null) {
                        onError("Ya existe una persona con ese nombre")
                        return@launch
                    }
                }

                // No existe, la creamos
                val crear = ApiClient.instance.crearPersona(PersonaCreate(nombre))
                if (crear.isSuccessful && crear.body() != null) {
                    val personaId = crear.body()!!.id
                    _uiState.value = _uiState.value.copy(personaId = personaId)
                    onSuccess(personaId)
                } else {
                    onError("Error al crear persona")
                }

            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    // Envía todas las fotos al backend
    fun enviarFotos(
        personaId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                setLoading(true)
                val fotos = _uiState.value.fotos

                fotos.forEachIndexed { index, bytes ->
                    val requestBody = bytes.toRequestBody("image/jpeg".toMediaType())
                    val part = MultipartBody.Part.createFormData(
                        "imagen",
                        "foto_$index.jpg",
                        requestBody
                    )
                    val response = ApiClient.instance.enviarFoto(personaId, part)
                    if (!response.isSuccessful) {
                        onError("Error enviando foto ${index + 1}")
                        return@launch
                    }
                }

                setUploadSuccess(true)
                onSuccess()

            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }
}