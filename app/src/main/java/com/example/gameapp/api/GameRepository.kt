package com.example.gameapp.api

import com.example.gameapp.Model.GameListResponse
import com.example.gameapp.api.RawgApiService
import com.example.gameapp.api.RetroFitClient


import java.io.IOException

const val RAWG_API_KEY = "ec82805bd3eb46d89a877b5c08d233ec"
/**
 * Repositorio de datos que actúa como intermediario entre el ViewModel y la fuente de datos (API de RAWG).
 * Es responsable de hacer la llamada de red, inyectar la clave API y manejar excepciones de red.
 */
class GameRepository(
    // Inyección de la interfaz del servicio de Retrofit.
    // Por defecto, usa la instancia Singleton del NetworkModule.
    private val apiService: RawgApiService = RetroFitClient.apiService
) {


    /**
     * Función que obtiene una lista de juegos desde la API de RAWG.
     * @param page Número de página a solicitar (para paginación).
     * @return GameListResponse que contiene la lista de juegos y metadatos.
     * @throws IOException Si ocurre un error de red o la respuesta no es exitosa.
     */
    suspend fun fetchGames(page: Int = 1): GameListResponse {

        // El bloque try-catch es crucial para manejar fallos de red (ej. sin conexión)
        // o respuestas HTTP que no son 200 (ej. 404, 500).
        try {
            // Llama a la función de Retrofit, inyectando la clave API
            // y los parámetros de paginación/ordenación.
            val response = apiService.getGames(
                apiKey = RAWG_API_KEY,
                page = page,
                pageSize = 20, // Usa el valor por defecto definido en la interfaz
                ordering = "-rating" // Usa el valor por defecto definido en la interfaz
            )

            // Si Retrofit devuelve una respuesta, la retornamos
            return response

        } catch (e: Exception) {

            throw IOException("Error al conectar con la API de RAWG: ${e.message}", e)
        }
    }
}