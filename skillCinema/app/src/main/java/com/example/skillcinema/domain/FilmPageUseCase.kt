package com.example.skillcinema.domain

import androidx.lifecycle.LiveData
import com.example.skillcinema.data.Repository
import com.example.skillcinema.models.FullFilmDataDto
import com.example.skillcinema.models.GalleryData
import com.example.skillcinema.models.SeasonsData
import com.example.skillcinema.models.ShortFilmDataListDto
import com.example.skillcinema.models.StaffData

//todo DONE
class FilmPageUseCase(
    private val repository: Repository
) {
    private var allStaff: List<StaffData> = emptyList()
    private var actorsList: List<StaffData> = emptyList()
    private var staffList: List<StaffData> = emptyList()


    suspend fun getActorsList(id: Long): List<StaffData> {
        if (allStaff.isEmpty()) {
            loadAndSplitStaffData(id)
        }
        return actorsList
    }

    suspend fun getStaffList(id: Long): List<StaffData> {
        if (allStaff.isEmpty()) {
            loadAndSplitStaffData(id)
        }
        return staffList
    }

    private suspend fun loadAndSplitStaffData(id: Long) {
        allStaff = repository.getStaffsFromApi(id)
        filterStaffData(allStaff)
    }

    private fun filterStaffData(inputList: List<StaffData>) {
        actorsList = inputList.filter {
            it.professionKey == "ACTOR"
        }
        staffList = inputList.filter {
            it.professionKey != "ACTOR"
        }
    }

    suspend fun getFilmData(id: Long): FullFilmDataDto {
        return repository.getFilmDataById(id)
    }

    fun getFullFilmLiveData(id: Long): LiveData<FullFilmDataDto> {
        return repository.getFullFilmLiveData(id)
    }

    suspend fun getGalleryData(id: Long): GalleryData {
        return repository.getGalleryData(id)
    }

    suspend fun getSeasonsData(serialId: Long): SeasonsData {
        return repository.getSeasonsData(serialId)
    }

    suspend fun getSimilarFilmsData(id: Long): ShortFilmDataListDto {
        val similarFilms = repository.getSimilarFilms(id)
        similarFilms.items.forEach {
            val filmData = repository.getFilmDataById(it.kinopoiskID)
            it.ratingKinopoisk = filmData.ratingKinopoisk
            it.genres = filmData.genres
            it.isWatched = filmData.isWatched
        }
        return similarFilms
    }
}