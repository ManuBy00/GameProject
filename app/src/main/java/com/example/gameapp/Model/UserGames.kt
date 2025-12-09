package com.example.gameapp.Model

class UserGames( val userIdFk: Int,

    // Clave Foránea del juego al que se aplica la calificación
                 val gameIdFk: Int,

    // La puntuación real dada por el usuario (Real/Float en SQLite)
                 val rating: Float ) {
}