package com.example.skillcinema.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Repository
import com.example.skillcinema.domain.CollectionsUseCase
import com.example.skillcinema.models.collections.UserCollection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    val repository: Repository
) : ViewModel() {

    private val useCase = CollectionsUseCase(repository)

    private val _isLoading = MutableStateFlow<ProfileLoadState>(ProfileLoadState.Loading)
    val isLoading = _isLoading.asStateFlow()


    init {
        viewModelScope.launch {
            _isLoading.value = ProfileLoadState.Loading
            try {
                val userCollectionsLiveData = useCase.getAllUserCollections()
                val isWatched = useCase.getAllIsWatchedLiveData()
                val isFavorite = useCase.getAllIsFavoriteLiveData()
                val isWantToWatch = useCase.getAllIsWantToWatchLiveData()
                val isItWasInteresting = useCase.getAllIsItWasInterestingLiveData()

                _isLoading.value =
                    ProfileLoadState.Success(
                        userCollectionsLiveData,
                        isWatched,
                        isFavorite,
                        isWantToWatch,
                        isItWasInteresting
                    )

            } catch (e: Exception) {
                _isLoading.value = ProfileLoadState.Error(e)
            }
        }
    }

    fun createUserCollection(name: String) {
        viewModelScope.launch {
            useCase.createUserCollection(name)
        }
    }

    fun clearItWasInterestingHistory() {
        viewModelScope.launch {
            useCase.getAllIsItWasInteresting().forEach {
                useCase.updateIsItWasInterestingFilm(it.kinopoiskID, false)
            }
        }
    }

    fun deleteUserCollection(userCollection: UserCollection) {
        viewModelScope.launch {
            useCase.deleteUserCollection(userCollection)
        }
    }
}