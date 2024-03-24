package com.example.project8

import androidx.recyclerview.widget.DiffUtil

class MovieDiffItemCallBack : DiffUtil.ItemCallback<OmdbMovies>() {
    override fun areItemsTheSame(oldItem: OmdbMovies, newItem: OmdbMovies): Boolean {
        return oldItem.Title == newItem.Title
    }

    override fun areContentsTheSame(oldItem: OmdbMovies, newItem: OmdbMovies): Boolean {
        return oldItem == newItem
    }
}
