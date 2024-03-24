package com.example.project8

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal const val BASE_URL = "https://www.omdbapi.com/"
internal const val API_KEY = "7a22a494"

class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"

    private lateinit var adapter: MoviesItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = MoviesItemAdapter(this)
        val rvMovies = findViewById<RecyclerView>(R.id.rvMovies)
        rvMovies.layoutManager = LinearLayoutManager(this)
        rvMovies.adapter = adapter

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(OmdbService::class.java)

        val searchInput = findViewById<EditText>(R.id.searchInput)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val feedbackButton = findViewById<Button>(R.id.feedbackButton)

        // on click for search button that uses retrofit to request and search movie based on link, api_key, and movie name
        searchButton.setOnClickListener {
            if (searchInput.text.isNotEmpty()) {
                val call = service.searchMovies(searchInput.text.toString(), API_KEY)

                call.enqueue(object : Callback<MovieSearchResponse> {
                    override fun onResponse(call: Call<MovieSearchResponse>, response: Response<MovieSearchResponse>) {
                        Log.i(tag, "onResponse $response")
                        Log.i(tag, "onResponse ${response.body()?.toString()}")
                        val body = response.body()
                        if (body == null) {
                            Log.w(tag, "Did not receive valid response body from Yelp API... exiting")
                            return
                        }
                        adapter.submitList(body.Search) // Submit a list containing a single movie result
                    }

                    override fun onFailure(call: Call<MovieSearchResponse>, t: Throwable) {
                        Log.i(tag, "onFailure $t")
                    }
                })
            }
        }

        // intent for emailing feedback
        feedbackButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:dbhanus@iu.edu")
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
            startActivity(emailIntent)
        }
    }
}
