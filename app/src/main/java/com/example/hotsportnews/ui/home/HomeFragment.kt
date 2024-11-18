package com.example.hotsportnews.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hotsportnews.DetailActivity
import com.example.hotsportnews.databinding.FragmentHomeBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val client = OkHttpClient()
    private val teamIds = listOf(38, 35, 42, 40) // Ganti dengan ID tim yang ingin ditampilkan

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fetchTeamDetails(teamIds)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchTeamDetails(teamIds: List<Int>) {
        val teamList = mutableListOf<Team>()

        teamIds.forEach { teamId ->
            val teamDetailRequest = Request.Builder()
                .url("https://divanscore.p.rapidapi.com/teams/detail?teamId=$teamId")
                .get()
                .addHeader("x-rapidapi-key", "64b9c6df16mshae29f1170c9428fp1670b5jsnb81c8cdf6611")
                .addHeader("x-rapidapi-host", "divanscore.p.rapidapi.com")
                .build()

            client.newCall(teamDetailRequest).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.let { responseBody ->
                        val jsonData = responseBody.string()
                        val team = parseJsonToTeam(jsonData)

                        if (team != null) {
                            teamList.add(team)
                            Log.d("HomeFragment", "Team added: ${team.name}")

                            // Update the UI after all team details are loaded
                            if (teamList.size == teamIds.size) {
                                activity?.runOnUiThread {
                                    if (isAdded && _binding != null) { // Check if the fragment is still added and binding is not null
                                        val adapter = HomeAdapter(teamList) { team ->
                                            val intent =
                                                Intent(context, DetailActivity::class.java)
                                            intent.putExtra("teamData", team)
                                            startActivity(intent)
                                        }
                                        binding.recyclerViewHome.layoutManager =
                                            LinearLayoutManager(context)
                                        binding.recyclerViewHome.adapter = adapter
                                    }
                                }
                            } else {
                                Log.d("HomeFragment", "Not all teams are loaded yet")
                            }
                        } else {
                            Log.e("HomeFragment", "Team data is null for teamId: $teamId")
                        }
                    }
                }
            })
        }
    }

    private fun parseJsonToTeam(jsonData: String): Team? {
        val jsonObject = JSONObject(jsonData)

        // Periksa apakah JSON berisi key "team"
        if (!jsonObject.has("team")) {
            // Log error atau berikan nilai null jika "team" tidak ada
            return null
        }

        val teamObject = jsonObject.getJSONObject("team")

        // Parsing informasi tambahan seperti logoUrl dan venue
        val venue = teamObject.optJSONObject("venue") ?: JSONObject()
        val venueName = venue.optString("name", "Unknown Venue")
        val venueCity = venue.optJSONObject("city")?.optString("name", "Unknown City") ?: "Unknown City"

        val teamColors = teamObject.optJSONObject("teamColors") ?: JSONObject()
        val primaryColor = teamColors.optString("primary", "#000000")
        val secondaryColor = teamColors.optString("secondary", "#FFFFFF")

        val logoUrl = teamObject.optString("logoUrl", null)

        return Team(
            id = teamObject.getInt("id"),
            name = teamObject.getString("name"),
            slug = teamObject.getString("slug"),
            fullName = teamObject.getString("fullName"),
            primaryColor = primaryColor,
            venueName = venueName,
            venueCity = venueCity,
            venueCapacity = venue.optInt("capacity", 0),
            logoUrl = logoUrl
        )
    }
}
