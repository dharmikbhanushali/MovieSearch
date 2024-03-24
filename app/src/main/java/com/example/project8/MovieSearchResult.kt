package com.example.project8

import com.google.gson.annotations.SerializedName

data class MovieSearchResult(
    @SerializedName("Title") val title: String,
    @SerializedName("Year") val year: String,
    @SerializedName("Runtime") val runtime: String,
    @SerializedName("Ratings") val ratings: List<Rating>,
    @SerializedName("imdbRating") val imdbRating: String,
    @SerializedName("Genre") val genre: String,
    @SerializedName("imdbID") val id: String,
    @SerializedName("Poster") val poster: String
)

data class Rating(
    @SerializedName("Source") val source: String,
    @SerializedName("Value") val value: String
)