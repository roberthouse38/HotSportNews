package com.example.hotsportnews.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hotsportnews.R

data class ResultItem(val name: String, val description: String)

class NewsAdapter(
    private var resultList: List<ResultItem>,
    private val itemClickListener: (ResultItem) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.result_name)
        val descriptionTextView: TextView = itemView.findViewById(R.id.result_description)

        fun bind(resultItem: ResultItem) {
            nameTextView.text = resultItem.name
            descriptionTextView.text = resultItem.description
            itemView.setOnClickListener { itemClickListener(resultItem) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(resultList[position])
    }

    override fun getItemCount(): Int = resultList.size

    fun updateData(newResults: List<ResultItem>) {
        resultList = newResults
        notifyDataSetChanged()
    }
}

