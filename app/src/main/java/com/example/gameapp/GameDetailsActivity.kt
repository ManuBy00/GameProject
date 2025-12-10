package com.example.gameapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log // Importar para logs de depuración
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.echo.holographlibrary.Bar
import com.example.gameapp.Model.Game
import com.example.gameapp.Model.RatingItem
import com.example.gameapp.Utils.Constants
import com.example.gameapp.api.GameViewModel
import com.example.gameapp.databinding.GameDetailsBinding
import java.util.Locale
import kotlin.collections.ArrayList

class GameDetailsActivity : AppCompatActivity() {
    private lateinit var binding: GameDetailsBinding

    // Almacena el objeto Game completo obtenido de la API
    private var game: Game? = null

    // Clave de API obtenida de tus constantes
    private val API_KEY = Constants.constants.RAWG_API_KEY

    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = GameDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialización del ViewModel
        try {
            viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        } catch (e: Exception) {
            // Error al inicializar el ViewModel (posiblemente por inyección fallida)
            binding.errorTextView.text = "Error al inicializar el ViewModel: ${e.message}"
            binding.errorTextView.visibility = View.VISIBLE
            Log.e("GameDetailActivity", "ViewModel init failed: ${e.message}")
            return
        }

        // 1. RECUPERAR EL OBJETO GAME DEL INTENT (la versión sin ratings)
        val initialGame = intent.getSerializableExtra("game") as? Game

        // 2. Mostrar la información básica inmediatamente
        if (initialGame != null) {
            this.game = initialGame
            displayDetails(initialGame)

            // 3. Obtener el ID para buscar los detalles completos con ratings
            val gameId = initialGame.id

            // 4. Iniciar la observación del ViewModel
            observeViewModel()

            // 5. Llamar a la API para obtener la versión completa (con ratings)
            viewModel.loadGameDetails(gameId, API_KEY)

        } else {
            // Error si el objeto inicial no se pasa correctamente
            binding.errorTextView.text = "Error: Detalles básicos del juego no encontrados."
            binding.errorTextView.visibility = View.VISIBLE
            binding.detailsContainer.visibility = View.GONE
        }
    }

    /**
     * Observa los LiveData del ViewModel para actualizar la interfaz de usuario.
     */
    private fun observeViewModel() {

        // --- A. Observar estado de carga ---
        viewModel.isLoading.observe(this) { isLoading ->
            val showLoading = isLoading ?: false
            binding.loadingIndicator.visibility = if (showLoading) View.VISIBLE else View.GONE

            // Ocultamos el contenedor principal si estamos cargando, a menos que ya tengamos
            // datos iniciales mostrándose.
            if (showLoading && this.game == null) {
                binding.detailsContainer.visibility = View.GONE
            }
        }

        // --- B. Observar errores ---
        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage != null) {
                binding.errorTextView.text = "Error de carga: $errorMessage"
                binding.errorTextView.visibility = View.VISIBLE
                binding.detailsContainer.visibility = View.GONE // Ocultar detalles ante error de red
                Log.e("GameDetailActivity", "API Error: $errorMessage")
            } else {
                binding.errorTextView.visibility = View.GONE
            }
        }

        // --- C. Observar los detalles del juego (Game) ---
        viewModel.gameDetails.observe(this) { fetchedGame ->

            if (fetchedGame != null) {
                // ÉXITO: Los datos completos (incluyendo ratings) han llegado.
                this.game = fetchedGame // SOBREESCRIBIR la versión inicial

                displayDetails(fetchedGame) // Actualizar detalles por si algo cambió
                setGraph() // Dibujar el gráfico con los datos completos

                // Mostrar la vista principal y ocultar el indicador de carga/error
                binding.detailsContainer.visibility = View.VISIBLE
                binding.errorTextView.visibility = View.GONE

                Log.d("GameDetailActivity", "Detalles cargados. Ratings count: ${fetchedGame.ratings.size}")

            } else if (viewModel.error.value == null && viewModel.isLoading.value == false) {
                // Caso donde la llamada terminó sin datos y sin error explícito (raro, pero posible)
                binding.errorTextView.text = "No se pudieron obtener los detalles completos."
                binding.errorTextView.visibility = View.VISIBLE
            }
        }
    }

    fun displayDetails(game: Game) {
        // Asegurarse de que el contenedor principal es visible cuando mostramos los datos.
        binding.detailsContainer.visibility = View.VISIBLE

        binding.textViewTitle.text = game.name
        binding.textViewScore.text = "Puntuación : ${game.rating}"
        binding.textViewDeveloper.text = "Desarrollador : ${game.developer.toString()}"
        val genresString = game.genres.joinToString(", ") { it.name }
        binding.textViewGenre.text = "Género: $genresString"
        binding.textViewReleaseDate.text = "Lanzamiento: ${game.released}"

        binding.imageViewCover.load(game.backgroundImage) {
            crossfade(true)
            // error(R.drawable.logo_app)
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    /**
     * Configura el gráfico de barras utilizando la distribución de puntuaciones (ratings)
     * del objeto Game.
     */
    fun setGraph() {
        // Asegurarse de que el objeto Game está disponible
        val currentGame = this.game ?: return

        // Si la lista de ratings está vacía, ocultar el gráfico
        if (currentGame.ratings.isEmpty()) {
            binding.barGraph.visibility = View.GONE
            return
        }

        // Definir un mapa de colores para las categorías de puntuación
        val colorMap = mapOf(
            "exceptional" to Color.parseColor("#4CAF50"), // Verde (Excelente)
            "recommended" to Color.parseColor("#FFC107"), // Amarillo (Recomendado)
            "meh" to Color.parseColor("#FF9800"),        // Naranja (Aceptable)
            "skip" to Color.parseColor("#F44336")        // Rojo (Evitar)
        )

        val bars = ArrayList<Bar>()

        // 1. Iterar sobre la lista de RatingItem
        for (item in currentGame.ratings) {
            val bar = Bar()

            // 2. Establecer el valor de la barra (usamos el count/conteo de votos)
            bar.value = item.count.toFloat()

            // 3. Establecer el título/etiqueta de la barra
            bar.name = item.title

            // 4. Determinar el color basado en el título de la puntuación
            val normalizedTitle = item.title.lowercase(Locale.ROOT)
            bar.color = colorMap[normalizedTitle] ?: Color.parseColor("#2196F3") // Azul (Color por defecto)

            bars.add(bar)
        }

        // 5. Asignar la lista de barras al BarGraph
        binding.barGraph.bars = bars
        binding.barGraph.visibility = View.VISIBLE
    }
}