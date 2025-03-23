package com.example.skillcinema.presentation.gallery

import androidx.paging.PagingData
import com.example.skillcinema.models.GalleryData
import com.example.skillcinema.models.GalleryItem
import com.example.skillcinema.models.GalleryTypes
import kotlinx.coroutines.flow.Flow

sealed class GalleryLoadState {
        data object Loading : GalleryLoadState()
        data class Error(val message: String?) : GalleryLoadState()
        data class Success(
            val galleryDataByTypes : ArrayList<Pair<GalleryTypes, Flow<PagingData<GalleryItem>>>>
        ) : GalleryLoadState()
}