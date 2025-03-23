package com.example.skillcinema.presentation.listpage

import androidx.paging.PagingData
import com.example.skillcinema.models.ShortFilmData
import com.example.skillcinema.models.ShortFilmDataDto
import kotlinx.coroutines.flow.Flow

sealed class ListPageLoadState {
    data object Loading : ListPageLoadState()
    data class Error(val message: String?) : ListPageLoadState()
    data class Success(
        val data: Flow<PagingData<ShortFilmData>>,
    ) : ListPageLoadState()
}