package com.example.gameapp.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameapp.Model.Game

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * ViewModel que maneja la lógica de negocio y la gestión de datos
 * para la lista de juegos, sobreviviendo a los cambios de configuración.
 */
public class GameViewModel(
    private val repository: GameRepository = GameRepository()
) : ViewModel() {

    // MutableLiveData para la lista de juegos (el contenido sí es no nulo)
    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>> = _games

    // CORRECCIÓN: LiveData para el estado de la carga debe ser Boolean? (acepta null)
    private val _isLoading = MutableLiveData<Boolean?>(false) // Inicializado a false
    val isLoading: LiveData<Boolean?> = _isLoading

    // CORRECCIÓN: LiveData para mensajes de error debe ser String? (acepta null)
    private val _error = MutableLiveData<String?>(null) // Inicializado a null
    val error: LiveData<String?> = _error

    // Función que la Activity llamará para iniciar la carga
    fun loadGames() {
        // Verificar si ya está cargando para evitar llamadas duplicadas
        if (_isLoading.value == true) return

        // 1. Establecer el estado de carga
        _isLoading.value = true
        _error.value = null // Ahora se permite asignar null a String?

        // 2. Iniciar una Corrutina usando el scope del ViewModel.
        // El trabajo de red se lanza en el hilo de IO
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 3. Llamar al Repositorio (suspende el hilo de IO)
                val response = repository.fetchGames(page = 1)

                // 4. Cambiar al Hilo Principal para actualizar el LiveData
                withContext(Dispatchers.Main) {
                    _games.value = response.results
                    _isLoading.value = false // Finaliza la carga
                }
            } catch (e: IOException) {
                // 5. Manejar errores de red o API en el hilo principal
                withContext(Dispatchers.Main) {
                    _error.value = "Error de conexión: ${e.message}"
                    _isLoading.value = false // Finaliza la carga incluso con error
                    _games.value = emptyList() // Opcional: limpiar la lista en caso de error
                }
            }
        }
    }
}