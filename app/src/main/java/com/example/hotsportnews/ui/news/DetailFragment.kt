package com.example.hotsportnews.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.hotsportnews.R

class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        val nameTextView: TextView = view.findViewById(R.id.detail_name)

        val descriptionTextView: TextView = view.findViewById(R.id.detail_description)

        val name = arguments?.getString("name") ?: "No name available"
        val description = arguments?.getString("description") ?: "No description available"

        nameTextView.text = name
        descriptionTextView.text = description

        return view
    }
}

