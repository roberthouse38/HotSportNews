package com.example.hotsportnews

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotsportnews.ui.home.Team
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class DetailActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail) // Pastikan file XML ini benar

        val team = intent.getParcelableExtra<Team>("teamData")
        if (team != null) {
            // Gunakan ID yang benar dari layout XML
            val teamNameTextView = findViewById<TextView>(R.id.teamNameTextView)
            teamNameTextView.text = team.name

            // Fetch detail squad
            fetchSquadDetails(team.id)
        } else {
            Log.e("DetailActivity", "No team data received")
        }
    }

    private fun fetchSquadDetails(teamId: Int) {
        val request = Request.Builder()
            .url("https://divanscore.p.rapidapi.com/teams/get-squad?teamId=$teamId")
            .get()
            .addHeader("x-rapidapi-key", "64b9c6df16mshae29f1170c9428fp1670b5jsnb81c8cdf6611") // Ganti dengan API key Anda
            .addHeader("x-rapidapi-host", "divanscore.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("DetailActivity", "Error fetching squad details: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let { responseData ->
                    try {
                        val jsonObject = JSONObject(responseData)
                        val playersArray = jsonObject.optJSONArray("players")
                        val playerList = mutableListOf<Player>()

                        if (playersArray != null) {
                            for (i in 0 until playersArray.length()) {
                                val playerObject = playersArray.getJSONObject(i)
                                val player = playerObject.getJSONObject("player")
                                val playerName = player.optString("name", "Unknown")
                                val position = player.optString("position", "Unknown")
                                val jerseyNumber = player.optInt("jerseyNumber", 0)
                                val height = player.optInt("height", 0)
                                val country = player.getJSONObject("country").optString("name", "Unknown")
                                val marketValue = player.optInt("proposedMarketValue", 0)

                                playerList.add(
                                    Player(
                                        playerName,
                                        position,
                                        jerseyNumber,
                                        height,
                                        country,
                                        marketValue
                                    )
                                )
                            }
                        }

                        runOnUiThread {
                            val recyclerView = findViewById<RecyclerView>(R.id.squadRecyclerView)
                            recyclerView.layoutManager = LinearLayoutManager(this@DetailActivity)
                            recyclerView.adapter = SquadAdapter(playerList)
                        }
                    } catch (e: Exception) {
                        Log.e("DetailActivity", "Error parsing squad JSON: ${e.message}")
                    }
                }
            }
        })
    }
}
