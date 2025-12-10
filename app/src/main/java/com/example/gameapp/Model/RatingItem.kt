package com.example.gameapp.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable // Necesario si Game implementa Serializable

/**
 * Representa cada nivel de puntuación ('exceptional', 'recommended', etc.)
 * y el conteo asociado que viene de la API.
 */
data class RatingItem(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,      // Nombre del nivel (e.g., "Recommended", "Exceptional")
    @SerializedName("count") val count: Int,         // Número de votos
    @SerializedName("percent") val percent: Double   // Porcentaje de votos
) : Serializable

