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
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class DetailActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val team = intent.getParcelableExtra<Team>("teamData")
        team?.let {
            findViewById<TextView>(R.id.teamNameTextView).text = it.name
            fetchTeamDetails(it.id)
            fetchSquadDetails(it.id)
        }
    }

    private fun fetchTeamDetails(id: Int) {
        Log.d("DetailActivity", "Fetching details for team ID: $id")
    }

    private fun fetchSquadDetails(teamId: Int) {
        val request = Request.Builder()
            .url("https://divanscore.p.rapidapi.com/teams/get-squad?teamId=$teamId")
            .get()
            .addHeader("x-rapidapi-key", "64b9c6df16mshae29f1170c9428fp1670b5jsnb81c8cdf6611")
            .addHeader("x-rapidapi-host", "divanscore.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("DetailActivity", "Error fetching squad details: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                response.body?.string()?.let { responseData ->
                    Log.d("DetailActivity", "Response data: $responseData")  // Debugging output
                    try {
                        val jsonObject = JSONObject(responseData)
                        if (jsonObject.has("players")) {
                            val playersArray = jsonObject.getJSONArray("players")

                            val playerList = mutableListOf<Player>()

                            for (i in 0 until playersArray.length()) {
                                val playerObject = playersArray.getJSONObject(i)
                                if (playerObject.has("player")) {
                                    val player = playerObject.getJSONObject("player")
                                    val playerName = player.optString("name", "Unknown Player")
                                    val position = player.optString("position", "Unknown Position")
                                    val jerseyNumber = player.optInt("jerseyNumber", 0)
                                    val height = player.optInt("height", 0) // Gunakan default 0 jika height tidak ada
                                    val country = player.getJSONObject("country").getString("name")
                                    val marketValue = player.optInt("proposedMarketValue", 0)

                                    // Menambahkan player ke dalam list
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
                                } else {
                                    Log.e("DetailActivity", "No 'player' object found at index $i")
                                }
                            }

                            runOnUiThread {
                                val recyclerView =
                                    findViewById<RecyclerView>(R.id.squadRecyclerView)
                                recyclerView.layoutManager =
                                    LinearLayoutManager(this@DetailActivity)
                                recyclerView.adapter = SquadAdapter(playerList)
                            }
                        } else {
                            Log.e("DetailActivity", "'players' array not found in JSON")
                        }
                    } catch (e: Exception) {
                        Log.e("DetailActivity", "Error parsing squad JSON: ${e.message}")
                    }
                }
            }
        })
    }
}
