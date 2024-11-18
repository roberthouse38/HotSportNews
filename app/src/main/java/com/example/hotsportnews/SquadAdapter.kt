package com.example.hotsportnews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SquadAdapter(private val players: List<Player>) :
    RecyclerView.Adapter<SquadAdapter.PlayerViewHolder>() {

    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerName: TextView = view.findViewById(R.id.playerNameTextView)
        val playerPosition: TextView = view.findViewById(R.id.playerPositionTextView)
        val playerJerseyNumber: TextView = view.findViewById(R.id.playerJerseyNumberTextView)
        val playerHeight: TextView = view.findViewById(R.id.playerHeightTextView)
        val playerCountry: TextView = view.findViewById(R.id.playerCountryTextView)
        val playerMarketValue: TextView = view.findViewById(R.id.playerMarketValueTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        holder.playerName.text = player.name
        holder.playerPosition.text = "Position: ${player.position}"
        holder.playerJerseyNumber.text = "Jersey Number: ${player.jerseyNumber}"
        holder.playerHeight.text = "Height: ${player.height} cm"
        holder.playerCountry.text = "Country: ${player.country}"
        holder.playerMarketValue.text = "Market Value: ${player.marketValue} USD"
    }

    override fun getItemCount() = players.size
}
