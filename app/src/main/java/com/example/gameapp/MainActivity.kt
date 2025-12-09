package com.example.gameapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels // Importa el delegado de propiedades 'viewModels'
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager // Importa el LayoutManager
import com.example.gameapp.GameListRv.GameAdapter
import com.example.gameapp.Model.Game
import com.example.gameapp.Model.User
import com.example.gameapp.api.GameViewModel
import com.example.gameapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // 1. Declara la variable de binding para que sea accesible en toda la clase
    private lateinit var binding: ActivityMainBinding

    // 2. Declara el ViewModel de forma correcta usando el delegado de KTX
    // Esto crea y gestiona la instancia del ViewModel automáticamente
    private val gameViewModel: GameViewModel by viewModels()


    // 3. Declara una variable para el Adapter
    private lateinit var gameAdapter: GameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 4. Infla el layout usando ViewBinding y establece la vista.
        // ESTA es la forma correcta de configurar la vista con ViewBinding.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajusta los paddings para la interfaz edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 5. Llama a las funciones de configuración
        setupRecyclerView()
        setupConnectionTest()
    }

    /**
     * Configura el RecyclerView.
     */
    private fun setupRecyclerView() {
        // Inicializa el adapter
        val itemClickListener : (Game) -> Unit = { game ->
            val intent = Intent(this, GameDetailsActivity::class.java)
            intent.putExtra("game", game)
            startActivity(intent)
        }


        gameAdapter = GameAdapter(itemClickListener) // Asume que tienes una clase GameAdapter
        binding.recyclerViewGames.apply { // Asume que el ID de tu RecyclerView es 'recyclerViewGames'
            adapter = gameAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            layoutManager = GridLayoutManager(this@MainActivity, 2)
        }
    }

    /**
     * Configura los observadores para el ViewModel y la carga inicial de datos.
     */
    private fun setupConnectionTest() {
        // 6. Usa la instancia `gameViewModel` en lugar de la clase estática
        // Observador para la lista de juegos
        gameViewModel.games.observe(this) { games ->
            if (games.isNotEmpty()) {
                // Si la lista no está vacía, la conexión fue exitosa
                Toast.makeText(this, "Conexión RAWG Exitosa: ${games.size} juegos cargados.", Toast.LENGTH_LONG).show()
                // Envía la lista de juegos al adapter para que se muestren
                gameAdapter.submitList(games)
            }
        }

        // Observador para los mensajes de error
        gameViewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                // Si hay un error, lo mostramos para depurar la conexión.
                Toast.makeText(this, "Error de Conexión: $errorMessage", Toast.LENGTH_LONG).show()
            }
        }

        // 7. Inicia la carga de datos si la lista está vacía
        if (gameViewModel.games.value.isNullOrEmpty()) {
            gameViewModel.loadGames()
        }
    }
}
