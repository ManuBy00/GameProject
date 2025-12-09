package com.example.gameapp.Model

import com.google.gson.annotations.SerializedName

data class Game(@SerializedName("id") val id: Int,
                @SerializedName("name") val name: String,
                @SerializedName("released") val released: String?, // Fecha de lanzamiento
                @SerializedName("background_image") val backgroundImage: String?, // URL de la imagen
                @SerializedName("rating") val rating: Double, // Puntuación)
                @SerializedName("developers") val developer: String, // Desarrollador
                @SerializedName("genres",) val genres: List<Genre> = emptyList(),
                @SerializedName("ratings") val ratings: List<RatingItem> = emptyList(), // distribución de las puntuaciones
                ): java.io.Serializable// Géneros (lista de genres)




