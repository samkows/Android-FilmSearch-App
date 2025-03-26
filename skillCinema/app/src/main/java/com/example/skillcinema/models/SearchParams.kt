package com.example.skillcinema.models

data class SearchParams(
    val countryId: Long = 1L,
    val genreId: Long = 1L,
    val order: String = "RATING",
    val type: String = "ALL",
    val ratingFrom: Int = 0,
    val ratingTo: Int = 10,
    val yearFrom: Int = 1991,
    val yearTo: Int = 2030,
    val isWatched: Boolean = false
)