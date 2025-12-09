package com.example.gameapp.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.gameapp.Model.Game
import com.example.gameapp.Model.User
import com.example.gameapp.Model.UserGamesView

// Se cambia 'public class' por 'class'
class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // --- Definición de Tablas y Columnas ---
    companion object {
        private const val DATABASE_NAME = "GAME_PROJECT"
        private const val DATABASE_VERSION = 3

        // Tabla de Usuarios
        const val TABLE_USER = "user"
        const val COL_USER_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_EMAIL = "email"
        const val COL_PASSWORD = "password"

        // Tabla de Juegos del Usuario (Relación: Usuario | Juego | Puntuación)
        const val TABLE_USER_GAMES = "user_games"
        const val COL_UG_USER_ID_FK = "user_id_fk"
        const val COL_UG_GAME_ID_FK = "game_id_fk"
        const val COL_UG_RATING = "rating"

        // Tabla para almacenar los datos de los juegos (caché)
        const val TABLE_GAME = "game"
        const val COL_GAME_ID = "id"
        const val COL_GAME_NAME = "name"
        const val COL_GAME_BACKGROUND_IMAGE = "background_image"

        const val COL_GAME_RATING = "rating"
        const val COL_GAME_GENRES = "genres"
        const val COL_GAME_RELEASED = "released"
        const val COL_GAME_DEVELOPER = "developer"

        const val COL_GAME_RATINGS = "ratings"

    }

    // --- Lógica de Creación de Tablas ---

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Crear la Tabla de Usuarios
        val createTableUser = """
            CREATE TABLE $TABLE_USER (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT NOT NULL UNIQUE,
                $COL_EMAIL TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL
            );
        """.trimIndent()

        db.execSQL(createTableUser)

        // 2. Crear la Tabla de Juegos (caché principal)
        val createTableGame = """
            CREATE TABLE $TABLE_GAME (
                $COL_GAME_ID INTEGER PRIMARY KEY,
                $COL_GAME_NAME TEXT NOT NULL,
                $COL_GAME_BACKGROUND_IMAGE TEXT,
                $COL_GAME_GENRES TEXT,
                $COL_GAME_RELEASED TEXT,
                $COL_GAME_DEVELOPER TEXT,
                $COL_GAME_RATING REAL
            );
        """.trimIndent()

        db.execSQL(createTableGame)

        // 3. Crear la Tabla de Juegos del Usuario (Relación Muchos a Muchos con Puntuación)
        val createTableUserGames = """
            CREATE TABLE $TABLE_USER_GAMES (
                $COL_UG_USER_ID_FK INTEGER NOT NULL,
                $COL_UG_GAME_ID_FK INTEGER NOT NULL,
                $COL_UG_RATING REAL NOT NULL,
                PRIMARY KEY ($COL_UG_USER_ID_FK, $COL_UG_GAME_ID_FK),
                
                -- Definición de llaves foráneas
                FOREIGN KEY ($COL_UG_USER_ID_FK) REFERENCES $TABLE_USER($COL_USER_ID) ON DELETE CASCADE,
                FOREIGN KEY ($COL_UG_GAME_ID_FK) REFERENCES $TABLE_GAME($COL_GAME_ID) ON DELETE CASCADE
            );
        """.trimIndent()

        db.execSQL(createTableUserGames)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // En caso de actualización, borramos las tablas y las volvemos a crear.
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER_GAMES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    // Configuración para usar llaves foráneas
    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        db?.execSQL("PRAGMA foreign_keys = ON;")
    }

    /**
     * Inserta un juego en la tabla TABLE_GAME (caché) si no existe.
     * @param apiGame El objeto Game a insertar.
     */
    fun insertGame(apiGame: Game): Boolean {
        val db = writableDatabase
        // Convierte la List<Genre> a una String única: "Action, Indie, RPG"
        val genresString = apiGame.genres.joinToString(separator = ", ") {
            it.name
        }

        //  Mapear el objeto Game a ContentValues (Añadiendo released y developer)
        val values = ContentValues().apply {
            put(COL_GAME_ID, apiGame.id)
            put(COL_GAME_NAME, apiGame.name)
            put(COL_GAME_BACKGROUND_IMAGE, apiGame.backgroundImage)
            put(COL_GAME_GENRES, genresString)
            // Añadiendo los nuevos campos
            put(COL_GAME_RELEASED, apiGame.released)
            put(COL_GAME_DEVELOPER, apiGame.developer)
            put(COL_GAME_RATING, apiGame.rating)
        }

        //  Intentar insertar el juego
        val resultado = db.insert(TABLE_GAME, null, values)
        db.close()

        return resultado != -1L
    }

    fun insertUser(User: User): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USERNAME, User.username)
            put(COL_EMAIL, User.email)
            put(COL_PASSWORD, User.password)
        }
        val resultado = db.insert(TABLE_USER, null, values)
        db.close()
        return resultado != -1L
    }

    /**
     * Verifica si un email ya existe en la base de datos (para evitar duplicados en el registro).
     * @param email El correo electrónico a verificar.
     * @return true si el correo electrónico ya existe, false si está disponible.
     */
    fun checkEmailExists(email: String): Boolean {
        val db = this.readableDatabase

        // Consulta SQL para seleccionar cualquier columna (*) donde la columna de email coincida con el email proporcionado.
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USER WHERE $COL_EMAIL = ?",
            arrayOf(email) // Argumento para la cláusula WHERE (el email del usuario)
        )

        // Si el cursor contiene más de 0 filas, significa que el email ya existe.
        val exists = cursor.count > 0

        cursor.close()
        db.close()
        return exists
    }

    /**
     * Busca un usuario por email y verifica la contraseña.
     * * NOTA: Este método asume que la 'passwordHash' ya fue hasheada
     * por la lógica de negocio (el ViewModel o Repositorio) antes de ser llamada.
     * * @param email El correo electrónico del usuario.
     * @param passwordHash La contraseña hasheada para la verificación.
     * @return El ID del usuario si las credenciales son correctas, o -1 si fallan.
     */
    fun checkUserCredentials(email: String, passwordHash: String): Int {
        val db = this.readableDatabase
        var userId = -1

        // Columnas que queremos recuperar (solo el ID del usuario)
        val columns = arrayOf(COL_USER_ID)

        // Cláusula WHERE para buscar email y contraseña
        val selection = "$COL_EMAIL = ? AND $COL_PASSWORD = ?"
        val selectionArgs = arrayOf(email, passwordHash)

        // Ejecutar la consulta SELECT
        val cursor: Cursor = db.query(
            TABLE_USER,          // Nombre de la tabla
            columns,             // Columnas a devolver
            selection,           // Cláusula WHERE
            selectionArgs,       // Argumentos para la cláusula WHERE
            null, null, null     // Group by, having, order by (no usados aquí)
        )

        // Mover el cursor al primer resultado (solo esperamos uno si el email es UNIQUE)
        if (cursor.moveToFirst()) {
            // Obtener el índice de la columna ID
            val userIdIndex = cursor.getColumnIndex(COL_USER_ID)

            // Verificar si el índice es válido y obtener el ID
            if (userIdIndex != -1) {
                userId = cursor.getInt(userIdIndex)
            }
        }

        cursor.close()
        db.close()
        return userId
    }

    /**
     * Obtiene todos los datos de un usuario por su ID.
     * @param userId El ID del usuario a buscar.
     * @return Objeto User con los datos, o null si no se encuentra.
     */
    fun getUserById(userId: Int): User? {
        val db = this.readableDatabase
        var user: User? = null

        // Columnas a devolver (todas las columnas de la tabla USER)
        val columns = arrayOf(COL_USER_ID, COL_USERNAME, COL_EMAIL, COL_PASSWORD)

        // Cláusula WHERE: buscar por ID
        val selection = "$COL_USER_ID = ?"
        val selectionArgs = arrayOf(userId.toString()) // El ID se pasa como String para la query

        val cursor: Cursor = db.query(
            TABLE_USER,
            columns,
            selection,
            selectionArgs,
            null, null, null
        )

        // Si el cursor se mueve al primer resultado (solo esperamos uno)
        if (cursor.moveToFirst()) {
            // Extracción de datos (debes manejar el índice -1 si la columna no existe)
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID))
            val username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL))
            val passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD))

            val gamesUsuario: List<UserGamesView> = getUserGames(id)

            // Creación del objeto User
            user = User(id, username, email, passwordHash, gamesUsuario)
        }

        cursor.close()
        db.close()
        return user
    }

    /**
     * Obtiene la lista de juegos y su puntuación personal para un usuario específico.
     * Realiza un JOIN entre las tablas user_games y game.
     * @param userId El ID del usuario actual.
     * @return List<UserGamesView> que contiene los detalles del juego y la puntuación del usuario.
     */
    fun getUserGames(userId: Int): List<UserGamesView> {
        val userGamesList = mutableListOf<UserGamesView>()
        val db = this.readableDatabase

        // Consulta SQL compleja para unir las dos tablas
        val query = """
            SELECT 
                G.${COL_GAME_ID}, 
                G.${COL_GAME_NAME}, 
                G.${COL_GAME_BACKGROUND_IMAGE},
                G.${COL_GAME_GENRES},
                G.${COL_GAME_RELEASED},    
                G.${COL_GAME_DEVELOPER},   
                G.${COL_GAME_RATING},
                UG.${COL_UG_RATING}
            FROM 
                $TABLE_USER_GAMES UG
            INNER JOIN 
                $TABLE_GAME G ON UG.${COL_UG_GAME_ID_FK} = G.${COL_GAME_ID}
            WHERE 
                UG.${COL_UG_USER_ID_FK} = ?
        """.trimIndent()

        val cursor: Cursor = db.rawQuery(query, arrayOf(userId.toString()))

        // Mapear los resultados del Cursor a la lista de objetos UserGameView
        if (cursor.moveToFirst()) {
            do {
                // Extracción segura de índices
                val gameId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_GAME_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COL_GAME_NAME))
                val image = cursor.getString(cursor.getColumnIndexOrThrow(COL_GAME_BACKGROUND_IMAGE))
                val genres = cursor.getString(cursor.getColumnIndexOrThrow(COL_GAME_GENRES))
                val released = cursor.getString(cursor.getColumnIndexOrThrow(COL_GAME_RELEASED)) // Nuevo
                val developer = cursor.getString(cursor.getColumnIndexOrThrow(COL_GAME_DEVELOPER)) // Nuevo
                val userRating = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_UG_RATING))
                val gameRating = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_GAME_RATING))



                // Añadir a la lista (ASUMO QUE UserGamesView AHORA TIENE LOS 7 PARÁMETROS)
                userGamesList.add(
                    UserGamesView(gameId, name, image, genres, released, developer, userRating, gameRating )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return userGamesList
    }
}