package com.example.skillcinema.presentation.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.skillcinema.data.Repository
import com.example.skillcinema.domain.GalleryUseCase
import com.example.skillcinema.models.GalleryItem
import com.example.skillcinema.models.GalleryTypes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GalleryViewModel(
    repository: Repository
) : ViewModel() {
    private val firstPage = 1
    private val useCase = GalleryUseCase(repository)

    private val _isLoading = MutableStateFlow<GalleryLoadState>(GalleryLoadState.Loading)
    val isLoading = _isLoading.asStateFlow()

    private fun getPagingFlow(filmId: Long, type: GalleryTypes): Flow<PagingData<GalleryItem>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { GalleryPagingSource(useCase, filmId, type.name) }
        ).flow.cachedIn(viewModelScope)
    }

    private fun loadGalleryDataByAllTypes(id: Long) {
        viewModelScope.launch {
            _isLoading.value = GalleryLoadState.Loading
            try {
                val galleryDataByTypes: ArrayList<Pair<GalleryTypes, Flow<PagingData<GalleryItem>>>> =
                    ArrayList()

                GalleryTypes.entries.forEach {
                    val galleryData = useCase.getGalleryDataByType(id, it.name, firstPage)
                    it.quantity = galleryData.total
                    if (galleryData.items.isNotEmpty()) {
                        galleryDataByTypes.add(it to getPagingFlow(id, it))
                    }
                }

                _isLoading.value =
                    GalleryLoadState.Success(galleryDataByTypes)

            } catch (e: Exception) {
                _isLoading.value = GalleryLoadState.Error(e)
            }
        }
    }

    fun loadData(id: Long) {
        loadGalleryDataByAllTypes(id)
    }

    fun loadGalleryDataByOneType(id: Long, type: GalleryTypes): Flow<PagingData<GalleryItem>> {
        return getPagingFlow(id, type)
    }
}