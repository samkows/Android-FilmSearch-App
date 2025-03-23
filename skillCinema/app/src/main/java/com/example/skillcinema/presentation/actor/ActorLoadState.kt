package com.example.skillcinema.presentation.actor

import com.example.skillcinema.models.PersonData
import com.example.skillcinema.models.ShortFilmDataListDto

sealed class ActorLoadState {
    data object Loading : ActorLoadState()
    data class Error(val message: String?) : ActorLoadState()
    data class Success(
        val personData: PersonData,
        val theBestFilms: ShortFilmDataListDto
    ) : ActorLoadState()
}