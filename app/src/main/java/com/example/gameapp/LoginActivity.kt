package com.example.gameapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gameapp.Database.DBHelper
import com.example.gameapp.Model.User
import com.example.gameapp.Repository.UserRepository
import com.example.gameapp.Utils.Sesion
import com.example.gameapp.databinding.LoginActivityBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding
    private lateinit var userRepository: UserRepository

    private lateinit var dbHelper: DBHelper



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //  Infla el layout usando ViewBinding y establece la vista.
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userRepository = UserRepository(applicationContext)
        dbHelper = DBHelper(applicationContext)

        binding.loginButton.setOnClickListener {
            login()
        }

        binding.registrarText.setOnClickListener {
            abrirRegistro()
        }

        // Ajusta los paddings para la interfaz edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.loginMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun login(){
        if (binding.email.text.toString().isEmpty() || binding.password.text.toString().isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        val userLoged : User?

        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        val UserIdLoged:Int = userRepository.loginUser(email, password)

        if (UserIdLoged != -1) {
            userLoged = dbHelper.getUserById(UserIdLoged)
            Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()

            Sesion.getInstance().logIn(userLoged)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }else{
            Toast.makeText(this, "Login fallido", Toast.LENGTH_SHORT).show()
        }
    }

    fun abrirRegistro(){
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }
}
