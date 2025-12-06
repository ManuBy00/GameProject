package com.example.gameapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gameapp.Model.Game
import com.example.gameapp.Model.GameListResponse

import com.example.gameapp.databinding.ActivityMainBinding
import com.example.gameapp.api.GameViewModel

import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    /**
     * Configuración simplificada para la prueba de conexión:
     * 1. Observar solo la lista de juegos.
     * 2. Observar los errores para saber si la conexión falló.
     * 3. Iniciar la carga de datos.
     */
    private fun setupConnectionTest() {

        // Observador para la lista de juegos

        GameViewModel.games.observe(this) { games ->
            if (games.isNotEmpty()) {
                // Si la lista no está vacía, ¡la conexión fue exitosa!
                Toast.makeText(this, "Conexión RAWG Exitosa: ${games.size} juegos cargados.", Toast.LENGTH_LONG).show()
                gameAdapter.submitList(games)
            }
        }

        // Observador para los mensajes de error
        GameViewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                // Si hay un error, lo mostramos para depurar la conexión.
                Toast.makeText(this, "Error de Conexión: $errorMessage", Toast.LENGTH_LONG).show()
            }
        }

        // 4. Iniciar la carga de datos
        // Esta es la única vez que la Activity interactúa directamente con la lógica de negocio.
        if (GameViewModel.games.value.isNullOrEmpty()) {
            GameViewModel.loadGames()
        }
    }
}