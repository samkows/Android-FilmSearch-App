package com.example.skillcinema.presentation.actor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Repository
import com.example.skillcinema.domain.PersonUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActorViewModel(
    repository: Repository
) : ViewModel() {
    private val useCase = PersonUseCase(repository)

    private val _isLoading = MutableStateFlow<ActorLoadState>(ActorLoadState.Loading)
    val isLoading = _isLoading.asStateFlow()

    private fun loadPersonData(id: Long) {
        viewModelScope.launch {
            _isLoading.value = ActorLoadState.Loading
            try {
                val personData = useCase.getPersonData(id)
                val theBestFilms = useCase.getTheBestFilmsData(personData.films)

                _isLoading.value =
                    ActorLoadState.Success(personData, theBestFilms)

            } catch (e: Exception) {
                _isLoading.value = ActorLoadState.Error("error ${e.message}")
                e.message?.let { Log.e("actorViewModel", it) }
            }
        }
    }

    fun loadData(id: Long) {
        loadPersonData(id)
    }
}