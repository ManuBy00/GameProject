package com.example.gameapp.GameListRv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.gameapp.Model.Game
import com.example.gameapp.R


class GameAdapter : ListAdapter<Game, GameViewHolder>(GameDiffCallback()){
    // onCreateViewHolder: Infla el layout del elemento
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    // onBindViewHolder: Vincula los datos con las vistas del ViewHolder
    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = getItem(position)
        holder.bind(game)
    }


    // DiffUtil.Callback: Utilizado por ListAdapter para comparar elementos de forma eficiente
    class GameDiffCallback : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem == newItem
        }
    }
}