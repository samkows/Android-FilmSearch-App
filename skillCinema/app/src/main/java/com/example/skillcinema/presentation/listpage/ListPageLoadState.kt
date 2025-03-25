package com.example.skillcinema.presentation.listpage

import androidx.paging.PagingData
import com.example.skillcinema.models.ShortFilmData
import kotlinx.coroutines.flow.Flow

// todo DONE
sealed class ListPageLoadState {
    data object Loading : ListPageLoadState()
    data class Error(val throwable: Throwable?) : ListPageLoadState()
    data class Success(
        val data: Flow<PagingData<ShortFilmData>>
    ) : ListPageLoadState()
}