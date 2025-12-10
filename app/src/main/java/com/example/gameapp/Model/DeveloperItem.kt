package com.example.gameapp.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
data class DeveloperItem(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    // Agrega otros campos si son necesarios (slug, games_count, etc.)
): Serializable {

    override fun toString(): String {
        return name
    }
}

