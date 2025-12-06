package com.example.gameapp.Model

data class GameListResponse(
    // Número total de elementos en la base de datos de RAWG.
    val count: Int,

    // URL para obtener la siguiente página de resultados (clave para la paginación).
    val next: String?,

    // URL para obtener la página anterior (útil para la navegación).
    val previous: String?,

    // La lista real de objetos Game. Esta es la lista que alimentarás a tu GameAdapter.
    val results: List<Game>
)