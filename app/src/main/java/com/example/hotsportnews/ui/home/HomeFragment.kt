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
    private val teamIds = listOf(38,35,42,40) // Ganti dengan ID tim yang ingin kamu tampilkan
        //,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,7172,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Uncomment this line if you need to fetch team details dynamically based on top teams
        // fetchTeamIds()

        // Panggil fetchTeamDetails dengan parameter teamIds yang sudah didefinisikan
        fetchTeamDetails(teamIds)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchTeamIds() {
        val teamIdRequest = Request.Builder()
            .url("https://divanscore.p.rapidapi.com/teams/get-top-teams") // Replace with appropriate endpoint
            .get()
            .addHeader("x-rapidapi-key", "")
            .addHeader("x-rapidapi-host", "divanscore.p.rapidapi.com")
            .build()

        client.newCall(teamIdRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val jsonData = responseBody.string()
                    val teamIds = parseTeamIdsFromJson(jsonData) // This function needs to be defined
                    fetchTeamDetails(teamIds) // Pass the team IDs to the fetchTeamDetails function
                }
            }
        })
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
                            fetchTeamLogo(teamId) { logoUrl ->
                                team.logoUrl = logoUrl
                                teamList.add(team)
                                Log.d("HomeFragment", "Team added: ${team.name}")

                                // Update the UI after all team details and logos are loaded
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
                                    }
                                }
                        } else {
                            Log.e("HomeFragment", "Team data is null for teamId: $teamId")
                        }
                    }
                }
            })
        }
    }

    private fun fetchTeamLogo(teamId: Int, onLogoFetched: (String?) -> Unit) {
        val logoRequest = Request.Builder()
            .url("https://divanscore.p.rapidapi.com/teams/get-logo?teamId=$teamId")
            .get()
            .addHeader("x-rapidapi-key", "18155b999amsh76d498974911f2fp1d7666jsn1de7bf651dc8")
            .addHeader("x-rapidapi-host", "divanscore.p.rapidapi.com")
            .build()

        client.newCall(logoRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onLogoFetched(null)
            }

            override fun onResponse(call: Call, response: Response) {
                onLogoFetched(response.body?.string())
            }
        })
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

    // Define the missing function to parse team IDs from the JSON response
    private fun parseTeamIdsFromJson(jsonData: String): List<Int> {
        val jsonObject = JSONObject(jsonData)
        val teamIds = mutableListOf<Int>()

        val teamsArray = jsonObject.optJSONArray("teams")
        teamsArray?.let {
            for (i in 0 until it.length()) {
                val team = it.getJSONObject(i)
                teamIds.add(team.getInt("id"))
            }
        }

        return teamIds
    }
}

// Define the missing function to parse team IDs from the JSON response
private fun parseTeamIdsFromJson(jsonData: String): List<Int> {
    val jsonObject = JSONObject(jsonData)
    val teamIds = mutableListOf<Int>()

    val teamsArray = jsonObject.optJSONArray("teams")
    teamsArray?.let {
        for (i in 0 until it.length()) {
            val team = it.getJSONObject(i)
            teamIds.add(team.getInt("id"))
        }
    }

    return teamIds
}

