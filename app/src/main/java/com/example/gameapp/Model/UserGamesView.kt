package com.example.gameapp.Model

data class UserGamesView( val gameId: Int,
                          val name: String,
                          val backgroundImage: String?,
                          val genres: String?,
                          val released: String?,
                          val developer: String?,// Añadido para mostrar la fecha
                          val userRating: Double,
                          val gameRating: Double
                        )
