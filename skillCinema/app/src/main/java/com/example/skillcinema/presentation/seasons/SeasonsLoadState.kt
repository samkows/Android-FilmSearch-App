package com.example.skillcinema.presentation.seasons

import com.example.skillcinema.models.SeasonsData

//todo DONE
sealed class SeasonsLoadState {
    data object Loading : SeasonsLoadState()
    data class Error(val throwable: Throwable?) : SeasonsLoadState()
    data class Success(val seasonsData: SeasonsData) : SeasonsLoadState()
}