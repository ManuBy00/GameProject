package com.example.gameapp.Repository

import android.content.Context
import com.example.gameapp.Database.DBHelper
import com.example.gameapp.Model.User
import java.io.IOException

/**
 * Repositorio de usuarios.
 * Responsable de gestionar la autenticación (registro, login) con la base de datos local (SQLite).
 */
class UserRepository(private val context: Context) {

    // Inicialización del Helper de SQLite. Este repositorio usa los métodos DAO del DBHelper.
    private val dbHelper = DBHelper(context)

    /**
     * Intenta registrar un nuevo usuario en la base de datos.
     * * @param userEmail El correo electrónico del usuario.
     * @param username El nombre de usuario.
     * @param passwordHash La contraseña ya hasheada (por seguridad, nunca guardar texto plano).
     * @return true si el registro fue exitoso, false si el email ya existía.
     * @throws IOException Si ocurre un error de base de datos.
     */
    fun registerUser(User: User): Boolean {
        try {
            // Utilizamos el método de bajo nivel definido en DBHelper
            return dbHelper.insertUser(User)
        } catch (e: Exception) {
            // Lanza una excepción si hay un problema con la DB (ej. disco lleno)
            throw IOException("Error al registrar el usuario en la base de datos.", e)
        }
    }

    /**
     * Intenta iniciar sesión con las credenciales proporcionadas.
     * * @param userEmail El correo electrónico del usuario.
     * @param passwordHash La contraseña hasheada para la verificación.
     * @return El ID del usuario si las credenciales son válidas, o -1 si fallan.
     * @throws IOException Si ocurre un error de base de datos.
     */
    fun loginUser(userEmail: String, passwordHash: String): Int {
        try {
            // Utilizamos el método de bajo nivel para verificar las credenciales
            return dbHelper.checkUserCredentials(userEmail, passwordHash)
        } catch (e: Exception) {
            throw IOException("Error al verificar credenciales en la base de datos.", e)
        }
    }

    /**
     * Comprueba si un correo electrónico ya está en uso.
     * * @param userEmail El correo electrónico a verificar.
     * @return true si el correo electrónico ya existe, false si está disponible.
     * @throws IOException Si ocurre un error de base de datos.
     */
    fun isEmailTaken(userEmail: String): Boolean {
        try {
            return dbHelper.checkEmailExists(userEmail)
        } catch (e: Exception) {
            throw IOException("Error al consultar la base de datos.", e)
        }
    }

    // NOTA: En un flujo real, deberías añadir un método para obtener todos los datos
    // del usuario (username, email) una vez que el login sea exitoso.
}