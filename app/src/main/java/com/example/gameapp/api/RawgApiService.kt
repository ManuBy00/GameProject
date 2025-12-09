package com.example.gameapp.api

import com.example.gameapp.Model.Game
import com.example.gameapp.Model.GameListResponse
import com.example.gameapp.Utils.Constants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface  RawgApiService {
    // Endpoint para obtener la lista de juegos
    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String,
        @Query("page") page: Int, // Para paginación
        @Query("page_size") pageSize: Int = 20,
        @Query("ordering") ordering: String // Ordenar por puntuación descendente
    ): GameListResponse

    //@GET("games")
    suspend fun getGamesByString(
        @Query("key") apiKey: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 20,
        @Query("ordering") ordering: String = "-added",
        @Query("search") search: String? = null // Permite buscar por nombre
    ): GameListResponse

    /**
     * Obtiene los detalles completos de un juego específico usando su ID.
     * Este endpoint es el que incluye la distribución de puntuaciones (campo 'ratings').
     *
     * @param gameId El ID único del juego.
     * @param apiKey Tu clave de API para RAWG.
     * @return El objeto Game con todos sus detalles, incluyendo la lista 'ratings'.
     */
    @GET("games/{id}")
    suspend fun getGameDetails(
        @Path("id") gameId: Int, // {id} en la URL se reemplaza por gameId
        @Query("key") apiKey: String // Tu clave de API
    ): Game

}