package com.example.skillcinema.models

data class GenresAndCountriesData(
    val genres: List<GenreWithId>,
    val countries: List<CountryWithId>
)

data class CountryWithId(
    val id: Long,
    val country: String
)

data class GenreWithId(
    val id: Long,
    val genre: String
)