package com.example.hotsportnews.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hotsportnews.R

class HomeAdapter(
    private val teams: List<Team>,
    private val onItemClick: (Team) -> Unit // Menambahkan parameter callback untuk klik item
) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teamLogo: ImageView = itemView.findViewById(R.id.teamLogo)
        val teamName: TextView = itemView.findViewById(R.id.eventName)
        val venueName: TextView = itemView.findViewById(R.id.venueName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int = teams.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val team = teams[position]
        holder.teamName.text = team.name
        holder.venueName.text = "${team.venueName} - ${team.venueCity}"


        // Set background color with primaryColor
        holder.itemView.setBackgroundColor(Color.parseColor(team.primaryColor))

        // Set item click listener
        holder.itemView.setOnClickListener {
            onItemClick(team) // Memanggil callback onItemClick
        }
    }
}


