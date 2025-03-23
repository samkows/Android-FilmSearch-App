package com.example.skillcinema.presentation.profile

import androidx.lifecycle.LiveData
import com.example.skillcinema.models.FullFilmDataDto
import com.example.skillcinema.models.collections.UserCollectionWithFilms

//todo DONE
sealed class ProfileLoadState {
    data object Loading : ProfileLoadState()
    data class Error(val throwable: Throwable?) : ProfileLoadState()
    data class Success(
        val userCollections: LiveData<List<UserCollectionWithFilms>>?,
        val isWatched: LiveData<List<FullFilmDataDto>>,
        val isFavorite: LiveData<List<FullFilmDataDto>>,
        val isWantToWatch: LiveData<List<FullFilmDataDto>>,
        val isItWasInteresting: LiveData<List<FullFilmDataDto>>
    ) : ProfileLoadState()
}