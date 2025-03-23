package com.example.skillcinema.presentation.filmography

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Repository
import com.example.skillcinema.domain.PersonUseCase
import com.example.skillcinema.presentation.actor.ActorLoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FilmographyViewModel(
    repository: Repository
) : ViewModel() {
    private val useCase = PersonUseCase(repository)
    private val _isLoading = MutableStateFlow<FilmographyLoadState>(FilmographyLoadState.Loading)
    val isLoading = _isLoading.asStateFlow()

    private fun loadPersonData(id: Long) {
        viewModelScope.launch {
            _isLoading.value = FilmographyLoadState.Loading
            try {
                val personData = useCase.getPersonData(id)
                val filmography = useCase.getFilmographyData(personData.films)

                _isLoading.value =
                    FilmographyLoadState.Success(personData, filmography)

            } catch (e: Exception) {
                _isLoading.value = FilmographyLoadState.Error("error ${e.message}")
                e.message?.let { Log.e("actorViewModel", it) }
            }
        }
    }

    fun loadData(id: Long) {
        loadPersonData(id)
    }
}