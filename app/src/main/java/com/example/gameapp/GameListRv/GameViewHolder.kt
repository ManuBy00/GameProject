package com.example.gameapp.GameListRv


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.gameapp.Model.Game
import com.example.gameapp.R

class GameViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageViewGameBackground: ImageView = itemView.findViewById(R.id.ivGameBackground)
    val tvGameName: TextView = itemView.findViewById(R.id.tvGameName)

    fun bind(game: Game){
        tvGameName.text= game.name

        // Carga la imagen desde la URL usando Coil.
        imageViewGameBackground.load(game.backgroundImage) {
            // animación al cargar la imagen
            crossfade(true)
            //imagne por defecto mientras carga la real
            placeholder(R.drawable.ic_launcher_background)
            //imagen por defecto en caso de error
            error(R.drawable.ic_launcher_background)
        }
    }

}