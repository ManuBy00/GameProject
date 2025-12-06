package com.example.gameapp.api

import retrofit2.http.GET
import retrofit2.http.Query

interface  RawgApiService {

    // Endpoint para obtener la lista de juegos
    // La clave de API (key) es obligatoria y se pasa como un Query Parameter
    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String,
        @Query("page") page: Int, // Para paginación
        @Query("page_size") pageSize: Int = 20,
        @Query("ordering") ordering: String = "-rating" // Ordenar por puntuación descendente
    ): GameListResponse

    // Aquí puedes añadir más funciones para otros endpoints, por ejemplo:
    // @GET("games/{id}")
    // suspend fun getGameDetails(@Path("id") gameId: Int, @Query("key") apiKey: String): GameDetail
}