package com.example.gameapp.Repositories

import android.content.ContentValues
import android.content.Context
import com.example.gameapp.Database.DBHelper
import com.example.gameapp.Model.Game
import com.example.gameapp.Model.GameListResponse
import com.example.gameapp.Utils.Constants
import com.example.gameapp.api.RawgApiService
import com.example.gameapp.api.RetroFitClient
import java.io.IOException

/**
 * Repositorio de datos que actúa como intermediario entre el ViewModel y la fuente de datos (API de RAWG).
 * Es responsable de hacer la llamada de red, inyectar la clave API y manejar excepciones de red.
 */
class GameRepository(
    private val context: Context,
    private val apiService: RawgApiService = RetroFitClient.apiService

) {
    private val dbHelper = DBHelper(context)


    /**
     * Función que obtiene una lista de juegos desde la API de RAWG.
     * @param page Número de página a solicitar (para paginación).
     * @return GameListResponse que contiene la lista de juegos y metadatos.
     * @throws java.io.IOException Si ocurre un error de red o la respuesta no es exitosa.
     */
    suspend fun fetchGames(page: Int = 1): GameListResponse {

        // El bloque try-catch es crucial para manejar fallos de red (ej. sin conexión)
        // o respuestas HTTP que no son 200 (ej. 404, 500).
        try {
            // Llama a la función de Retrofit, inyectando la clave API
            // y los parámetros de paginación/ordenación.
            val response = apiService.getGames(
                apiKey = Constants.constants.RAWG_API_KEY,
                page = page,
                pageSize = 20, // Usa el valor por defecto definido en la interfaz
                ordering = "popularity" // Usa el valor por defecto definido en la interfaz
            )

            // Si Retrofit devuelve una respuesta, la retornamos
            return response

        } catch (e: Exception) {

            throw IOException("Error al conectar con la API de RAWG: ${e.message}", e)
        }
    }

    /**
     * Obtiene los detalles completos de un juego de la API, incluyendo la
     * distribución de puntuaciones (ratings).
     *
     * @param gameId El ID del juego a buscar.
     * @param apiKey La clave de API de RAWG.
     * @return El objeto Game completo.
     */
    suspend fun getGameDetails(gameId: Int, apiKey: String): Game {
        return apiService.getGameDetails(gameId, apiKey)
    }

    // MÉTODOS BASE DE DATOS

    /**
     * Inserta un juego en la tabla TABLE_GAME (caché) si no existe.
     * @param apiGame El objeto Game a insertar.
     */
    fun insertGame(apiGame: Game): Boolean {
        val db = dbHelper.writableDatabase
        // Convierte la List<Genre> a una String única: "Action, Indie, RPG"
        val genresString = apiGame.genres.joinToString(separator = ", ") {
            it.name
        }

        //  Mapear el objeto Game a ContentValues
        val values = ContentValues().apply {
            put(DBHelper.Companion.COL_GAME_ID, apiGame.id)
            put(DBHelper.Companion.COL_GAME_NAME, apiGame.name)
            put(DBHelper.Companion.COL_GAME_BACKGROUND_IMAGE, apiGame.backgroundImage)
            // Inserta la String mapeada en la columna de géneros
            put(DBHelper.Companion.COL_GAME_GENRES, genresString)
        }

        //  Intentar insertar el juego
        val resultado = db.insert(DBHelper.Companion.TABLE_GAME, null, values)
        db.close()

        return resultado != -1L
    }

}