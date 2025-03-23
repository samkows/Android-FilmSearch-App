package com.example.skillcinema.presentation.home

import com.example.skillcinema.models.DynamicListIds
import com.example.skillcinema.models.ShortFilmDataListDto

//todo DONE
sealed class HomeLoadState {
    data object Loading : HomeLoadState()
    data class Error(val throwable: Throwable?) : HomeLoadState()
    data class Success(
        val premieres: ShortFilmDataListDto,
        val popular: ShortFilmDataListDto,
        val top250: ShortFilmDataListDto,
        val serials: ShortFilmDataListDto,
        val firstDynamic: ShortFilmDataListDto,
        val secondDynamic: ShortFilmDataListDto,
        val firstDynamicIds: DynamicListIds,
        val secondDynamicIds: DynamicListIds
    ) : HomeLoadState()
}