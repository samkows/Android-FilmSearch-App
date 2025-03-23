package com.example.skillcinema.domain

import com.example.skillcinema.data.Repository
import com.example.skillcinema.models.SearchParams
import com.example.skillcinema.models.ShortFilmDataDto
import com.example.skillcinema.models.ShortFilmDataListDto

//todo DONE
class SearchUseCase(
    private val repository: Repository
) {
    suspend fun getSearchList(
        params: SearchParams, keyword: String, page: Int
    ): ShortFilmDataListDto {
        val searchListDataFromApi = repository.getSearchList(
            params.countryId,
            params.genreId,
            params.order,
            params.type,
            params.ratingFrom,
            params.ratingTo,
            params.yearFrom,
            params.yearTo,
            keyword,
            page
        )
        val filteredByRating =
            filterByRating(searchListDataFromApi.items, params.ratingFrom, params.ratingTo)
        val filteredByWatchedStatus = filterByWatchedStatus(filteredByRating, params.isWatched)

        return ShortFilmDataListDto(
            total = searchListDataFromApi.total,
            totalPages = searchListDataFromApi.totalPages,
            items = filteredByWatchedStatus
        )
    }

    private fun filterByRating(
        list: List<ShortFilmDataDto>,
        ratingFrom: Int,
        ratingTo: Int
    ): List<ShortFilmDataDto> {
        return if (ratingFrom != 0 || ratingTo != 10) {
            list.filter { it.ratingKinopoisk != null }
        } else {
            list
        }
    }

    private suspend fun filterByWatchedStatus(
        list: List<ShortFilmDataDto>,
        isWatched: Boolean
    ): List<ShortFilmDataDto> {
        val watched = repository.getAllIsWatched()
        val watchedIds = watched.map { it.kinopoiskID }

        return if (isWatched) {
            list.filter { it.kinopoiskID in watchedIds }
        } else {
            list.filter { it.kinopoiskID !in watchedIds }
        }
    }
}