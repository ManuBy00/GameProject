package com.example.gameapp


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gameapp.Repository.UserRepository
import com.example.gameapp.databinding.RegistroActivityBinding
import com.example.gameapp.MainActivity
import com.example.gameapp.LoginActivity
import com.example.gameapp.Model.User

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: RegistroActivityBinding


    // Inicialización segura del Repositorio
    private val userRepository: UserRepository by lazy {
        UserRepository(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Inicializar ViewBinding

        binding = RegistroActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Conectar el botón de registro a la función
        binding.registroButton.setOnClickListener {
            register()
        }

        // 3. Ajustar paddings para Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Función que maneja la lógica de validación y registro de usuarios.
     */
    private fun register(){
        val username = binding.nombreUsuario.text.toString().trim()
        val email = binding.gmail.text.toString().trim()
        val password = binding.password.text.toString()
        val password2 = binding.password2.text.toString()

        // 1. Validación de Campos Vacíos
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Validación de Coincidencia de Contraseñas
        if (password != password2) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            return
        }


        val passwordHash = password

        // 4. Validación de Email Existente
        if (userRepository.isEmailTaken(email)) {
            Toast.makeText(this, "El correo electrónico ya está registrado.", Toast.LENGTH_LONG).show()
            return
        }

        val user: User = User(username, email, passwordHash,)

        // 5. Intentar Registro
        val registrationSuccess = userRepository.registerUser(user)

        if (registrationSuccess) {
            Toast.makeText(this, "¡Registro exitoso! Ya puedes iniciar sesión.", Toast.LENGTH_LONG).show()

            // 6. Navegación: Volver a la pantalla de Login
            val intent = Intent(this, LoginActivity::class.java)
            // Esto asegura que la pila de actividades sea correcta
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        } else {
            // Este caso debería ocurrir si hay un error de base de datos interno.
            Toast.makeText(this, "Error en el registro. Inténtalo de nuevo.", Toast.LENGTH_LONG).show()
        }
    }
}
