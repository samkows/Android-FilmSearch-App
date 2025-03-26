package com.example.skillcinema.presentation.filmpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Repository
import com.example.skillcinema.domain.CollectionsUseCase
import com.example.skillcinema.domain.FilmPageUseCase
import com.example.skillcinema.models.FullFilmDataDto
import com.example.skillcinema.models.collections.UserCollectionWithFilms
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FilmPageViewModel(
    val repository: Repository
) : ViewModel() {

    private val useCase = FilmPageUseCase(repository)
    private val collectionsUseCase = CollectionsUseCase(repository)

    private val _isLoading = MutableStateFlow<FilmPageLoadState>(FilmPageLoadState.Loading)
    val isLoading = _isLoading.asStateFlow()

    lateinit var userCollectionsWithFilms: LiveData<List<UserCollectionWithFilms>>
    lateinit var isFavoriteFilmsLiveData: LiveData<List<FullFilmDataDto>>
    lateinit var isWantToWatchFilmsLiveData: LiveData<List<FullFilmDataDto>>
    lateinit var filmLiveData: LiveData<FullFilmDataDto>

    private fun loadFilmDataById(id: Long) {
        viewModelScope.launch {
            _isLoading.value = FilmPageLoadState.Loading
            try {
                val filmData = useCase.getFilmData(id)
                val actorsList = useCase.getActorsList(id)
                val staffList = useCase.getStaffList(id)
                val galleryData = useCase.getGalleryData(id)
                val similarFilms = useCase.getSimilarFilmsData(id)

                var seasonsQuantity = 0L
                var episodesQuantity = 0L

                if (filmData.serial == true) {
                    val seasonsData = useCase.getSeasonsData(id)

                    seasonsQuantity = seasonsData.total
                    episodesQuantity = seasonsData.seasons.sumOf { it.episodes.size.toLong() }
                }

                filmLiveData = useCase.getFullFilmLiveData(id)
                userCollectionsWithFilms = collectionsUseCase.getAllUserCollections()!!
                isFavoriteFilmsLiveData = collectionsUseCase.getAllIsFavoriteLiveData()
                isWantToWatchFilmsLiveData = collectionsUseCase.getAllIsWantToWatchLiveData()

                _isLoading.value =
                    FilmPageLoadState.Success(
                        filmData = filmData,
                        actors = actorsList,
                        staffs = staffList,
                        galleryData = galleryData,
                        similarFilms = similarFilms,
                        seasonsQuantity = seasonsQuantity,
                        episodesQuantity = episodesQuantity,
                    )

                markFilmAsInteresting(id)

            } catch (e: Exception) {
                _isLoading.value = FilmPageLoadState.Error(e)
            }
        }
    }

    private fun markFilmAsInteresting(filmId: Long) {
        viewModelScope.launch {
            collectionsUseCase.updateIsItWasInterestingFilm(filmId, true)
        }
    }

    fun favoriteClicked(filmId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            collectionsUseCase.updateIsFavoriteFilm(filmId, isFavorite)
        }
    }

    fun wantToWatchClicked(filmId: Long, isWantToWatch: Boolean) {
        viewModelScope.launch {
            collectionsUseCase.updateIsWantToWatchFilm(filmId, isWantToWatch)
        }
    }

    fun watchedClicked(filmId: Long, isWatched: Boolean) {
        viewModelScope.launch {
            collectionsUseCase.updateIsWatchedFilm(filmId, isWatched)
        }
    }

    fun insertInUserCollection(collectionId: Long, filmId: Long) {
        viewModelScope.launch {
            collectionsUseCase.insertInUserCollection(collectionId, filmId)
        }
    }

    fun deleteFromUserCollection(collectionId: Long, filmId: Long) {
        viewModelScope.launch {
            collectionsUseCase.deleteFromUserCollection(collectionId, filmId)
        }
    }

    fun createUserCollectionAndAddFilm(name: String, filmId: Long) {
        viewModelScope.launch {
            val collectionId = collectionsUseCase.createUserCollection(name)
            insertInUserCollection(collectionId, filmId)
        }
    }

    fun loadData(id: Long) {
        loadFilmDataById(id)
    }
}