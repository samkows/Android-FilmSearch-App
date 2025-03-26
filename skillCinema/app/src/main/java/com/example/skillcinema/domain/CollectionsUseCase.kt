package com.example.skillcinema.domain

import androidx.lifecycle.LiveData
import com.example.skillcinema.data.Repository
import com.example.skillcinema.models.FullFilmDataDto
import com.example.skillcinema.models.collections.UserCollection
import com.example.skillcinema.models.collections.UserCollectionWithFilms
import com.example.skillcinema.models.collections.UserCollectionsFilmsCrossRef

class CollectionsUseCase(
    private val repository: Repository
) {

    // IS WATCHED APP COLLECTION
    fun getAllIsWatchedLiveData(): LiveData<List<FullFilmDataDto>> {
        return repository.getAllIsWatchedLiveData()
    }

    suspend fun getAllIsWatched(): List<FullFilmDataDto> {
        return repository.getAllIsWatched()
    }

    suspend fun updateIsWatchedFilm(id: Long, isWatched: Boolean) {
        repository.updateIsWatchedFilm(id, isWatched)
    }

    //IS FAVORITE APP COLLECTION
    fun getAllIsFavoriteLiveData(): LiveData<List<FullFilmDataDto>> {
        return repository.getAllIsFavoriteLiveData()
    }

    suspend fun getAllIsFavorite(): List<FullFilmDataDto> {
        return repository.getAllIsFavorite()
    }

    suspend fun updateIsFavoriteFilm(id: Long, isFavorite: Boolean) {
        repository.updateIsFavoriteFilm(id, isFavorite)
    }

    // IS WANT TO WATCH APP COLLECTION
    fun getAllIsWantToWatchLiveData(): LiveData<List<FullFilmDataDto>> {
        return repository.getAllIsWantToWatchLiveData()
    }

    suspend fun getAllIsWantToWatch(): List<FullFilmDataDto> {
        return repository.getAllIsWantToWatch()
    }

    suspend fun updateIsWantToWatchFilm(id: Long, isWantToWatch: Boolean) {
        repository.updateIsWantToWatchFilm(id, isWantToWatch)
    }

    // IS IT WAS INTERESTING APP COLLECTION
    fun getAllIsItWasInterestingLiveData(): LiveData<List<FullFilmDataDto>> {
        return repository.getAllIsItWasInterestingLiveData()
    }

    suspend fun getAllIsItWasInteresting(): List<FullFilmDataDto> {
        return repository.getAllIsItWasInteresting()
    }

    suspend fun updateIsItWasInterestingFilm(id: Long, isItWasInteresting: Boolean) {
        repository.updateIsItWasInterestingFilm(id, isItWasInteresting)
    }

    //USER COLLECTIONS
    fun getAllUserCollections(): LiveData<List<UserCollectionWithFilms>>? {
        return repository.getAllUserCollectionsLiveData()
    }

    suspend fun getUserCollection(id: Long): UserCollectionWithFilms? {
        return repository.getUserCollectionWithFilms(id)
    }

    suspend fun createUserCollection(name: String): Long {
        return repository.insertUserCollection(UserCollection(userCollectionName = name))
    }

    suspend fun deleteUserCollection(userCollection: UserCollection) {
        repository.deleteUserCollection(userCollection)
    }

    suspend fun insertInUserCollection(collectionId: Long, filmId: Long) {
        val userCollectionsFilmsCrossRef = UserCollectionsFilmsCrossRef(
            collectionId = collectionId,
            filmId = filmId
        )
        repository.insertInUserCollection(userCollectionsFilmsCrossRef)
    }

    suspend fun deleteFromUserCollection(collectionId: Long, filmId: Long) {
        val userCollectionsFilmsCrossRef = UserCollectionsFilmsCrossRef(
            collectionId = collectionId,
            filmId = filmId
        )
        repository.deleteFromUserCollection(userCollectionsFilmsCrossRef)
    }
}