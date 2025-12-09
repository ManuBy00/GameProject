package com.example.gameapp

import android.graphics.Color
import android.os.Bundle
import android.view.View // Se necesita para View.VISIBLE/GONE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider // Importamos ViewModelProvider
import coil.load
import com.echo.holographlibrary.Bar
import com.example.gameapp.Model.Game
import com.example.gameapp.Model.RatingItem
import com.example.gameapp.Utils.Constants
import com.example.gameapp.api.GameViewModel // Correcto: la clase que extiende AndroidViewModel
import com.example.gameapp.databinding.GameDetailsBinding
import java.util.Locale
import kotlin.collections.ArrayList

class GameDetailsActivity : AppCompatActivity() {
    private lateinit var binding: GameDetailsBinding

    // Almacena el objeto Game completo obtenido de la API
    private var game: Game? = null

    // Clave de API obtenida de tus constantes
    private val API_KEY = Constants.constants.RAWG_API_KEY

    // 1. Declaramos el ViewModel
    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = GameDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Inicialización del ViewModel usando ViewModelProvider (el Factory por defecto
        // para AndroidViewModel maneja la dependencia de Application)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        // 3. OBTENER EL ID del juego de los extras del Intent
        val gameId = intent.getIntExtra("game_id", -1)

        // 4. Iniciar la observación de los datos
        observeViewModel()

        if (gameId != -1) {
            // 5. Cargar los detalles completos del juego desde la API
            viewModel.loadGameDetails(gameId, API_KEY)
        } else {

        }
    }

    /**
     * Observa los LiveData del ViewModel para actualizar la interfaz de usuario.
     */
    private fun observeViewModel() {

        // --- A. Observar estado de carga ---
        viewModel.isLoading.observe(this) { isLoading ->
            // Si isLoading es true, mostramos el indicador y ocultamos el resto
            val showLoading = isLoading ?: false // Usamos false si es null
            binding.loadingIndicator.visibility = if (showLoading) View.VISIBLE else View.GONE

            // Ocultamos el contenedor principal si estamos cargando o hay un error
            if (showLoading) {
                binding.detailsContainer.visibility = View.GONE
            }
        }

        // --- B. Observar errores ---
        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage != null) {
                binding.errorTextView.text = "Error de carga: $errorMessage"
                binding.errorTextView.visibility = View.VISIBLE
                binding.detailsContainer.visibility = View.GONE // Ocultar detalles al mostrar error
            } else {
                binding.errorTextView.visibility = View.GONE
            }
        }

        // --- C. Observar los detalles del juego (Game) ---
        viewModel.gameDetails.observe(this) { fetchedGame ->
            this.game = fetchedGame // Guardar el objeto completo localmente

            if (fetchedGame != null) {
                // 4. Mostrar los detalles y el gráfico
                displayDetails(fetchedGame)
                setGraph()

                // Aseguramos que se muestre el contenedor principal y se oculte el error
                binding.detailsContainer.visibility = View.VISIBLE
                binding.errorTextView.visibility = View.GONE
            } else if (viewModel.error.value == null) {
                // Caso donde es null pero no hay un error específico (ej. inicio)
                binding.detailsContainer.visibility = View.GONE
            }
        }
    }

    fun displayDetails(game: Game) {
        binding.textViewTitle.text = game.name
        binding.textViewScore.text = "Puntuación : ${game.rating}"
        binding.textViewDeveloper.text = "Desarrollador : ${game.developer}"
        val genresString = game.genres.joinToString(", ") { it.name }
        binding.textViewGenre.text = "Género: $genresString"
        binding.textViewReleaseDate.text = "Lanzamiento: ${game.released}"

        binding.imageViewCover.load(game.backgroundImage) {
            crossfade(true)
            // Agrega tu recurso de fallback aquí si es necesario, por ejemplo:
            // error(R.drawable.placeholder_game)
        }
    }

    /**
     * Configura el gráfico de barras utilizando la distribución de puntuaciones (ratings)
     * del objeto Game.
     */
    fun setGraph() {
        // Asegurarse de que el objeto Game está disponible
        val currentGame = this.game ?: return

        // Definir un mapa de colores para las categorías de puntuación
        val colorMap = mapOf(
            // Colores basados en la semántica de la puntuación
            "exceptional" to Color.parseColor("#4CAF50"), // Verde (Excelente)
            "recommended" to Color.parseColor("#FFC107"), // Amarillo (Recomendado)
            "meh" to Color.parseColor("#FF9800"),        // Naranja (Aceptable)
            "skip" to Color.parseColor("#F44336")        // Rojo (Evitar)
            // Cualquier otro se manejará con el color por defecto
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
        if (bars.isNotEmpty()) {
            binding.barGraph.bars = bars
            binding.barGraph.visibility = View.VISIBLE
            // Opcional: Animar las barras si la librería lo soporta
            // binding.barGraph.animateToGoalValues()
        } else {
            // Ocultar el gráfico si no hay datos
            binding.barGraph.visibility = View.GONE
        }
    }
}