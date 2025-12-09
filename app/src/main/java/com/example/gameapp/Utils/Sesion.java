package com.example.gameapp.Utils;

import com.example.gameapp.Model.User;

public class Sesion {
    private static Sesion instance;
    private User userLoged;

    // Constructor privado para evitar instancias externas
    private Sesion() {}

    // Metodo para obtener la instancia única
    public static Sesion getInstance() {
        if (instance == null) {
            instance = new Sesion();
        }
        return instance;
    }

    // Metodo para iniciar sesión
    public void logIn(User user) {
        this.userLoged = user;
    }

    // Metodo para obtener el usuario logueado
    public User getUsuarioIniciado() {
        return userLoged;
    }


    // Metodo para cerrar sesión
    public void logOut() { //cuando se cierra sesión, se escriben los datos en el xml.

        this.userLoged = null;
    }
}
