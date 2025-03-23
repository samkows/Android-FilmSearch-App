package com.example.skillcinema.presentation.filmpage

import com.example.skillcinema.models.FullFilmDataDto
import com.example.skillcinema.models.GalleryData
import com.example.skillcinema.models.ShortFilmDataListDto
import com.example.skillcinema.models.StaffData

//todo DONE
sealed class FilmPageLoadState {
    data object Loading : FilmPageLoadState()
    data class Error(val throwable: Throwable?) : FilmPageLoadState()
    data class Success(
        val filmData: FullFilmDataDto,
        val actors: List<StaffData>,
        val staffs: List<StaffData>,
        val galleryData: GalleryData,
        val similarFilms: ShortFilmDataListDto,
        val seasonsQuantity: Long,
        val episodesQuantity: Long
    ) : FilmPageLoadState()
}