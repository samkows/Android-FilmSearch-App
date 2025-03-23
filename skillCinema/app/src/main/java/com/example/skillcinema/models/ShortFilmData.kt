package com.example.skillcinema.models


interface ShortFilmData {
    val kinopoiskID: Long
    val nameRu: String?
    val nameEn: String?
    val nameOriginal: String?
    val genres: List<Genre>?
    val countries: List<Country>
    val ratingKinopoisk: Double?
    val posterURL: String?
    val posterURLPreview: String?

    //   val appCollections: AppCollections
    var isFavorite: Boolean
    var isWantToWatch: Boolean
    var isWatched: Boolean
    var isItWasInteresting: Boolean
}

data class Genre(
    val genre: String?
)

data class Country(
    val country: String?
)