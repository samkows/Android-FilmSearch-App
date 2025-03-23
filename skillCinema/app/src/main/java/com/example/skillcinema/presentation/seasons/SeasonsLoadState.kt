package com.example.skillcinema.presentation.seasons

import com.example.skillcinema.models.SeasonsData

sealed class SeasonsLoadState {
    data object Loading : SeasonsLoadState()
    data class Error(val message: String?) : SeasonsLoadState()
    data class Success(
        val seasonsData: SeasonsData
    ) : SeasonsLoadState()
}