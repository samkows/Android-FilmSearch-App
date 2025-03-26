package com.example.skillcinema.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.skillcinema.models.FullFilmDataDto
import com.example.skillcinema.models.collections.UserCollection
import com.example.skillcinema.models.collections.UserCollectionWithFilms
import com.example.skillcinema.models.collections.UserCollectionsFilmsCrossRef

@Dao
interface AppDatabaseDao {

    // FULL FILM DATA
    @Query("SELECT * FROM full_film_data_table WHERE kinopoiskId = :id")
    suspend fun getFullFilmData(id: Long): FullFilmDataDto?

    @Query("SELECT * FROM full_film_data_table WHERE kinopoiskId = :id")
    fun getFullFilmLiveData(id: Long): LiveData<FullFilmDataDto>

    @Insert
    suspend fun insertFullFilmData(filmDataDto: FullFilmDataDto)

    @Delete
    suspend fun deleteFullFilmData(filmDataDto: FullFilmDataDto)

    @Query("DELETE FROM full_film_data_table")
    suspend fun deleteAllFullFilmData()


    // IS WATCHED APP COLLECTION
    @Query("SELECT * FROM full_film_data_table WHERE is_watched LIKE 1")
    fun getAllIsWatchedLiveData(): LiveData<List<FullFilmDataDto>>

    @Query("SELECT * FROM full_film_data_table WHERE is_watched LIKE 1")
    suspend fun getAllIsWatched(): List<FullFilmDataDto>

    @Query("SELECT is_watched FROM full_film_data_table WHERE kinopoiskId = :id")
    fun getIsWatchedFilmLiveData(id: Long): LiveData<Boolean>

    @Query("UPDATE full_film_data_table SET is_watched = :isWatched WHERE kinopoiskId = :id")
    suspend fun updateIsWatchedFilm(id: Long, isWatched: Boolean)

    // IS FAVORITE APP COLLECTION
    @Query("SELECT * FROM full_film_data_table WHERE is_favorite LIKE 1")
    fun getAllIsFavoriteLiveData(): LiveData<List<FullFilmDataDto>>

    @Query("SELECT * FROM full_film_data_table WHERE is_favorite LIKE 1")
    suspend fun getAllIsFavorite(): List<FullFilmDataDto>

    @Query("SELECT is_favorite FROM full_film_data_table WHERE kinopoiskId = :id")
    fun getIsFavoriteFilmLiveData(id: Long): LiveData<Boolean>

    @Query("UPDATE full_film_data_table SET is_favorite = :isFavorite WHERE kinopoiskId = :id")
    suspend fun updateIsFavoriteFilm(id: Long, isFavorite: Boolean)

    // IS WANT TO WATCH APP COLLECTION
    @Query("SELECT * FROM full_film_data_table WHERE is_want_to_watch LIKE 1")
    fun getAllIsWantToWatchLiveData(): LiveData<List<FullFilmDataDto>>

    @Query("SELECT * FROM full_film_data_table WHERE is_want_to_watch LIKE 1")
    suspend fun getAllIsWantToWatch(): List<FullFilmDataDto>

    @Query("SELECT is_want_to_watch FROM full_film_data_table WHERE kinopoiskId = :id")
    fun getIsWantToWatchFilmLiveData(id: Long): LiveData<Boolean>

    @Query("UPDATE full_film_data_table SET is_want_to_watch = :isWantToWatch WHERE kinopoiskId = :id")
    suspend fun updateIsWantToWatchFilm(id: Long, isWantToWatch: Boolean)

    // IS IT WAS INTERESTING APP COLLECTION
    @Query("SELECT * FROM full_film_data_table WHERE is_it_was_interesting LIKE 1")
    fun getAllIsItWasInterestingLiveData(): LiveData<List<FullFilmDataDto>>

    @Query("SELECT * FROM full_film_data_table WHERE is_it_was_interesting LIKE 1")
    suspend fun getAllIsItWasInteresting(): List<FullFilmDataDto>

    @Query("UPDATE full_film_data_table SET is_it_was_interesting = :isItWasInteresting WHERE kinopoiskId = :id")
    suspend fun updateIsItWasInterestingFilm(id: Long, isItWasInteresting: Boolean)


    // USER COLLECTIONS
    @Insert
    suspend fun insertUserCollection(userCollection: UserCollection): Long

    @Delete
    suspend fun deleteUserCollection(userCollection: UserCollection)

    @Transaction
    @Query("SELECT * FROM user_collections_table")
    fun getAllUserCollectionsLiveData(): LiveData<List<UserCollectionWithFilms>>?

    @Transaction
    @Query("SELECT * FROM user_collections_table WHERE collection_id = :id")
    suspend fun getUserCollectionWithFilms(id: Long): UserCollectionWithFilms?

    @Insert
    suspend fun insertInUserCollection(userCollectionsFilmsCrossRef: UserCollectionsFilmsCrossRef)

    @Delete
    suspend fun deleteFromUserCollection(userCollectionsFilmsCrossRef: UserCollectionsFilmsCrossRef)
}