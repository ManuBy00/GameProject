package com.example.gameapp.api

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameapp.Model.Game
import com.example.gameapp.Repositories.GameRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * ViewModel que maneja la lógica de negocio y la gestión de datos
 * para la lista de juegos, sobreviviendo a los cambios de configuración.
 */
public class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository = GameRepository(application)

    // MutableLiveData para la lista de juegos (el contenido sí es no nulo)
    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>> = _games

    // LiveData que contendrá el objeto Game completo (incluyendo los ratings)
    private val _gameDetails = MutableLiveData<Game?>()
    val gameDetails: LiveData<Game?> = _gameDetails

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
        _error.value = null

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
                }
            }
        }
    }
    /**
     * Inicia la carga de los detalles del juego.
     * @param gameId El ID del juego.
     * @param apiKey La clave de la API.
     */
    fun loadGameDetails(gameId: Int, apiKey: String) {
        // Usamos viewModelScope para ejecutar la corrutina de forma segura
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null // Limpiamos errores anteriores

            try {
                // Llama al repositorio, que a su vez llama a la API
                val details = repository.getGameDetails(gameId, apiKey)

                // Actualiza el LiveData con los detalles del juego
                _gameDetails.value = details

            } catch (e: Exception) {
                // Manejo de cualquier error de red o de parseo
                _error.value = "Error al cargar los detalles: ${e.localizedMessage}"
                _gameDetails.value = null // Limpiamos los datos

            } finally {
                _isLoading.value = false
            }
        }
    }

}