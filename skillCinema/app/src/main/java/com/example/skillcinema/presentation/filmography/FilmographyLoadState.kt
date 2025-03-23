package com.example.skillcinema.presentation.filmography

import com.example.skillcinema.models.PersonData
import com.example.skillcinema.models.ProfessionKeys

sealed class FilmographyLoadState {
    data object Loading : FilmographyLoadState()
    data class Error(val message: String?) : FilmographyLoadState()
    data class Success(
        val personData: PersonData,
        val filmography: ArrayList<ProfessionKeys>
    ) : FilmographyLoadState()
}