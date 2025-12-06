package com.example.gameapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroFitClient {
    // La URL base de la API de RAWG (Corregido el formato del enlace)
    private const val BASE_URL = "[https://api.rawg.io/api/](https://api.rawg.io/api/)"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Usamos Gson para convertir JSON a nuestras Data Classes
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Inicializa el servicio una vez que Retrofit está construido
    val apiService: RawgApiService by lazy {
        retrofit.create(RawgApiService::class.java)
    }

}