package com.example.project8

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.project8.databinding.MovieItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MoviesItemAdapter(val context: Context) :
    ListAdapter<OmdbMovies, MoviesItemAdapter.ItemViewHolder>(MovieDiffItemCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, context)
    }

    class ItemViewHolder(val binding: MovieItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun inflateFrom(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MovieItemBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }

        // Bind iterates through each Movie item in the initial list of movies and uses the call back
        // function to access their individual details and the use that info to set those details for view
        @SuppressLint("SetTextI18n")
        fun bind(movie: OmdbMovies, context: Context) {
            fetchMovieDetails(movie.Title) { movieDetails ->
                Log.i("Specific Movie Details", movieDetails.toString())
                // Handle the movie details here
                if (movieDetails != null) {
                    // setting all needed values to their expected items
                    binding.movieName.text = movieDetails.title
                    binding.runTime.text = "Runtime: ${movieDetails.runtime}"
                    binding.releaseYear.text = "Released in ${movieDetails.year}"
                    if(movieDetails.ratings[0] != null){
                        binding.rating.text = "Rating: ${(movieDetails.ratings[0]).toString().substring(
                            (movieDetails.ratings[0]).toString().indexOf("value=") + 6,
                            (movieDetails.ratings[0]).toString().length - 1)
                        }"
                    }
                    binding.imdbRating.text = "IMDB Rating: ${movieDetails.imdbRating}"
                    binding.genre.text = "Genre: ${movieDetails.genre}"
                    binding.link.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    binding.link.text = "https://www.imdb.com/title/${movieDetails.id}/"

                    // Load the movie poster using Glide
                    Glide.with(context).load(movieDetails.poster)
                        .apply(
                            RequestOptions().transform(
                                CenterCrop(), RoundedCorners(20)
                            )
                        )
                        .into(binding.imageView)

                    // link on click listener which takes you to link page
                    binding.link.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(binding.link.text.toString()))
                        context.startActivity(intent)
                    }

                    // share button onclick for sharing intent
                    binding.shareButton.setOnClickListener{
                        if(movieDetails.title != null){
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "${movieDetails.title} - https://www.imdb.com/title/${movieDetails.id}/")

                            if (shareIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(Intent.createChooser(shareIntent, "Share URL"))
                            }
                        }
                    }
                }
            }
        }


        // Call Back function that makes a second api call to fetch specific Movie Data
        fun fetchMovieDetails(movieTitle: String, onMovieDetailsFetched: (MovieSearchResult?) -> Unit) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(OmdbService::class.java)

            val call = service.searchSpecificMovies(movieTitle, API_KEY)
            call.enqueue(object : Callback<MovieSearchResult> {
                override fun onResponse(call: Call<MovieSearchResult>, response: Response<MovieSearchResult>) {
                    val body = response.body()
                    onMovieDetailsFetched(body)
                }

                override fun onFailure(call: Call<MovieSearchResult>, t: Throwable) {
                    Log.e("MovieDetails", "Failed to fetch movie details: ${t.message}")
                    onMovieDetailsFetched(null)
                }
            })
        }
    }
}