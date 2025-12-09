package com.example.gameapp.Model

import java.io.Serializable

data class Genre(
    val id: Int,
    val name: String,
    val slug: String
) : Serializable