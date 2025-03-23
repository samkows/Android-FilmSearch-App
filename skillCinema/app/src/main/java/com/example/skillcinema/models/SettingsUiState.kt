package com.example.skillcinema.models

//todo DONE
data class SettingsUiState(
    val filters: SearchParams,
    val countries: List<CountryWithId>,
    val genres: List<GenreWithId>
)
