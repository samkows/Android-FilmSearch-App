package com.example.skillcinema.data

import androidx.lifecycle.LiveData
import com.example.skillcinema.models.FullFilmDataDto
import com.example.skillcinema.models.GalleryData
import com.example.skillcinema.models.GenresAndCountriesData
import com.example.skillcinema.models.PersonData
import com.example.skillcinema.models.SeasonsData
import com.example.skillcinema.models.ShortFilmDataListDto
import com.example.skillcinema.models.StaffData
import com.example.skillcinema.models.collections.UserCollection
import com.example.skillcinema.models.collections.UserCollectionWithFilms
import com.example.skillcinema.models.collections.UserCollectionsFilmsCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class Repository(
    private val dao: AppDatabaseDao,
    private val networkingApi: SkillCinemaApi.NetworkingApi
) {
    // NETWORKING
    // private val networkingApi = SkillCinemaApi.RetrofitInstance.networkingApi
    //todo hz
    val countriesAndGenres: Flow<GenresAndCountriesData> = flow {
        val data = getGenresAndCountries()
        emit(data)
    }

    suspend fun getPremieres(year: Int, month: String): ShortFilmDataListDto {
        return networkingApi.getPremieres(year, month)
    }

    suspend fun getPopularFilms(page: Int): ShortFilmDataListDto {
        return networkingApi.getPopularFilms(page)
    }

    suspend fun getTopMovies(page: Int): ShortFilmDataListDto {
        return networkingApi.getTopMovies(page)
    }

    suspend fun getSerials(page: Int): ShortFilmDataListDto {
        return networkingApi.getSerials(page)
    }

    suspend fun getDynamicList(country: Long, genre: Long, page: Int = 1): ShortFilmDataListDto {
        return networkingApi.getDynamicList(country, genre, page)
    }

    suspend fun getStaffsFromApi(filmId: Long): List<StaffData> {
        return networkingApi.getStaffsFromFilm(filmId)
    }

    suspend fun getGalleryData(filmId: Long): GalleryData {
        return networkingApi.getGalleryData(filmId)
    }

    suspend fun getGalleryDataByType(filmId: Long, type: String, page: Int): GalleryData {
        return networkingApi.getGalleryDataByType(filmId, type, page)
    }

    suspend fun getSimilarFilms(filmId: Long): ShortFilmDataListDto {
        return networkingApi.getSimilarFilms(filmId)
    }

    suspend fun getGenresAndCountries(): GenresAndCountriesData {
        return networkingApi.getGenresAndCountries()
    }

    suspend fun getPersonData(id: Long): PersonData {
        return networkingApi.getPersonData(id)
    }

    suspend fun getSeasonsData(id: Long): SeasonsData {
        return networkingApi.getSeasonsData(id)
    }

    suspend fun getSearchList(
        country: Long,
        genre: Long,
        order: String,
        type: String,
        ratingFrom: Int,
        ratingTo: Int,
        yearFrom: Int,
        yearTo: Int,
        keyword: String,
        page: Int
    ): ShortFilmDataListDto {
        return networkingApi.getSearchList(
            country,
            genre,
            order,
            type,
            ratingFrom,
            ratingTo,
            yearFrom,
            yearTo,
            keyword,
            page
        )
    }

    suspend fun getFilmDataById(id: Long): FullFilmDataDto {
        val data = getFilmDataFromDataBase(id)
        if (data != null) {
            return data
        } else {
            val dataFromApi = networkingApi.getFilmById(id)
            dao.insertFullFilmData(dataFromApi)
            return dataFromApi
        }
    }


    // DATABASE
    private suspend fun getFilmDataFromDataBase(filmId: Long): FullFilmDataDto? {
        return dao.getFullFilmData(filmId)
    }

     fun getFullFilmLiveData(id: Long): LiveData<FullFilmDataDto> {
        return dao.getFullFilmLiveData(id)
    }

//    fun getLiveDataList(ids: List<Long>): LiveData<List<FullFilmDataDto?>> {
//        return dao.getLiveDataList(ids)
//    }

//    fun getFilmWithUserCollections(id: Long): LiveData<FilmWithUserCollections>? {
//        return dao.getFilmWithUserCollections(id)
//    }

    // IS WATCHED APP COLLECTION
    fun getAllIsWatchedLiveData(): LiveData<List<FullFilmDataDto>> {
        return dao.getAllIsWatchedLiveData()
    }

    suspend fun getAllIsWatched(): List<FullFilmDataDto> {
        return dao.getAllIsWatched()
    }

//    suspend fun getAllIsNotWatched(): List<FullFilmDataDto> {
//        return dao.getAllIsNotWatched()
//    }

    fun getIsWatchedFilmLiveData(id: Long): LiveData<Boolean> {
        return dao.getIsWatchedFilmLiveData(id)
    }

    suspend fun updateIsWatchedFilm(id: Long, isWatched: Boolean) {
        dao.updateIsWatchedFilm(id, isWatched)
    }

    // IS FAVORITE APP COLLECTION
    fun getAllIsFavoriteLiveData(): LiveData<List<FullFilmDataDto>> {
        return dao.getAllIsFavoriteLiveData()
    }

    suspend fun getAllIsFavorite(): List<FullFilmDataDto> {
        return dao.getAllIsFavorite()
    }

    fun getIsFavoriteFilmLiveData(id: Long): LiveData<Boolean> {
        return dao.getIsFavoriteFilmLiveData(id)
    }

    suspend fun updateIsFavoriteFilm(id: Long, isFavorite: Boolean) {
        dao.updateIsFavoriteFilm(id, isFavorite)
    }

    // IS WANT TO WATCH APP COLLECTION
    fun getAllIsWantToWatchLiveData(): LiveData<List<FullFilmDataDto>> {
        return dao.getAllIsWantToWatchLiveData()
    }

    suspend fun getAllIsWantToWatch(): List<FullFilmDataDto> {
        return dao.getAllIsWantToWatch()
    }

    fun getIsWantToWatchFilmLiveData(id: Long): LiveData<Boolean> {
        return dao.getIsWantToWatchFilmLiveData(id)
    }

    suspend fun updateIsWantToWatchFilm(id: Long, isWantToWatch: Boolean) {
        dao.updateIsWantToWatchFilm(id, isWantToWatch)
    }

    // IS IT WAS INTERESTING APP COLLECTION
    fun getAllIsItWasInterestingLiveData(): LiveData<List<FullFilmDataDto>> {
        return dao.getAllIsItWasInterestingLiveData()
    }

    suspend fun getAllIsItWasInteresting(): List<FullFilmDataDto> {
        return dao.getAllIsItWasInteresting()
    }

    suspend fun updateIsItWasInterestingFilm(id: Long, isItWasInteresting: Boolean) {
        dao.updateIsItWasInterestingFilm(id, isItWasInteresting)
    }

    // USER COLLECTIONS
    fun getAllUserCollectionsLiveData(): LiveData<List<UserCollectionWithFilms>>? {
        return dao.getAllUserCollectionsLiveData()
    }

    suspend fun getUserCollectionWithFilms(id: Long): UserCollectionWithFilms? {
        return dao.getUserCollectionWithFilms(id)
    }

//    suspend fun getUserCollections(): List<UserCollection> {
//        return dao.getUserCollections()
//    }

    suspend fun insertUserCollection(userCollection: UserCollection): Long {
        return dao.insertUserCollection(userCollection)
    }

    suspend fun deleteUserCollection(userCollection: UserCollection) {
        dao.deleteUserCollection(userCollection)
    }

    suspend fun insertInUserCollection(userCollectionsFilmsCrossRef: UserCollectionsFilmsCrossRef) {
        dao.insertInUserCollection(userCollectionsFilmsCrossRef)
    }

    suspend fun deleteFromUserCollection(userCollectionsFilmsCrossRef: UserCollectionsFilmsCrossRef) {
        dao.deleteFromUserCollection(userCollectionsFilmsCrossRef)
    }
}