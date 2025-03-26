package com.example.skillcinema.models

data class SettingsUiState(
    val filters: SearchParams,
    val countries: List<CountryWithId>,
    val genres: List<GenreWithId>
)