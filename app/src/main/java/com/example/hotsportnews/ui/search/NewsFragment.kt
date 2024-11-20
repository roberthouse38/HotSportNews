package com.example.hotsportnews.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hotsportnews.R
import com.example.hotsportnews.databinding.FragmentNewsBinding
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        newsAdapter = NewsAdapter(listOf()) { resultItem ->
            showDetail(resultItem)
        }

        binding.searchResultsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.searchResultsRecyclerView.adapter = newsAdapter

        val searchBar: EditText = binding.searchBar
        val searchButton = binding.searchButton // Referensi tombol search

        // Listener untuk tombol Search
        searchButton.setOnClickListener {
            val query = searchBar.text.toString()
            if (query.isNotEmpty()) {
                searchNews(query)
            }
        }

        return root
    }


    private fun searchNews(query: String) {
        val request = Request.Builder()
            .url("https://divanscore.p.rapidapi.com/search?q=$query&type=all&page=0")
            .get()
            .addHeader("x-rapidapi-key", "64b9c6df16mshae29f1170c9428fp1670b5jsnb81c8cdf6611")
            .addHeader("x-rapidapi-host", "divanscore.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    responseData.let { data->
                    val results = parseResults(responseData)
                    activity?.runOnUiThread {
                        newsAdapter.updateData(results)
                        }
                    }
                }else {
                    println("Response not Successful: ${response.code}")
                }
            }
        })
    }

    private fun parseResults(jsonData: String?): List<ResultItem> {
        val results = mutableListOf<ResultItem>()

        if (jsonData.isNullOrEmpty()) {
            return results
        }

        try {
            val jsonObject = JSONObject(jsonData)
            val jsonArray = jsonObject.optJSONArray("results") ?: return results

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.optJSONObject(i) ?: continue
                val entity = item.optJSONObject("entity") ?: continue
                val name = entity.optString("name", "No name available")
                val description = entity.optString("description", "No description available")

                results.add(ResultItem(name, description))
            }
        } catch (e: JSONException) {
            e.printStackTrace() // Log error for debugging
        }

        return results
    }


    private fun showDetail(resultItem: ResultItem) {
        val detailFragment = DetailFragment().apply {
            arguments = Bundle().apply {
                putString("name", resultItem.name)
                putString("description", resultItem.description)
            }
        }
        // Mengganti fragment dalam kontainer di dalam NewsFragment
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment) // Pastikan `fragment_container` ada di layout `NewsFragment`
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

