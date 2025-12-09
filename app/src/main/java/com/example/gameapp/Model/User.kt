package com.example.gameapp.Model

class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val games: List<UserGamesView>
) {

    /**
     * Constructor secundario simplificado para el REGISTRO.
     * Permite crear un objeto User solo con los datos de entrada,
     * utilizando los valores por defecto del constructor principal para 'id' y 'games'.
     */
    constructor(username: String, email: String, password: String) :
            this(0, username, email, password, emptyList())

}