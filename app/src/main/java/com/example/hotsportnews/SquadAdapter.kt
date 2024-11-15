package com.example.hotsportnews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SquadAdapter(private val playerList: List<Player>) : RecyclerView.Adapter<SquadAdapter.PlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = playerList[position]
        holder.bind(player)
    }

    override fun getItemCount(): Int = playerList.size

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.playerNameTextView)
        private val positionTextView: TextView = itemView.findViewById(R.id.playerPositionTextView)
        private val jerseyNumberTextView: TextView = itemView.findViewById(R.id.playerJerseyNumberTextView)
        private val heightTextView: TextView = itemView.findViewById(R.id.playerHeightTextView)
        private val countryTextView: TextView = itemView.findViewById(R.id.playerCountryTextView)
        private val marketValueTextView: TextView = itemView.findViewById(R.id.playerMarketValueTextView)

        fun bind(player: Player) {
            nameTextView.text = player.name
            positionTextView.text = player.position
            jerseyNumberTextView.text = "Jersey #: ${player.jerseyNumber}"
            heightTextView.text = "Height: ${player.height} cm"
            countryTextView.text = "Country: ${player.country}"
            marketValueTextView.text = "Market Value: €${player.marketValue}"
        }
    }
}
